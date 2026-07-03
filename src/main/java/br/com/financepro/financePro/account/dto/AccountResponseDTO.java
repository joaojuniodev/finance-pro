package br.com.financepro.financePro.account.dto;

import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AccountResponseDTO {

    private UUID id;
    private BigDecimal currentBalance;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal netIncome;
    private BiggestExpense biggestExpense;
    private List<WalletResponseDTO> wallets = new ArrayList<>();

    public AccountResponseDTO() {}

    public AccountResponseDTO(UUID id, BigDecimal currentBalance, BigDecimal income, BigDecimal expenses, BigDecimal netIncome, BiggestExpense biggestExpense, List<WalletResponseDTO> wallets) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.biggestExpense = biggestExpense;
        this.wallets = wallets;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public BiggestExpense getBiggestExpense() {
        return biggestExpense;
    }

    public void setBiggestExpense(BiggestExpense biggestExpense) {
        this.biggestExpense = biggestExpense;
    }

    public List<WalletResponseDTO> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletResponseDTO> wallets) {
        this.wallets = wallets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AccountResponseDTO that = (AccountResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCurrentBalance(), that.getCurrentBalance()) && Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getNetIncome(), that.getNetIncome()) && Objects.equals(getBiggestExpense(), that.getBiggestExpense()) && Objects.equals(getWallets(), that.getWallets());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getCurrentBalance());
        result = 31 * result + Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getNetIncome());
        result = 31 * result + Objects.hashCode(getBiggestExpense());
        result = 31 * result + Objects.hashCode(getWallets());
        return result;
    }
}
