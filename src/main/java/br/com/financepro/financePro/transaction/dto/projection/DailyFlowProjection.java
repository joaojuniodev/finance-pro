package br.com.financepro.financePro.transaction.dto.projection;

import br.com.financepro.financePro.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyFlowProjection {
    LocalDate getDate();
    TransactionType getType();
    BigDecimal getTotal();
}