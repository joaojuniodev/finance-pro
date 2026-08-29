package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;

import java.util.List;

public record ActivitiesResponseDTO(
    List<TransactionResponseDTO> transactions,
    List<RecurrenceResponseDTO> recurrences
) {
}