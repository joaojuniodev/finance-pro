package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Service
public class InvoiceCalendarService {
    private final InvoiceRepository invoices;
    private final InvoiceRecurrenceService recurrences;

    public InvoiceCalendarService(InvoiceRepository invoices, InvoiceRecurrenceService recurrences) {
        this.invoices = invoices;
        this.recurrences = recurrences;
    }

    public void ensureYear(Wallet wallet, Invoice template, int year) {
        for (int month = 1; month <= 12; month++) {
            ensureMonth(wallet, template, YearMonth.of(year, month));
        }
    }

    public Invoice ensureMonth(Wallet wallet, Invoice template, YearMonth month) {
        LocalDate first = month.atDay(1);
        return invoices.findFirstByWalletIdAndClosingDateBetween(wallet.getId(), first, month.atEndOfMonth())
            .orElseGet(() -> {
                Invoice invoice = new Invoice();
                LocalDate closing = month.atDay(Math.min(template.getClosingDate().getDayOfMonth(), month.lengthOfMonth()));
                invoice.setWallet(wallet);
                invoice.setAccount(wallet.getAccount());
                invoice.setClosingDate(closing);
                int days = template.getDaysUntilExpiration() != null ? template.getDaysUntilExpiration()
                    : Math.toIntExact(ChronoUnit.DAYS.between(template.getClosingDate(), template.getExpirationDate()));
                invoice.setDaysUntilExpiration(days);
                invoice.setExpirationDate(closing.plusDays(days));
                invoice.setStatus(InvoiceStatus.PAYING);
                invoice.setLimit(template.getLimit());
                Invoice saved = invoices.save(invoice);
                wallet.getInvoices().add(saved);
                recurrences.addForNewInvoice(saved);
                return saved;
            });
    }
}
