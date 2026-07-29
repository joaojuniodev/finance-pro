package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;

import java.math.BigDecimal;
import java.util.Objects;

public class ExpenseByCategoryResponseDTO {

    private CategoryResponseDTO category;
    private BigDecimal percentage;
    private BigDecimal amount;

    public ExpenseByCategoryResponseDTO() {}

    public ExpenseByCategoryResponseDTO(CategoryResponseDTO category, BigDecimal percentage, BigDecimal amount) {
        this.category = category;
        this.percentage = percentage;
        this.amount = amount;
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseByCategoryResponseDTO that = (ExpenseByCategoryResponseDTO) o;
        return Objects.equals(getCategory(), that.getCategory()) && Objects.equals(getPercentage(), that.getPercentage()) && Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getPercentage());
        result = 31 * result + Objects.hashCode(getAmount());
        return result;
    }
}