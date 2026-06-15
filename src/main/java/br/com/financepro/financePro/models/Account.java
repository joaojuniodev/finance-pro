package br.com.financepro.financePro.models;

import br.com.financepro.financePro.models.enums.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Column
    private BigDecimal income;

    @Column
    private BigDecimal expenses;

    @Column(name = "net_income")
    private BigDecimal netIncome;

    @Enumerated(EnumType.STRING)
    @Column
    private Category biggestExpense;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Recurrence> recurrences = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Wallet> wallets = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {}

    public Account(UUID id, BigDecimal currentBalance, BigDecimal income, BigDecimal expenses, BigDecimal netIncome, Category biggestExpense) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.biggestExpense = biggestExpense;
    }

    public Account(UUID id, BigDecimal currentBalance, BigDecimal income, BigDecimal expenses, BigDecimal netIncome, Category biggestExpense, Set<Transaction> transactions, Set<Recurrence> recurrences, Set<Wallet> wallets) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.biggestExpense = biggestExpense;
        this.transactions = transactions;
        this.recurrences = recurrences;
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

    public Category getBiggestExpense() {
        return biggestExpense;
    }

    public void setBiggestExpense(Category biggestExpense) {
        this.biggestExpense = biggestExpense;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Recurrence> getRecurrences() {
        return recurrences;
    }

    public void setRecurrences(Set<Recurrence> recurrences) {
        this.recurrences = recurrences;
    }

    public Set<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(Set<Wallet> wallets) {
        this.wallets = wallets;
    }
}
