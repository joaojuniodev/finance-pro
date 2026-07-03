package br.com.financepro.financePro.movement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MovementResponseDTO {

    private UUID id;
    private BigDecimal amount;
    private UUID fromWalletId;
    private UUID toWalletId;
    private LocalDateTime registeredAt;

    public MovementResponseDTO() {}

    public MovementResponseDTO(UUID id, BigDecimal amount, UUID fromWalletId, UUID toWalletId, LocalDateTime registeredAt) {
        this.id = id;
        this.amount = amount;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
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

    public UUID getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(UUID fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public UUID getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(UUID toWalletId) {
        this.toWalletId = toWalletId;
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

        MovementResponseDTO that = (MovementResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getFromWalletId(), that.getFromWalletId()) && Objects.equals(getToWalletId(), that.getToWalletId()) && Objects.equals(getRegisteredAt(), that.getRegisteredAt());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getFromWalletId());
        result = 31 * result + Objects.hashCode(getToWalletId());
        result = 31 * result + Objects.hashCode(getRegisteredAt());
        return result;
    }
}