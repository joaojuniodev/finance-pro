package br.com.financepro.financePro.transaction.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AllTransactionResponseDTO {

    private BigDecimal currentBalance;
    private BigDecimal availableToSpend; // currentBalance - committed
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal netIncome;
    private BigDecimal commitment; // recorrências somadas
    private BiggestExpenseOfTheMonth expenseOfTheMonth;
    private BiggestIncomeOfTheMonth incomeOfTheMonth;
    private List<TransactionResponseDTO> transactions;

    public AllTransactionResponseDTO() {}

    public AllTransactionResponseDTO(BigDecimal currentBalance, BigDecimal availableToSpend, BigDecimal income, BigDecimal expenses, BigDecimal netIncome, BigDecimal commitment, BiggestExpenseOfTheMonth expenseOfTheMonth, BiggestIncomeOfTheMonth incomeOfTheMonth, List<TransactionResponseDTO> transactions) {
        this.currentBalance = currentBalance;
        this.availableToSpend = availableToSpend;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.commitment = commitment;
        this.expenseOfTheMonth = expenseOfTheMonth;
        this.incomeOfTheMonth = incomeOfTheMonth;
        this.transactions = transactions;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getAvailableToSpend() {
        return availableToSpend;
    }

    public void setAvailableToSpend(BigDecimal availableToSpend) {
        this.availableToSpend = availableToSpend;
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

    public BigDecimal getCommitment() {
        return commitment;
    }

    public void setCommitment(BigDecimal commitment) {
        this.commitment = commitment;
    }

    public BiggestExpenseOfTheMonth getExpenseOfTheMonth() {
        return expenseOfTheMonth;
    }

    public void setExpenseOfTheMonth(BiggestExpenseOfTheMonth expenseOfTheMonth) {
        this.expenseOfTheMonth = expenseOfTheMonth;
    }

    public BiggestIncomeOfTheMonth getIncomeOfTheMonth() {
        return incomeOfTheMonth;
    }

    public void setIncomeOfTheMonth(BiggestIncomeOfTheMonth incomeOfTheMonth) {
        this.incomeOfTheMonth = incomeOfTheMonth;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AllTransactionResponseDTO that = (AllTransactionResponseDTO) o;
        return Objects.equals(getCurrentBalance(), that.getCurrentBalance()) && Objects.equals(getAvailableToSpend(), that.getAvailableToSpend()) && Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getNetIncome(), that.getNetIncome()) && Objects.equals(getCommitment(), that.getCommitment()) && Objects.equals(getExpenseOfTheMonth(), that.getExpenseOfTheMonth()) && Objects.equals(getIncomeOfTheMonth(), that.getIncomeOfTheMonth()) && Objects.equals(getTransactions(), that.getTransactions());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCurrentBalance());
        result = 31 * result + Objects.hashCode(getAvailableToSpend());
        result = 31 * result + Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getNetIncome());
        result = 31 * result + Objects.hashCode(getCommitment());
        result = 31 * result + Objects.hashCode(getExpenseOfTheMonth());
        result = 31 * result + Objects.hashCode(getIncomeOfTheMonth());
        result = 31 * result + Objects.hashCode(getTransactions());
        return result;
    }
}