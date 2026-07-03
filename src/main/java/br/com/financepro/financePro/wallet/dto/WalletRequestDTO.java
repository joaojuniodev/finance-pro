package br.com.financepro.financePro.wallet.dto;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class WalletRequestDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal balance;
    private UUID goalId;
    private UUID accountId;

    public WalletRequestDTO() {}

    public WalletRequestDTO(UUID id, String name, String description, BigDecimal balance, UUID goalId, UUID accountId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.goalId = goalId;
        this.accountId = accountId;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getGoalId() {
        return goalId;
    }

    public void setGoalId(UUID goalId) {
        this.goalId = goalId;
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

        WalletRequestDTO that = (WalletRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getBalance(), that.getBalance()) && Objects.equals(getGoalId(), that.getGoalId()) && Objects.equals(getAccountId(), that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getBalance());
        result = 31 * result + Objects.hashCode(getGoalId());
        result = 31 * result + Objects.hashCode(getAccountId());
        return result;
    }
}