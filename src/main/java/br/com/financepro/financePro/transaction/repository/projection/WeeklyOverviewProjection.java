package br.com.financepro.financePro.transaction.repository.projection;

import java.math.BigDecimal;

public interface WeeklyOverviewProjection {

    Integer getMonth();

    Integer getWeek();

    BigDecimal getIncome();

    BigDecimal getExpenses();
}