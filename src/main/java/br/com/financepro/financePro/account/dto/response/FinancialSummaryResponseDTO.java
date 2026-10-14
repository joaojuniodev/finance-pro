package br.com.financepro.financePro.account.dto.response;

import java.math.BigDecimal;

public record FinancialSummaryResponseDTO(
    BigDecimal currentBalance,
    BigDecimal income,
    BigDecimal expenses,
    BigDecimal availableToSpend
) {
}
