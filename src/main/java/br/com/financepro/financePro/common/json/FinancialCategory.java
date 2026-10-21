package br.com.financepro.financePro.common.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FinancialCategory(
    @JsonProperty("financial-security")
    List<QuestionJson> financialSecurity,

    @JsonProperty("financial-resilience")
    List<QuestionJson> financialResilience,

    @JsonProperty("financial-organization-and-control")
    List<QuestionJson> financialOrganizationAndControl,

    @JsonProperty("financial-impulse-regulation")
    List<QuestionJson> financialImpulseRegulation,

    @JsonProperty("financial-balance-and-freedom")
    List<QuestionJson> financialBalanceAndFreedom,

    @JsonProperty("financial-orientation-the-future")
    List<QuestionJson> financialOrientationTheFuture
){}