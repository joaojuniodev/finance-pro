package br.com.financepro.financePro.recurrence.service.params;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceSort;
import br.com.financepro.financePro.common.enums.RecurrenceType;

import java.util.UUID;

public record RecurrenceSearchParams(
    UUID accountId,
    String search,
    RecurrenceType type,
    FrequencyType frequencyType,
    ExecutionType executionType,
    RecurrenceSort sort
) {
}