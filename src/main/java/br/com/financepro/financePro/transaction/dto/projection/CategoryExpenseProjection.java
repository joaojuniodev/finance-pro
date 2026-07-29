package br.com.financepro.financePro.transaction.dto.projection;

import br.com.financepro.financePro.category.model.Category;

import java.math.BigDecimal;

public record CategoryExpenseProjection(
    Category category,
    BigDecimal amount
) {}