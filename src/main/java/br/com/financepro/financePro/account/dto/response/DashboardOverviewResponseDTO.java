package br.com.financepro.financePro.account.dto.response;

import br.com.financepro.financePro.recurrence.dto.RecurrenceSummaryDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DashboardOverviewResponseDTO {

    private BigDecimal currentBalance;
    private BigDecimal income;
    private BigDecimal expenses;
    private List<WalletResponseDTO> wallets;
    private List<TransactionResponseDTO> transactions;
    private List<RecurrenceSummaryDTO> recurrences;
    private List<ExpenseByCategoryResponseDTO> expensesByCategory;

    public DashboardOverviewResponseDTO() {}

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

    public List<WalletResponseDTO> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletResponseDTO> wallets) {
        this.wallets = wallets;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }

    public List<RecurrenceSummaryDTO> getRecurrences() {
        return recurrences;
    }

    public void setRecurrences(List<RecurrenceSummaryDTO> recurrences) {
        this.recurrences = recurrences;
    }

    public List<ExpenseByCategoryResponseDTO> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(List<ExpenseByCategoryResponseDTO> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DashboardOverviewResponseDTO that = (DashboardOverviewResponseDTO) o;
        return Objects.equals(getCurrentBalance(), that.getCurrentBalance()) && Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getWallets(), that.getWallets()) && Objects.equals(getTransactions(), that.getTransactions()) && Objects.equals(getRecurrences(), that.getRecurrences()) && Objects.equals(getExpensesByCategory(), that.getExpensesByCategory());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCurrentBalance());
        result = 31 * result + Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getWallets());
        result = 31 * result + Objects.hashCode(getTransactions());
        result = 31 * result + Objects.hashCode(getRecurrences());
        result = 31 * result + Objects.hashCode(getExpensesByCategory());
        return result;
    }
}