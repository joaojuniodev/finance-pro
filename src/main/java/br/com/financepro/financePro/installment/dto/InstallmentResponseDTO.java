package br.com.financepro.financePro.installment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentResponseDTO(
    UUID id,
    BigDecimal value,
    Boolean isPaid,
    Integer month,
    Integer year
) {
}