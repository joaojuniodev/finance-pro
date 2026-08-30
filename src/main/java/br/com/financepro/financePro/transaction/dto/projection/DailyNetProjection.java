package br.com.financepro.financePro.transaction.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyNetProjection {
    LocalDate getDate();
    BigDecimal getNetAmount();
}