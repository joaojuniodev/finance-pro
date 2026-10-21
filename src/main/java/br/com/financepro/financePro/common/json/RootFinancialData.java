package br.com.financepro.financePro.common.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RootFinancialData(
    @JsonProperty("financial-data")
    FinancialCategory financialData
){}