package br.com.financepro.financePro.invoice.dto;

import br.com.financepro.financePro.common.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceDetailsResponseDTO(
    UUID invoiceId,
    LocalDate closingDate,
    LocalDate expirationDate,
    InvoiceStatus status,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal pendingAmount,
    List<InvoiceInstallmentResponseDTO> installments
) {}