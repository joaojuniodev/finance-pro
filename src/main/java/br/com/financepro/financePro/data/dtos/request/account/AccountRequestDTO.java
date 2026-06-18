package br.com.financepro.financePro.data.dtos.request.account;

import br.com.financepro.financePro.models.enums.Category;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AccountRequestDTO {

    private UUID id;
    private BigDecimal currentBalance;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal netIncome;
    private Category biggestExpense;

    public AccountRequestDTO() {}

    public AccountRequestDTO(UUID id, BigDecimal currentBalance, BigDecimal income, BigDecimal expenses, BigDecimal netIncome, Category biggestExpense) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.biggestExpense = biggestExpense;
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

    public Category getBiggestExpense() {
        return biggestExpense;
    }

    public void setBiggestExpense(Category biggestExpense) {
        this.biggestExpense = biggestExpense;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AccountRequestDTO that = (AccountRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCurrentBalance(), that.getCurrentBalance()) && Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getNetIncome(), that.getNetIncome()) && getBiggestExpense() == that.getBiggestExpense();
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getCurrentBalance());
        result = 31 * result + Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getNetIncome());
        result = 31 * result + Objects.hashCode(getBiggestExpense());
        return result;
    }
}
