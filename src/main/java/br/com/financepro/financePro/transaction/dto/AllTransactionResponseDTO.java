package br.com.financepro.financePro.transaction.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AllTransactionResponseDTO {

    private BigDecimal currentBalance;
    private Integer upLastMonth;
    private BigDecimal availableToSpend; // currentBalance - committed
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal netIncome;
    private BigDecimal commitment; // recorrências somadas
    private TransactionResponseDTO transactionBiggestExpense;
    private TransactionResponseDTO transactionBiggestIncome;
    private List<TransactionResponseDTO> transactions;

    public AllTransactionResponseDTO() {}

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getUpLastMonth() {
        return upLastMonth;
    }

    public void setUpLastMonth(Integer upLastMonth) {
        this.upLastMonth = upLastMonth;
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

    public TransactionResponseDTO getTransactionBiggestExpense() {
        return transactionBiggestExpense;
    }

    public void setTransactionBiggestExpense(TransactionResponseDTO transactionBiggestExpense) {
        this.transactionBiggestExpense = transactionBiggestExpense;
    }

    public TransactionResponseDTO getTransactionBiggestIncome() {
        return transactionBiggestIncome;
    }

    public void setTransactionBiggestIncome(TransactionResponseDTO transactionBiggestIncome) {
        this.transactionBiggestIncome = transactionBiggestIncome;
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
        return Objects.equals(getCurrentBalance(), that.getCurrentBalance()) && Objects.equals(getUpLastMonth(), that.getUpLastMonth()) && Objects.equals(getAvailableToSpend(), that.getAvailableToSpend()) && Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getNetIncome(), that.getNetIncome()) && Objects.equals(getCommitment(), that.getCommitment()) && Objects.equals(getTransactionBiggestExpense(), that.getTransactionBiggestExpense()) && Objects.equals(getTransactionBiggestIncome(), that.getTransactionBiggestIncome()) && Objects.equals(getTransactions(), that.getTransactions());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCurrentBalance());
        result = 31 * result + Objects.hashCode(getUpLastMonth());
        result = 31 * result + Objects.hashCode(getAvailableToSpend());
        result = 31 * result + Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getNetIncome());
        result = 31 * result + Objects.hashCode(getCommitment());
        result = 31 * result + Objects.hashCode(getTransactionBiggestExpense());
        result = 31 * result + Objects.hashCode(getTransactionBiggestIncome());
        result = 31 * result + Objects.hashCode(getTransactions());
        return result;
    }
}