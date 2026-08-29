package br.com.financepro.financePro.transaction.dto.projection;

import java.math.BigDecimal;

public interface CategoryAmountProjection {
    String getId();
    String getName();
    String getColor();
    String getIcon();
    BigDecimal getTotalAmount();
}