package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.invoice.dto.InvoiceDetailsResponseDTO;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.invoice.InvoiceMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardInvoiceService {

    private static final Logger log = LoggerFactory.getLogger(CreditCardInvoiceService.class.getName());

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private WalletBalanceOperations walletBalanceService;

    @Autowired
    private InvoiceMapper mapper;

    @Autowired
    private InvoiceRecurrenceService invoiceRecurrences;

    @Transactional(readOnly = true)
    public InvoiceDetailsResponseDTO getInvoiceDetails(UUID id, String username) {
        log.info("Getting Invoice Details");

        var entity = repository.findOwned(id, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));

        return mapper.toDetails(entity, getInstallmentsForBillingCycle(entity), invoiceRecurrences.getCharges(entity.getId()));
    }

    @Transactional
    public InvoiceDetailsResponseDTO payBill(UUID invoiceId, UUID paymentWalletId, String username) {

        log.info("Paying the Credit Card");

        Invoice invoice = getInvoice(invoiceId, username);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("This invoice has already been paid");
        }

        Wallet creditCard = invoice.getWallet();
        List<Installment> installments = getInstallmentsForBillingCycle(invoice);
        List<Installment> unpaidInstallments = installments.stream()
            .filter(installment -> !Boolean.TRUE.equals(installment.getPaid()))
            .toList();

        var recurringCharges = invoiceRecurrences.getCharges(invoiceId);
        var unpaidCharges = recurringCharges.stream()
            .filter(charge -> !charge.isPaid())
            .toList();

        BigDecimal pendingAmount = unpaidInstallments.stream()
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        pendingAmount = pendingAmount.add(unpaidCharges.stream()
            .map(charge -> charge.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
            debitPaymentWallet(creditCard, paymentWalletId, pendingAmount);
            unpaidInstallments.forEach(installment -> installment.setPaid(true));
            installmentRepository.saveAll(unpaidInstallments);
            unpaidCharges.forEach(charge -> charge.setPaid(true));
            invoiceRecurrences.savePaidCharges(unpaidCharges);
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setLimit(invoice.getLimit().add(pendingAmount));
        repository.save(invoice);
        createNextInvoiceIfNecessary(invoice);

        return mapper.toDetails(invoice, getInstallmentsForBillingCycle(invoice), recurringCharges);
    }

    @Transactional
    public InvoiceDetailsResponseDTO payInstallment(
        UUID invoiceId,
        UUID installmentId,
        UUID paymentWalletId,
        String username
    ) {

        log.info("Paying a portion of the Bill");

        Invoice invoice = getInvoice(invoiceId, username);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("This invoice has already been paid");
        }
        Installment installment = getInstallmentsForBillingCycle(invoice).stream()
            .filter(item -> item.getId().equals(installmentId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Installment not found in this credit card bill: " + installmentId));

        if (Boolean.TRUE.equals(installment.getPaid())) {
            throw new IllegalStateException("This installment has already been paid");
        }

        debitPaymentWallet(invoice.getWallet(), paymentWalletId, installment.getValue());
        installment.setPaid(true);
        installmentRepository.save(installment);

        invoice.setLimit(invoice.getLimit().add(installment.getValue()));
        repository.save(invoice);

        return mapper.toDetails(invoice, getInstallmentsForBillingCycle(invoice), invoiceRecurrences.getCharges(invoiceId));
    }

    private void debitPaymentWallet(Wallet creditCard, UUID paymentWalletId, BigDecimal amount) {

        log.info("Debiting Credit Card");

        Wallet paymentWallet = walletRepository.findByIdForUpdate(paymentWalletId)
            .orElseThrow(() -> new NotFoundException("Not found payment Wallet Id: " + paymentWalletId));

        if (paymentWallet.getId().equals(creditCard.getId())) {
            throw new IllegalArgumentException("Choose another wallet to pay this credit card bill");
        }

        if (!paymentWallet.getAccount().getId().equals(creditCard.getAccount().getId())) {
            throw new IllegalArgumentException("The payment wallet must belong to the same account");
        }

        if (paymentWallet.getType() == WalletType.CREDIT_CARD) {
            throw new IllegalArgumentException("A fatura deve ser paga com uma carteira de saldo.");
        }
        if (paymentWallet.getBalance() == null || paymentWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para pagar a fatura.");
        }

        walletBalanceService.debit(paymentWallet, amount, false, false);
    }

    private Invoice getInvoice(UUID invoiceId, String username) {
        Invoice owned = repository.findOwned(invoiceId, username)
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada."));
        // Lock the card before the invoice, matching recurrence creation's lock order.
        walletRepository.findByIdForUpdate(owned.getWallet().getId())
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado."));
        Invoice invoice = repository.findOwnedForUpdate(invoiceId, username)
            .orElseThrow(() -> new NotFoundException("Not found Invoice Id: " + invoiceId));

        if (invoice.getClosingDate() == null || invoice.getExpirationDate() == null) {
            throw new IllegalArgumentException("The selected Invoice is invalid");
        }

        return invoice;
    }

    private void createNextInvoiceIfNecessary(Invoice paidInvoice) {
        LocalDate nextClosingDate = paidInvoice.getClosingDate().plusMonths(1);
        LocalDate firstDayOfMonth = nextClosingDate.withDayOfMonth(1);
        LocalDate lastDayOfMonth = nextClosingDate.withDayOfMonth(nextClosingDate.lengthOfMonth());

        boolean nextCycleExists = repository.existsByWalletIdAndClosingDateBetween(
            paidInvoice.getWallet().getId(),
            firstDayOfMonth,
            lastDayOfMonth
        );

        if (!nextCycleExists) {
            Invoice next = repository.save(createNextInvoice(paidInvoice, nextClosingDate));
            invoiceRecurrences.addForNewInvoice(next);
        }
    }

    private Invoice createNextInvoice(Invoice paidInvoice, LocalDate nextClosingDate) {
        LocalDate nextExpirationDate = paidInvoice.getDaysUntilExpiration() == null
            ? paidInvoice.getExpirationDate().plusMonths(1)
            : calculateNextExpirationDate(nextClosingDate, paidInvoice.getDaysUntilExpiration());

        Invoice nextInvoice = new Invoice();
        nextInvoice.setLimit(paidInvoice.getLimit());
        nextInvoice.setStatus(InvoiceStatus.PAYING);
        nextInvoice.setClosingDate(nextClosingDate);
        nextInvoice.setDaysUntilExpiration(paidInvoice.getDaysUntilExpiration());
        nextInvoice.setWallet(paidInvoice.getWallet());
        nextInvoice.setExpirationDate(nextExpirationDate);
        return nextInvoice;
    }

    private List<Installment> getInstallmentsForBillingCycle(Invoice invoice) {
        return installmentRepository.findAllByInstallmentPlanInvoiceWalletIdAndMonthAndYear(
            invoice.getWallet().getId(),
            invoice.getClosingDate().getMonthValue(),
            invoice.getClosingDate().getYear()
        );
    }

    private LocalDate calculateNextExpirationDate(LocalDate date, Integer daysUntilExpiration) {
        return date.plusDays(daysUntilExpiration);
    }
}
