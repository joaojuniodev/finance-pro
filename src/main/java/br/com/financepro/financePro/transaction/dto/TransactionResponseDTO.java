package br.com.financepro.financePro.transaction.dto;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.common.enums.CategoryType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionResponseDTO {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private String observation;
    private TransactionType type;
    private TransactionStatus status;
    private CategoryResponseDTO category;
    private LocalDateTime registeredAt;
    private UUID recurrenceId;

    public TransactionResponseDTO() {}

    public TransactionResponseDTO(UUID id, BigDecimal amount, String description, String observation, TransactionType type, TransactionStatus status, CategoryResponseDTO category, LocalDateTime registeredAt, UUID recurrenceId) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.observation = observation;
        this.type = type;
        this.status = status;
        this.category = category;
        this.registeredAt = registeredAt;
        this.recurrenceId = recurrenceId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TransactionResponseDTO that = (TransactionResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getObservation(), that.getObservation()) && getType() == that.getType() && getStatus() == that.getStatus() && Objects.equals(getCategory(), that.getCategory()) && Objects.equals(getRegisteredAt(), that.getRegisteredAt()) && Objects.equals(recurrenceId, that.recurrenceId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getObservation());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getRegisteredAt());
        result = 31 * result + Objects.hashCode(recurrenceId);
        return result;
    }
}