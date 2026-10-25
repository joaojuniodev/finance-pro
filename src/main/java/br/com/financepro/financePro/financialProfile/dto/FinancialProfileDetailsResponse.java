package br.com.financepro.financePro.financialProfile.dto;

import br.com.financepro.financePro.financialProfile.model.FinancialProfile;
import java.time.LocalDateTime;

public record FinancialProfileDetailsResponse(
    FinancialProfileResponse profile,
    Integer generalFinancialIndex,
    FinancialDimensionsDTO dimensions,
    LocalDateTime registeredAt
) {
    public static FinancialProfileDetailsResponse from(FinancialProfile entity) {
        return new FinancialProfileDetailsResponse(
            new FinancialProfileResponse(
                entity.getFinancialTrend(), entity.getFinancialMoment(), entity.getCurrentAreaOfFocus(),
                entity.getMainAttention(), entity.getMainStrength(), entity.getKeyOpportunity(),
                entity.getConfidenceScore(), entity.getConfidenceInTheResult(), entity.getInterpretiveSynthesis()
            ),
            entity.getGeneralFinancialIndex(),
            new FinancialDimensionsDTO(
                asDouble(entity.getFinancialSecurity()), asDouble(entity.getFinancialResilience()),
                asDouble(entity.getOrganizationAndControl()), asDouble(entity.getImpulseRegulation()),
                asDouble(entity.getBalanceAndFreedom()), asDouble(entity.getOrientationTheFuture())
            ),
            entity.getRegisteredAt()
        );
    }
    private static Double asDouble(Integer score) {
        return score == null ? null : score.doubleValue();
    }
}
