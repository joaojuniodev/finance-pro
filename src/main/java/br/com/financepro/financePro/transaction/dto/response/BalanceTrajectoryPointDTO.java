package br.com.financepro.financePro.transaction.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BalanceTrajectoryPointDTO(
    LocalDate date,
    BigDecimal balance
) {
}