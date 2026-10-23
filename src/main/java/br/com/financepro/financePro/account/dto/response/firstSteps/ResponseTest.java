package br.com.financepro.financePro.account.dto.response.firstSteps;

import java.util.Map;

public record ResponseTest(
    Map<String, String> financialTrend,
    String financialMoment,
    Map<String, String> areaOfFocus,
    Map<String, String> mainAttention,
    Map<String, String> mainStrength,
    Map<String, String> keyOpportunity
) {}
