package br.com.financepro.financePro.transaction.dto.request;

import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionRequestDTO {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private String observation;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime registeredAt;
    private UUID categoryId;
    private UUID walletId;
    private UUID accountId;

    public TransactionRequestDTO() {}

    public TransactionRequestDTO(UUID id, BigDecimal amount, String description, String observation, TransactionType type, TransactionStatus status, LocalDateTime registeredAt, UUID categoryId, UUID walletId, UUID accountId) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.observation = observation;
        this.type = type;
        this.status = status;
        this.registeredAt = registeredAt;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.accountId = accountId;
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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TransactionRequestDTO that = (TransactionRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getObservation(), that.getObservation()) && getType() == that.getType() && Objects.equals(getRegisteredAt(), that.getRegisteredAt()) && Objects.equals(getCategoryId(), that.getCategoryId()) && Objects.equals(getAccountId(), that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getObservation());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getRegisteredAt());
        result = 31 * result + Objects.hashCode(getCategoryId());
        result = 31 * result + Objects.hashCode(getAccountId());
        return result;
    }
}