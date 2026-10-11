package br.com.financepro.financePro.invoice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceInstallmentResponseDTO(
    UUID id,
    UUID installmentPlanId,
    String description,
    BigDecimal value,
    Boolean paid,
    Integer month,
    Integer year
) {}