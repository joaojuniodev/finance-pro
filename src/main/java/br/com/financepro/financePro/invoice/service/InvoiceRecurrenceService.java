package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.model.InvoiceRecurrenceCharge;
import br.com.financepro.financePro.invoice.repository.InvoiceRecurrenceChargeRepository;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceRecurrenceService {
    private final InvoiceRepository invoices;
    private final InvoiceRecurrenceChargeRepository charges;
    private final RecurrenceRepository recurrences;
    private final WalletRepository wallets;

    public InvoiceRecurrenceService(
        InvoiceRepository invoices,
        InvoiceRecurrenceChargeRepository charges,
        RecurrenceRepository recurrences,
        WalletRepository wallets
    ) {
        this.invoices = invoices;
        this.charges = charges;
        this.recurrences = recurrences;
        this.wallets = wallets;
    }

    public void addToOpenInvoice(Recurrence recurrence) {
        if (recurrence.getStatus() != RecurrenceStatus.ACTIVE) return;
        lockCard(recurrence.getWallet().getId());
        for (Invoice invoice : invoices.findOpenByWalletForUpdate(recurrence.getWallet().getId())) {
            // An existing charge is a snapshot and must survive changes to the recurrence.
            if (!charges.existsByInvoiceIdAndRecurrenceId(invoice.getId(), recurrence.getId())) {
                addToInvoice(invoice, recurrence);
            }
        }
    }

    public void addForNewInvoice(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.PAID) return;
        lockCard(invoice.getWallet().getId());
        for (Recurrence recurrence : recurrences.findActiveCardExpenses(invoice.getWallet().getId())) {
            addToInvoice(invoice, recurrence);
        }
    }

    private void lockCard(UUID walletId) {
        wallets.findByIdForUpdate(walletId)
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado."));
    }

    private void addToInvoice(Invoice invoice, Recurrence recurrence) {
        LocalDate end = invoice.getClosingDate();
        LocalDate start = invoices
            .findFirstByWalletIdAndClosingDateLessThanOrderByClosingDateDesc(invoice.getWallet().getId(), end)
            .map(Invoice::getClosingDate)
            .orElse(end.minusMonths(1));
        for (YearMonth month = YearMonth.from(start); !month.isAfter(YearMonth.from(end)); month = month.plusMonths(1)) {
            if (recurrence.getFrequencyType() == FrequencyType.YEARLY
                && month.getMonthValue() != recurrence.getMonthOfTheYear()) continue;
            addOccurrence(invoice, recurrence, month, recurrence.getDayOne(), 1, start, end);
            if (recurrence.getFrequencyType() == FrequencyType.BIWEEKLY) {
                addOccurrence(invoice, recurrence, month, recurrence.getDayTwo(), 2, start, end);
            }
        }
    }

    private void addOccurrence(
        Invoice invoice, Recurrence recurrence, YearMonth month, int day, int slot, LocalDate start, LocalDate end
    ) {
        LocalDate date = month.atDay(Math.min(day, month.lengthOfMonth()));
        if (!date.isAfter(start) || date.isAfter(end) ||
            (recurrence.getNextExecutionDate() != null && date.isBefore(recurrence.getNextExecutionDate())) ||
            charges.existsByRecurrenceIdAndScheduledDateAndSlot(recurrence.getId(), date, slot)) return;
        InvoiceRecurrenceCharge charge = new InvoiceRecurrenceCharge();
        charge.setInvoice(invoice);
        charge.setRecurrence(recurrence);
        charge.setScheduledDate(date);
        charge.setSlot(slot);
        charge.setDescription(recurrence.getDescription());
        charge.setAmount(recurrence.getAmount());
        charges.save(charge);
        invoice.setLimit(invoice.getLimit().subtract(charge.getAmount()));
        invoices.save(invoice);
    }

    public List<InvoiceRecurrenceCharge> getCharges(UUID invoiceId) {
        return charges.findAllByInvoiceIdOrderByScheduledDateAsc(invoiceId);
    }

    public void savePaidCharges(List<InvoiceRecurrenceCharge> paidCharges) {
        for (InvoiceRecurrenceCharge charge : paidCharges) {
            Recurrence recurrence = charge.getRecurrence();
            if (recurrence == null || recurrence.getWallet() == null ||
                !recurrence.getWallet().getId().equals(charge.getInvoice().getWallet().getId())) continue;
            LocalDate closing = charge.getInvoice().getClosingDate();
            if (recurrence.getNextExecutionDate() != null && !recurrence.getNextExecutionDate().isAfter(closing)) {
                // The conditional update leaves a concurrently edited schedule intact.
                recurrences.advanceCardSchedule(
                    recurrence.getId(), recurrence.getWallet().getId(), recurrence.getNextExecutionDate(),
                    recurrence.getFrequencyType(), recurrence.getDayOne(), recurrence.getDayTwo(),
                    recurrence.getMonthOfTheYear(), LocalDate.now(), firstDateAfter(recurrence, closing)
                );
            }
        }
        charges.saveAll(paidCharges);
    }

    private LocalDate firstDateAfter(Recurrence recurrence, LocalDate date) {
        YearMonth month = YearMonth.from(date);
        for (int i = 0; i <= 12; i++, month = month.plusMonths(1)) {
            if (recurrence.getFrequencyType() == FrequencyType.YEARLY
                && month.getMonthValue() != recurrence.getMonthOfTheYear()) continue;
            LocalDate first = month.atDay(Math.min(recurrence.getDayOne(), month.lengthOfMonth()));
            if (first.isAfter(date)) return first;
            if (recurrence.getFrequencyType() == FrequencyType.BIWEEKLY) {
                LocalDate second = month.atDay(Math.min(recurrence.getDayTwo(), month.lengthOfMonth()));
                if (second.isAfter(date)) return second;
            }
        }
        throw new IllegalStateException("Não foi possível calcular a próxima recorrência.");
    }
}
