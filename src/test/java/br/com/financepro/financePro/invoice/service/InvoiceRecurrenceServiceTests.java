package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.model.InvoiceRecurrenceCharge;
import br.com.financepro.financePro.invoice.repository.InvoiceRecurrenceChargeRepository;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceRecurrenceServiceTests {
    @Test
    void schedulesOccurrencesBetweenClosingDatesAndDeductsEachCharge() {
        assertOccurrences(FrequencyType.MONTHLY, 15, null, null, List.of(LocalDate.of(2026, 4, 15)));
        assertOccurrences(FrequencyType.BIWEEKLY, 5, 15, null, List.of(LocalDate.of(2026, 4, 15), LocalDate.of(2026, 5, 5)));
        assertOccurrences(FrequencyType.YEARLY, 15, null, 4, List.of(LocalDate.of(2026, 4, 15)));
        assertOccurrences(FrequencyType.YEARLY, 15, null, 6, List.of());
        assertOccurrences(FrequencyType.MONTHLY, 10, null, null, List.of(LocalDate.of(2026, 5, 10)));
        assertOccurrences(FrequencyType.MONTHLY, 11, null, null, LocalDate.of(2026, 5, 11), List.of());
        assertOccurrences(FrequencyType.BIWEEKLY, 30, 31, null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
            List.of(LocalDate.of(2026, 2, 28), LocalDate.of(2026, 2, 28)));
    }

    private void assertOccurrences(FrequencyType frequency, int first, Integer second, Integer month, List<LocalDate> expected) {
        assertOccurrences(frequency, first, second, month, LocalDate.of(2026, 4, 1), expected);
    }

    private void assertOccurrences(FrequencyType frequency, int first, Integer second, Integer month, LocalDate next, List<LocalDate> expected) {
        assertOccurrences(frequency, first, second, month, next, LocalDate.of(2026, 5, 10), expected);
    }

    private void assertOccurrences(FrequencyType frequency, int first, Integer second, Integer month, LocalDate next, LocalDate closing, List<LocalDate> expected) {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        Recurrence recurrence = new Recurrence();
        recurrence.setId(UUID.randomUUID());
        recurrence.setWallet(wallet);
        recurrence.setStatus(RecurrenceStatus.ACTIVE);
        recurrence.setFrequencyType(frequency);
        recurrence.setDayOne(first);
        recurrence.setDayTwo(second);
        recurrence.setMonthOfTheYear(month);
        recurrence.setAmount(new BigDecimal("20.00"));
        recurrence.setDescription("Serviço");
        recurrence.setNextExecutionDate(next);
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID());
        invoice.setWallet(wallet);
        invoice.setClosingDate(closing);
        invoice.setLimit(new BigDecimal("100.00"));
        List<InvoiceRecurrenceCharge> saved = new ArrayList<>();
        InvoiceRepository invoices = proxy(InvoiceRepository.class, (name, args) -> switch (name) {
            case "findOpenByWalletForUpdate" -> List.of(invoice);
            case "findFirstByWalletIdAndClosingDateLessThanOrderByClosingDateDesc" -> Optional.empty();
            case "save" -> args[0];
            default -> throw new UnsupportedOperationException(name);
        });
        InvoiceRecurrenceChargeRepository charges = proxy(InvoiceRecurrenceChargeRepository.class, (name, args) -> switch (name) {
            case "existsByInvoiceIdAndRecurrenceId" -> !saved.isEmpty();
            case "existsByRecurrenceIdAndScheduledDateAndSlot" -> false;
            case "save" -> { saved.add((InvoiceRecurrenceCharge) args[0]); yield args[0]; }
            default -> throw new UnsupportedOperationException(name);
        });
        WalletRepository wallets = proxy(WalletRepository.class, (name, args) -> {
            if (name.equals("findByIdForUpdate")) return Optional.of(wallet);
            throw new UnsupportedOperationException(name);
        });
        RecurrenceRepository recurrences = proxy(RecurrenceRepository.class, (name, args) -> {
            if (name.equals("findActiveCardExpenses")) return List.of(recurrence);
            throw new UnsupportedOperationException(name);
        });
        InvoiceRecurrenceService service = new InvoiceRecurrenceService(invoices, charges, recurrences, wallets);
        service.addForNewInvoice(invoice);
        recurrence.setAmount(new BigDecimal("99.00"));
        service.addToOpenInvoice(recurrence);
        assertEquals(expected, saved.stream().map(InvoiceRecurrenceCharge::getScheduledDate).toList());
        assertEquals(new BigDecimal("100.00").subtract(new BigDecimal("20.00").multiply(BigDecimal.valueOf(expected.size()))), invoice.getLimit());
    }

    private interface Invocation { Object invoke(String name, Object[] args); }
    private static <T> T proxy(Class<T> type, Invocation invocation) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
            (instance, method, args) -> invocation.invoke(method.getName(), args)));
    }
}
