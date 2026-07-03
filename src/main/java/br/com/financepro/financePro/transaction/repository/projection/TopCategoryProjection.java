package br.com.financepro.financePro.transaction.repository.projection;

import br.com.financepro.financePro.common.enums.CategoryType;

import java.math.BigDecimal;
import java.util.UUID;

public interface TopCategoryProjection {
    UUID getCategoryId();
    String getCategoryName();
    CategoryType getCategoryType();
    Boolean getCategorySystem();
    BigDecimal getTotal();
}