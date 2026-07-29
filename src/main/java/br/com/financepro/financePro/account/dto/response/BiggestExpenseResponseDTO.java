package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;

import java.math.BigDecimal;
import java.util.Objects;

public class BiggestExpenseResponseDTO {

    private BigDecimal value;
    private CategoryResponseDTO category;

    public BiggestExpenseResponseDTO() {}

    public BiggestExpenseResponseDTO(BigDecimal value, CategoryResponseDTO category) {
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

        BiggestExpenseResponseDTO that = (BiggestExpenseResponseDTO) o;
        return Objects.equals(getValue(), that.getValue()) && getCategory() == that.getCategory();
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getValue());
        result = 31 * result + Objects.hashCode(getCategory());
        return result;
    }
}