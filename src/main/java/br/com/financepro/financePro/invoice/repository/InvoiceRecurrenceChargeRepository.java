package br.com.financepro.financePro.invoice.repository;

import br.com.financepro.financePro.invoice.model.InvoiceRecurrenceCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceRecurrenceChargeRepository extends JpaRepository<InvoiceRecurrenceCharge, UUID> {
    List<InvoiceRecurrenceCharge> findAllByInvoiceIdOrderByScheduledDateAsc(UUID invoiceId);
    boolean existsByRecurrenceIdAndScheduledDateAndSlot(UUID recurrenceId, LocalDate scheduledDate, int slot);
    boolean existsByInvoiceIdAndRecurrenceId(UUID invoiceId, UUID recurrenceId);
}