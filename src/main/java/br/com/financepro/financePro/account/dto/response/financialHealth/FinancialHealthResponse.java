package br.com.financepro.financePro.account.dto.response.financialHealth;

import br.com.financepro.financePro.common.enums.FinancialHealthStatus;

import java.util.List;

public record FinancialHealthResponse(
    Integer score,
    FinancialHealthStatus status,
    List<FeedbackFinancialHealth> identifiedStrengths,
    List<FeedbackFinancialHealth> optimizationPointsIdentified
) {}