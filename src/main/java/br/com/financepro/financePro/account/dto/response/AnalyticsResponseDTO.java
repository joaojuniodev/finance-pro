package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.transaction.dto.projection.CategorySpendingDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsResponseDTO(
    BigDecimal availableToSpend,
    BigDecimal income,
    BigDecimal expense,
    BigDecimal commitments,
    List<TransactionResponseDTO> transactions,
    List<RecurrenceResponseDTO> recurrences,
    List<CategorySpendingDTO> categorySpending
) {
}