package br.com.financepro.financePro.financialProfile.dto;

import java.util.Map;

public record FinancialProfileResponse(
    Map<String, String> financialTrend,
    Map<String, String> financialMoment,
    Map<String, String> areaOfFocus,
    Map<String, String> mainAttention,
    Map<String, String> mainStrength,
    Map<String, String> keyOpportunity,
    Integer confidenceScore,
    Map<String, String> confidenceInTheResult,
    String interpretiveSynthesis
) {}