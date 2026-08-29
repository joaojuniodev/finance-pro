package br.com.financepro.financePro.transaction.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public record CategorySpendingDTO(
    UUID id,
    String name,
    String color,
    String icon,
    BigDecimal amount,
    Double percentage
) {
}