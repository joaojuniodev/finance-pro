package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.model.InvoiceRecurrenceCharge;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.invoice.InvoiceMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceOperations;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvoicePaymentSafetyTests {
    @Test
    void doesNotExposeOrPayAnotherUsersInvoice() {
        InvoiceRepository invoices = proxy(InvoiceRepository.class, (name, args) -> {
            if (name.equals("findOwned")) return Optional.empty();
            throw new UnsupportedOperationException(name);
        });
        CreditCardInvoiceService service = new CreditCardInvoiceService();
        ReflectionTestUtils.setField(service, "repository", invoices);
        UUID id = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> service.getInvoiceDetails(id, "other"));
        assertThrows(NotFoundException.class, () -> service.payBill(id, UUID.randomUUID(), "other"));
    }

    @Test
    void paysRecurringChargesTogetherWithInstallmentsAndCreatesOnlyTheNextInvoice() {
        Account account = new Account();
        account.setId(UUID.randomUUID());
        Wallet card = new Wallet();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        Wallet payment = new Wallet();
        payment.setId(UUID.randomUUID());
        payment.setAccount(account);
        payment.setType(WalletType.CHECKING);
        payment.setBalance(new BigDecimal("100.00"));
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID());
        invoice.setStatus(InvoiceStatus.PAYING);
        invoice.setLimit(new BigDecimal("50.00"));
        invoice.setWallet(card);
        invoice.setClosingDate(LocalDate.of(2026, 11, 10));
        invoice.setExpirationDate(LocalDate.of(2026, 11, 20));
        InvoiceRecurrenceCharge charge = new InvoiceRecurrenceCharge();
        charge.setAmount(new BigDecimal("30.00"));
        charge.setDescription("Serviço");
        charge.setScheduledDate(LocalDate.of(2026, 11, 5));
        charge.setInvoice(invoice);
        InstallmentPlan plan = new InstallmentPlan();
        plan.setId(UUID.randomUUID());
        plan.setDescription("Compra");
        Installment installment = new Installment();
        installment.setValue(new BigDecimal("20.00"));
        installment.setPaid(false);
        installment.setMonth(11);
        installment.setYear(2026);
        installment.setInstallmentPlan(plan);
        java.util.concurrent.atomic.AtomicReference<BigDecimal> debit = new java.util.concurrent.atomic.AtomicReference<>();
        java.util.concurrent.atomic.AtomicInteger nextCreated = new java.util.concurrent.atomic.AtomicInteger();
        InvoiceRepository invoices = proxy(InvoiceRepository.class, (name, args) -> switch (name) {
            case "findOwned", "findOwnedForUpdate" -> Optional.of(invoice);
            case "existsByWalletIdAndClosingDateBetween" -> false;
            case "save" -> { if (args[0] != invoice) nextCreated.incrementAndGet(); yield args[0]; }
            default -> throw new UnsupportedOperationException(name);
        });
        WalletRepository wallets = proxy(WalletRepository.class, (name, args) -> {
            if (name.equals("findByIdForUpdate")) return Optional.of(payment);
            throw new UnsupportedOperationException(name);
        });
        InstallmentRepository installments = proxy(InstallmentRepository.class, (name, args) -> switch (name) {
            case "findAllByInstallmentPlanInvoiceWalletIdAndMonthAndYear" -> List.of(installment);
            case "saveAll" -> args[0];
            default -> throw new UnsupportedOperationException(name);
        });
        WalletBalanceOperations balances = proxy(WalletBalanceOperations.class, (name, args) -> {
            if (name.equals("debit")) { debit.set((BigDecimal) args[1]); return null; }
            throw new UnsupportedOperationException(name);
        });
        InvoiceRecurrenceService recurring = new InvoiceRecurrenceService(null, null, null, null) {
            @Override public List<InvoiceRecurrenceCharge> getCharges(UUID id) { return List.of(charge); }
            @Override public void savePaidCharges(List<InvoiceRecurrenceCharge> values) {}
            @Override public void addForNewInvoice(Invoice next) { assertEquals(LocalDate.of(2026, 12, 10), next.getClosingDate()); }
        };
        CreditCardInvoiceService service = new CreditCardInvoiceService();
        ReflectionTestUtils.setField(service, "repository", invoices);
        ReflectionTestUtils.setField(service, "walletRepository", wallets);
        ReflectionTestUtils.setField(service, "installmentRepository", installments);
        ReflectionTestUtils.setField(service, "walletBalanceService", balances);
        ReflectionTestUtils.setField(service, "invoiceRecurrences", recurring);
        ReflectionTestUtils.setField(service, "mapper", new InvoiceMapper());

        var result = service.payBill(invoice.getId(), payment.getId(), "owner");
        assertEquals(new BigDecimal("50.00"), debit.get());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(true, charge.isPaid());
        assertEquals(true, installment.getPaid());
        assertEquals(new BigDecimal("100.00"), invoice.getLimit());
        assertEquals(new BigDecimal("50.00"), result.paidAmount());
        assertEquals(1, nextCreated.get());
        assertThrows(IllegalStateException.class, () -> service.payBill(invoice.getId(), payment.getId(), "owner"));
        assertEquals(1, nextCreated.get());
    }

    @Test
    void rejectsPaymentWithoutEnoughBalanceBeforeChangingTheInvoice() {
        assertRejected(WalletType.CHECKING, new BigDecimal("19.99"), InsufficientBalanceException.class);
    }

    @Test
    void rejectsPayingWithAnotherCreditCard() {
        assertRejected(WalletType.CREDIT_CARD, new BigDecimal("100.00"), IllegalArgumentException.class);
    }

    private void assertRejected(WalletType paymentType, BigDecimal balance, Class<? extends RuntimeException> error) {
        Account account = new Account();
        account.setId(UUID.randomUUID());
        Wallet card = new Wallet();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        Wallet payment = new Wallet();
        payment.setId(UUID.randomUUID());
        payment.setAccount(account);
        payment.setType(paymentType);
        payment.setBalance(balance);
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID());
        invoice.setStatus(InvoiceStatus.PAYING);
        invoice.setLimit(new BigDecimal("50.00"));
        invoice.setWallet(card);
        invoice.setClosingDate(LocalDate.of(2026, 11, 10));
        invoice.setExpirationDate(LocalDate.of(2026, 11, 20));
        Installment installment = new Installment();
        installment.setValue(new BigDecimal("20.00"));
        installment.setPaid(false);

        InvoiceRepository invoices = proxy(InvoiceRepository.class, (name, args) -> {
            if (name.equals("findOwned") || name.equals("findOwnedForUpdate")) return Optional.of(invoice);
            throw new UnsupportedOperationException(name);
        });
        WalletRepository wallets = proxy(WalletRepository.class, (name, args) -> {
            if (name.equals("findByIdForUpdate")) return Optional.of(payment);
            throw new UnsupportedOperationException(name);
        });
        InstallmentRepository installments = proxy(InstallmentRepository.class, (name, args) -> {
            if (name.equals("findAllByInstallmentPlanInvoiceWalletIdAndMonthAndYear")) return List.of(installment);
            throw new UnsupportedOperationException(name);
        });
        CreditCardInvoiceService service = new CreditCardInvoiceService();
        ReflectionTestUtils.setField(service, "repository", invoices);
        ReflectionTestUtils.setField(service, "walletRepository", wallets);
        ReflectionTestUtils.setField(service, "installmentRepository", installments);
        ReflectionTestUtils.setField(service, "invoiceRecurrences", new InvoiceRecurrenceService(null, null, null, null) {
            @Override public java.util.List<br.com.financepro.financePro.invoice.model.InvoiceRecurrenceCharge> getCharges(UUID invoiceId) {
                return List.of();
            }
        });

        assertThrows(error, () -> service.payBill(invoice.getId(), payment.getId(), "owner"));
        assertEquals(InvoiceStatus.PAYING, invoice.getStatus());
        assertEquals(false, installment.getPaid());
        assertEquals(new BigDecimal("50.00"), invoice.getLimit());
    }

    private interface Invocation { Object invoke(String name, Object[] args); }

    private static <T> T proxy(Class<T> type, Invocation invocation) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
            (instance, method, args) -> invocation.invoke(method.getName(), args)));
    }
}
