package br.com.financepro.financePro.transaction.dto;

import java.math.BigDecimal;

public record WeekOverviewResponse(
    Integer week,
    BigDecimal income,
    BigDecimal expenses
) {
}