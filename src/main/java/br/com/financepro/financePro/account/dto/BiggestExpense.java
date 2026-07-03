package br.com.financepro.financePro.account.dto;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.common.enums.CategoryType;

import java.math.BigDecimal;
import java.util.Objects;

public class BiggestExpense {

    private BigDecimal value;
    private CategoryResponseDTO category;

    public BiggestExpense() {}

    public BiggestExpense(BigDecimal value, CategoryResponseDTO category) {
        this.value = value;
        this.category = category;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        BiggestExpense that = (BiggestExpense) o;
        return Objects.equals(getValue(), that.getValue()) && getCategory() == that.getCategory();
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getValue());
        result = 31 * result + Objects.hashCode(getCategory());
        return result;
    }
}