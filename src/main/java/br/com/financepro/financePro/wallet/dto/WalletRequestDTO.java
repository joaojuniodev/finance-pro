package br.com.financepro.financePro.wallet.dto;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class WalletRequestDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal balance;
    private String cardDigits;
    private UUID goalId;
    private UUID accountId;
    private UUID bankId;

    public WalletRequestDTO() {}

    public WalletRequestDTO(UUID id, String name, String description, BigDecimal balance, String cardDigits, UUID goalId, UUID accountId, UUID bankId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.cardDigits = cardDigits;
        this.goalId = goalId;
        this.accountId = accountId;
        this.bankId = bankId;
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

    public String getCardDigits() {
        return cardDigits;
    }

    public void setCardDigits(String cardDigits) {
        this.cardDigits = cardDigits;
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

    public UUID getBankId() {
        return bankId;
    }

    public void setBankId(UUID bankId) {
        this.bankId = bankId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WalletRequestDTO that = (WalletRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getBalance(), that.getBalance()) && Objects.equals(getCardDigits(), that.getCardDigits()) && Objects.equals(getGoalId(), that.getGoalId()) && Objects.equals(getAccountId(), that.getAccountId()) && Objects.equals(getBankId(), that.getBankId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getBalance());
        result = 31 * result + Objects.hashCode(getCardDigits());
        result = 31 * result + Objects.hashCode(getGoalId());
        result = 31 * result + Objects.hashCode(getAccountId());
        result = 31 * result + Objects.hashCode(getBankId());
        return result;
    }
}