package br.com.financepro.financePro.installmentPlan.service;

import br.com.financepro.financePro.common.contract.ICrud;
import br.com.financepro.financePro.installment.dto.InstallmentRequestDTO;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.filter.InstallmentPlanFilter;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.installmentPlan.repository.InstallmentPlanRepository;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.invoice.service.InvoiceCalendarService;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.enums.InstallmentPlanStatus;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import br.com.financepro.financePro.mapper.installmentPlan.InstallmentPlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InstallmentPlanService implements ICrud<InstallmentPlan, InstallmentPlanResponseDTO, InstallmentPlanRequestDTO, InstallmentPlanFilter> {

    private final Logger log = LoggerFactory.getLogger(InstallmentPlanService.class.getName());

    @Autowired
    private InstallmentPlanRepository repository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InstallmentPlanMapper mapper;

    @Autowired
    private InstallmentMapper installmentMapper;

    @Autowired
    private InvoiceCalendarService invoiceCalendar;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public List<InstallmentPlanResponseDTO> getAll() {
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<InstallmentPlanResponseDTO> getAllByWallet(UUID walletId, String username) {
        return repository.findAllByInvoice_Wallet_IdAndInvoice_Wallet_Account_User_UsernameOrderByStartDateDesc(walletId, username)
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    @Override
    public List<InstallmentPlanResponseDTO> filter(InstallmentPlanFilter filter) {
        return List.of();
    }

    @Override
    public InstallmentPlanResponseDTO getById(UUID id) {
        return null;
    }

    @Transactional
    @Override
    public InstallmentPlanResponseDTO create(InstallmentPlanRequestDTO request) {
        throw new UnsupportedOperationException("Usuário autenticado é obrigatório.");
    }

    @Transactional
    public InstallmentPlanResponseDTO create(InstallmentPlanRequestDTO request, String username) {
        log.info("Creating new Installment Plan");
        var anchor = invoiceRepository.findOwned(request.invoiceId(), username)
            .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada."));
        if (anchor.getClosingDate() == null || anchor.getExpirationDate() == null || anchor.getLimit() == null) {
            throw new IllegalArgumentException("Dados da fatura incompletos.");
        }
        var wallet = walletRepository.findByIdForUpdate(anchor.getWallet().getId())
            .orElseThrow(() -> new IllegalArgumentException("Cartão não encontrado."));
        if (wallet.getType() != WalletType.CREDIT_CARD || request.description() == null || request.description().isBlank()
            || request.value() == null || request.value().signum() <= 0 || request.value().scale() > 2
            || request.fees() == null || request.fees().signum() < 0 || request.times() == null
            || request.times().stripTrailingZeros().scale() > 0 || request.times().compareTo(BigDecimal.ONE) < 0
            || request.times().compareTo(BigDecimal.valueOf(120)) > 0) {
            throw new IllegalArgumentException("Dados do parcelamento inválidos.");
        }
        int count = request.times().intValueExact();
        YearMonth start = request.startMonth() == null ? YearMonth.from(anchor.getClosingDate()) : request.startMonth();
        if (start.getYear() < 1900 || start.plusMonths(count - 1L).getYear() > 2100) {
            throw new IllegalArgumentException("Ano do parcelamento inválido.");
        }
        List<Integer> paid = request.paidInstallments() == null ? List.of() : request.paidInstallments();
        if (paid.stream().anyMatch(number -> number == null || number < 1 || number > count
                || start.plusMonths(number - 1L).isAfter(YearMonth.now())) || paid.stream().distinct().count() != paid.size()) {
            throw new IllegalArgumentException("Parcelas pagas inválidas.");
        }
        YearMonth end = start.plusMonths(count - 1L);
        for (int year = start.getYear(); year <= end.getYear(); year++) invoiceCalendar.ensureYear(wallet, anchor, year);
        var firstInvoice = invoiceCalendar.ensureMonth(wallet, anchor, start);
        BigDecimal totalValue = calculateTheInterest(request.value(), request.fees());
        if (totalValue.movePointRight(2).compareTo(BigDecimal.valueOf(count)) < 0) {
            throw new IllegalArgumentException("O valor é insuficiente para a quantidade de parcelas.");
        }
        var entity = mapper.toEntity(request);
        entity.setInvoice(firstInvoice);
        entity.setAccount(wallet.getAccount());
        entity.setStatus(paid.size() == count ? InstallmentPlanStatus.PAID : InstallmentPlanStatus.PAYING);
        entity.setTotalValue(totalValue);
        entity.setStartDate(start.atDay(1));
        entity.setEndDate(end.atDay(1));
        var plan = repository.save(entity);
        var installments = saveInstallments(start, totalValue, request.times(), plan, anchor, paid);
        plan.setInstallments(installments);

        return mapper.toResponse(plan);
    }

    private List<Installment> saveInstallments(YearMonth start, BigDecimal totalValue, BigDecimal times, InstallmentPlan plan, br.com.financepro.financePro.invoice.model.Invoice anchor, List<Integer> paid) {
        int n = times.intValue();
        BigDecimal[] cents = totalValue.movePointRight(2).divideAndRemainder(BigDecimal.valueOf(n));
        List<Installment> installments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            YearMonth dueDate = start.plusMonths(i);
            BigDecimal amount = cents[0].add(i < cents[1].intValue() ? BigDecimal.ONE : BigDecimal.ZERO).movePointLeft(2);

            var dto = new InstallmentRequestDTO(
                null,
                amount,
                paid.contains(i + 1),
                dueDate.getMonthValue(),
                dueDate.getYear(),
                plan.getId()
            );
            var installment = installmentMapper.toEntity(dto);
            installment.setAccount(plan.getAccount());
            installments.add(installmentRepository.save(installment));
            var invoice = invoiceCalendar.ensureMonth(anchor.getWallet(), anchor, dueDate);
            if (!paid.contains(i + 1)) {
                if (invoice.getStatus() == InvoiceStatus.PAID) invoice.setStatus(InvoiceStatus.PAYING);
                invoice.setLimit(invoice.getLimit().subtract(amount));
                invoiceRepository.save(invoice);
            }
        }
        return installments;
    }

    @Override
    public InstallmentPlanResponseDTO update(InstallmentPlanRequestDTO request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    private BigDecimal calculateTheInterest(BigDecimal total, BigDecimal fees) {
        var valueInterest = (total.multiply(fees).divide(BigDecimal.valueOf(100)));
        return total.add(valueInterest).setScale(2, RoundingMode.HALF_UP);
    }
}
