package br.com.financepro.financePro.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceRecurrenceChargeResponseDTO(UUID id, UUID recurrenceId, String description,
                                                  LocalDate scheduledDate, BigDecimal amount, boolean paid) {}
