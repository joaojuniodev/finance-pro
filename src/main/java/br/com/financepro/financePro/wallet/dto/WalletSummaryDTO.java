package br.com.financepro.financePro.wallet.dto;

import br.com.financepro.financePro.transaction.dto.response.TransactionSummaryDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class WalletSummaryDTO {

    private BigDecimal income;
    private BigDecimal expenses;
    private WalletResponseDTO wallet;
    private List<TransactionSummaryDTO> transactions;

    public WalletSummaryDTO() {}

    public WalletSummaryDTO(BigDecimal income, BigDecimal expenses, WalletResponseDTO wallet, List<TransactionSummaryDTO> transactions) {
        this.income = income;
        this.expenses = expenses;
        this.wallet = wallet;
        this.transactions = transactions;
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

    public WalletResponseDTO getWallet() {
        return wallet;
    }

    public void setWallet(WalletResponseDTO wallet) {
        this.wallet = wallet;
    }

    public List<TransactionSummaryDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionSummaryDTO> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WalletSummaryDTO that = (WalletSummaryDTO) o;
        return Objects.equals(getIncome(), that.getIncome()) && Objects.equals(getExpenses(), that.getExpenses()) && Objects.equals(getWallet(), that.getWallet()) && Objects.equals(getTransactions(), that.getTransactions());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getIncome());
        result = 31 * result + Objects.hashCode(getExpenses());
        result = 31 * result + Objects.hashCode(getWallet());
        result = 31 * result + Objects.hashCode(getTransactions());
        return result;
    }
}