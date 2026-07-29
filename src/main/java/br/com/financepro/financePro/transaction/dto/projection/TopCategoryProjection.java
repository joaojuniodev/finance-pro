package br.com.financepro.financePro.transaction.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface TopCategoryProjection {
    UUID getCategoryId();
    BigDecimal getTotal();
}