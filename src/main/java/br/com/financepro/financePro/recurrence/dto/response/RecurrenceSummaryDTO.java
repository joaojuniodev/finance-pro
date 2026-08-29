package br.com.financepro.financePro.recurrence.dto.response;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.common.enums.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class RecurrenceSummaryDTO {

    private UUID id;
    private BigDecimal amount;
    private RecurrenceType type;
    private String description;
    private LocalDate nextExecutionDate;
    private CategoryResponseDTO category;

    public RecurrenceSummaryDTO() {}

    public RecurrenceSummaryDTO(UUID id, BigDecimal amount, RecurrenceType type, String description, LocalDate nextExecutionDate, CategoryResponseDTO category) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.nextExecutionDate = nextExecutionDate;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
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

        RecurrenceSummaryDTO that = (RecurrenceSummaryDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && getType() == that.getType() && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(nextExecutionDate, that.nextExecutionDate) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(nextExecutionDate);
        result = 31 * result + Objects.hashCode(category);
        return result;
    }
}