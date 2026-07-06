package br.com.financepro.financePro.transaction.dto;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;

import java.math.BigDecimal;

public record BiggestExpenseOfTheMonth(
    BigDecimal value,
    CategoryResponseDTO category
) {

    public BiggestExpenseOfTheMonth(BigDecimal value, CategoryResponseDTO category) {
        this.value = value;
        this.category = category;
    }
}