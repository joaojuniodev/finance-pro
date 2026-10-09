package br.com.financepro.financePro.invoice.dto;

import br.com.financepro.financePro.common.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponseDTO(
    UUID id,
    BigDecimal limit,
    InvoiceStatus status,
    LocalDate closingDate,
    LocalDate expirationDate,
    Integer daysUntilExpiration
) {}