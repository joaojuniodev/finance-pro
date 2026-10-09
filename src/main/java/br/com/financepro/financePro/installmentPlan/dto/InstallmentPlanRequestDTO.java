package br.com.financepro.financePro.installmentPlan.dto;

import br.com.financepro.financePro.common.enums.InstallmentPlanStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentPlanRequestDTO(
    UUID id,
    String description,
    BigDecimal value,
    BigDecimal times,
    BigDecimal fees,
    InstallmentPlanStatus status,
    UUID walletId
) {
}