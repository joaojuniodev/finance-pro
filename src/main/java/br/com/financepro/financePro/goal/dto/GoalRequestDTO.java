package br.com.financepro.financePro.goal.dto;

import br.com.financepro.financePro.common.enums.CategoryType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class GoalRequestDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal totalAmount;
    private BigDecimal currentAmount;
    private UUID categoryId;
    private UUID walletId;

    public GoalRequestDTO() {}

    public GoalRequestDTO(UUID id, String name, String description, BigDecimal totalAmount, BigDecimal currentAmount, UUID categoryId, UUID walletId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.categoryId = categoryId;
        this.walletId = walletId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        GoalRequestDTO that = (GoalRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getTotalAmount(), that.getTotalAmount()) && Objects.equals(getCurrentAmount(), that.getCurrentAmount()) && Objects.equals(getCategoryId(), that.getCategoryId()) && Objects.equals(getWalletId(), that.getWalletId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getTotalAmount());
        result = 31 * result + Objects.hashCode(getCurrentAmount());
        result = 31 * result + Objects.hashCode(getCategoryId());
        result = 31 * result + Objects.hashCode(getWalletId());
        return result;
    }
}