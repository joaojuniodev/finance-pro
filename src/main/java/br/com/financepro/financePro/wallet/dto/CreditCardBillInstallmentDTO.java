package br.com.financepro.financePro.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardBillInstallmentDTO(
    UUID id,
    UUID installmentPlanId,
    String description,
    BigDecimal value,
    Boolean paid,
    Integer month,
    Integer year
) {
}