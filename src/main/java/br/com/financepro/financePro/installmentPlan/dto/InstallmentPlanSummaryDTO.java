package br.com.financepro.financePro.installmentPlan.dto;

import br.com.financepro.financePro.common.enums.InstallmentPlanStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentPlanSummaryDTO(
    UUID id,
    String description,
    BigDecimal totalValue,
    BigDecimal value,
    BigDecimal times,
    BigDecimal fees,
    InstallmentPlanStatus status
) {
}