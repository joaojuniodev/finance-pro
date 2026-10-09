package br.com.financepro.financePro.installment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentRequestDTO(
    UUID id,
    BigDecimal value,
    Boolean isPaid,
    Integer month,
    Integer year,
    UUID installmentPlanId
) {
}