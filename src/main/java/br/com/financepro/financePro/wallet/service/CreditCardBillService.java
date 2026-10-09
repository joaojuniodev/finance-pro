package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.wallet.dto.CreditCardBillInstallmentDTO;
import br.com.financepro.financePro.wallet.dto.CreditCardBillResponseDTO;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardBillService {

    private final WalletRepository walletRepository;
    private final InstallmentRepository installmentRepository;
    private final WalletBalanceOperations walletBalanceService;

    public CreditCardBillService(
        WalletRepository walletRepository,
        InstallmentRepository installmentRepository,
        WalletBalanceOperations walletBalanceService
    ) {
        this.walletRepository = walletRepository;
        this.installmentRepository = installmentRepository;
        this.walletBalanceService = walletBalanceService;
    }

    @Transactional(readOnly = true)
    public CreditCardBillResponseDTO getBill(UUID creditCardId) {
        Wallet creditCard = getCreditCard(creditCardId);
        return toResponse(creditCard, getInstallmentsForBillingCycle(creditCard));
    }

    @Transactional
    public CreditCardBillResponseDTO payInstallment(
        UUID creditCardId,
        UUID installmentId,
        UUID paymentWalletId
    ) {
        Wallet creditCard = getCreditCard(creditCardId);
        Installment installment = getInstallmentsForBillingCycle(creditCard).stream()
            .filter(item -> item.getId().equals(installmentId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Installment not found in this credit card bill: " + installmentId));

        if (Boolean.TRUE.equals(installment.getPaid())) {
            throw new IllegalStateException("This installment has already been paid");
        }

        debitPaymentWallet(creditCard, paymentWalletId, installment.getValue());
        installment.setPaid(true);
        installmentRepository.save(installment);

        return toResponse(creditCard, getInstallmentsForBillingCycle(creditCard));
    }

    @Transactional
    public CreditCardBillResponseDTO payBill(UUID creditCardId, UUID paymentWalletId) {
        Wallet creditCard = getCreditCard(creditCardId);
        List<Installment> installments = getInstallmentsForBillingCycle(creditCard);
        List<Installment> unpaidInstallments = installments.stream()
            .filter(installment -> !Boolean.TRUE.equals(installment.getPaid()))
            .toList();

        BigDecimal pendingAmount = unpaidInstallments.stream()
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
            debitPaymentWallet(creditCard, paymentWalletId, pendingAmount);
            unpaidInstallments.forEach(installment -> installment.setPaid(true));
            installmentRepository.saveAll(unpaidInstallments);
        }

        creditCard.setClosingDate(creditCard.getClosingDate().plusMonths(1));
        creditCard.setExpirationDate(creditCard.getExpirationDate().plusMonths(1));
        walletRepository.save(creditCard);

        return toResponse(creditCard, getInstallmentsForBillingCycle(creditCard));
    }

    private Wallet getCreditCard(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new NotFoundException("Not found Wallet Id: " + walletId));

        if (wallet.getType() != WalletType.CREDIT_CARD
            || wallet.getClosingDate() == null
            || wallet.getExpirationDate() == null) {
            throw new IllegalArgumentException("The selected wallet is not a configured credit card");
        }

        return wallet;
    }

    private List<Installment> getInstallmentsForBillingCycle(Wallet creditCard) {
        return installmentRepository.findAllByInstallmentPlanWalletIdAndMonthAndYear(
            creditCard.getId(),
            creditCard.getClosingDate().getMonthValue(),
            creditCard.getClosingDate().getYear()
        );
    }

    private void debitPaymentWallet(Wallet creditCard, UUID paymentWalletId, BigDecimal amount) {
        Wallet paymentWallet = walletRepository.findById(paymentWalletId)
            .orElseThrow(() -> new NotFoundException("Not found payment Wallet Id: " + paymentWalletId));

        if (paymentWallet.getId().equals(creditCard.getId())) {
            throw new IllegalArgumentException("Choose another wallet to pay this credit card bill");
        }

        if (!paymentWallet.getAccount().getId().equals(creditCard.getAccount().getId())) {
            throw new IllegalArgumentException("The payment wallet must belong to the same account");
        }

        walletBalanceService.debit(paymentWallet, amount, false, false);
    }

    private CreditCardBillResponseDTO toResponse(Wallet creditCard, List<Installment> installments) {
        BigDecimal totalAmount = installments.stream()
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidAmount = installments.stream()
            .filter(installment -> Boolean.TRUE.equals(installment.getPaid()))
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingAmount = totalAmount.subtract(paidAmount);

        return new CreditCardBillResponseDTO(
            creditCard.getId(),
            creditCard.getClosingDate(),
            creditCard.getExpirationDate(),
            getStatus(creditCard.getExpirationDate(), pendingAmount),
            totalAmount,
            paidAmount,
            pendingAmount,
            installments.stream().map(installment -> new CreditCardBillInstallmentDTO(
                installment.getId(),
                installment.getInstallmentPlan().getId(),
                installment.getInstallmentPlan().getDescription(),
                installment.getValue(),
                installment.getPaid(),
                installment.getMonth(),
                installment.getYear()
            )).toList()
        );
    }

    private String getStatus(LocalDate expirationDate, BigDecimal pendingAmount) {
        if (pendingAmount.compareTo(BigDecimal.ZERO) == 0) return "PAID";
        if (LocalDate.now().isAfter(expirationDate)) return "EXPIRED";
        if (ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) <= 3) return "DUE_SOON";
        return "PENDING";
    }
}
