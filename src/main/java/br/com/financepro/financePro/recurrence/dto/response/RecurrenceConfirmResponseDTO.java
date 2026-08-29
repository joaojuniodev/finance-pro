package br.com.financepro.financePro.recurrence.dto.response;

public record RecurrenceConfirmResponseDTO(
    Boolean confirm,
    String description
) {
}