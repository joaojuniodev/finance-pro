package br.com.financepro.financePro.transaction.dto.response;

import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionSummaryDTO {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime registeredAt;

    public TransactionSummaryDTO() {}

    public TransactionSummaryDTO(UUID id, BigDecimal amount, String description, TransactionType type, TransactionStatus status, LocalDateTime registeredAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.registeredAt = registeredAt;
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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TransactionSummaryDTO that = (TransactionSummaryDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getDescription(), that.getDescription()) && getType() == that.getType() && getStatus() == that.getStatus() && Objects.equals(getRegisteredAt(), that.getRegisteredAt());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getRegisteredAt());
        return result;
    }
}