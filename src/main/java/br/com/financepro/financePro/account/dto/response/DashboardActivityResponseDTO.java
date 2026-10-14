package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.transaction.dto.response.EvolutionDataPointDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record DashboardActivityResponseDTO(
    LocalDate weekStart,
    LocalDate weekEnd,
    List<EvolutionDataPointDTO> weeklyFlow,
    List<TransactionResponseDTO> recentTransactions
) {
}
