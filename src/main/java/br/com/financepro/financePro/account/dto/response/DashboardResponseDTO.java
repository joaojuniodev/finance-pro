package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.transaction.dto.response.CategorySpendingDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceSummaryDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDTO(
    BigDecimal currentBalance,
    BigDecimal income,
    BigDecimal expenses,
    BigDecimal availableToSpend,
    List<WalletResponseDTO> wallets,
    List<TransactionResponseDTO> transactions,
    List<RecurrenceSummaryDTO> recurrences,
    List<CategorySpendingDTO> expensesByCategory
) {
}