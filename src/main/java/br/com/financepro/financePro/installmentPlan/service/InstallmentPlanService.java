package br.com.financepro.financePro.installmentPlan.service;

import br.com.financepro.financePro.common.contract.ICrud;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.dto.InstallmentRequestDTO;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.installment.service.InstallmentService;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.filter.InstallmentPlanFilter;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.installmentPlan.repository.InstallmentPlanRepository;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import br.com.financepro.financePro.mapper.installmentPlan.InstallmentPlanMapper;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InstallmentPlanService implements ICrud<InstallmentPlan, InstallmentPlanResponseDTO, InstallmentPlanRequestDTO, InstallmentPlanFilter> {

    private final Logger log = LoggerFactory.getLogger(InstallmentPlanService.class.getName());

    @Autowired
    private InstallmentPlanRepository repository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private InstallmentPlanMapper mapper;

    @Autowired
    private InstallmentMapper installmentMapper;

    @Override
    public List<InstallmentPlanResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public List<InstallmentPlanResponseDTO> filter(InstallmentPlanFilter installmentPlanFilter) {
        return List.of();
    }

    @Override
    public InstallmentPlanResponseDTO getById(UUID id) {
        return null;
    }

    @Transactional
    @Override
    public InstallmentPlanResponseDTO create(InstallmentPlanRequestDTO request) {
        log.info("Creating new Installment Plan");

        LocalDate today = LocalDate.now();
        BigDecimal totalValue = calculateTheInterest(request.value(), request.fees());

        var wallet = walletRepository.findById(request.walletId())
            .orElseThrow(() -> new NotFoundException("Wallet not found"));
        
        var entity = mapper.toEntity(request);
        entity.setTotalValue(totalValue);
        entity.setStartDate(today);
        entity.setEndDate(today.plusMonths(request.times().longValue()));
        entity.setWallet(wallet);

        var plan = repository.save(entity);

        var installments = saveInstallments(today, totalValue, request.times(), plan.getId());
        plan.setInstallments(installments);

        return mapper.toResponse(plan);
    }

    private List<Installment> saveInstallments(LocalDate start, BigDecimal totalValue, BigDecimal times, UUID planId) {
        int n = times.intValue();
        BigDecimal base = totalValue.divide(times, 2, RoundingMode.HALF_UP);
        List<Installment> installments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            LocalDate dueDate = start.plusMonths(i);
            BigDecimal amount = (i == n - 1)
                ? totalValue.subtract(base.multiply(BigDecimal.valueOf(n - 1)))
                : base;

            var dto = new InstallmentRequestDTO(
                null,
                amount,
                false,
                dueDate.getMonthValue(),
                dueDate.getYear(),
                planId
            );

            installments.add(installmentRepository.save(installmentMapper.toEntity(dto)));
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
        return total.add(valueInterest);
    }
}