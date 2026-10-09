package br.com.financepro.financePro.installmentPlan.filter;

import java.util.UUID;

public record InstallmentPlanFilter(
    UUID accountId,
    UUID walletId
) {
}