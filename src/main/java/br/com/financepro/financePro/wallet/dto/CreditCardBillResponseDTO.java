package br.com.financepro.financePro.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreditCardBillResponseDTO(
    UUID walletId,
    LocalDate closingDate,
    LocalDate expirationDate,
    String status,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal pendingAmount,
    List<CreditCardBillInstallmentDTO> installments
) {
}