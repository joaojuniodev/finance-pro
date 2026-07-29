package br.com.financepro.financePro.account.model;

import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

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

    @ManyToOne
    @JoinColumn(name = "biggest_expense_category_id")
    private Category biggestExpenseCategory;

    @Column(name = "biggest_expense_value")
    private BigDecimal biggestExpenseValue;

    @OneToMany(mappedBy = "account")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Recurrence> recurrences = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<Wallet> wallets = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {}

    public Account(UUID id, BigDecimal currentBalance, BigDecimal income, BigDecimal expenses, BigDecimal netIncome) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.income = income;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.biggestExpenseCategory = null;
        this.biggestExpenseValue = null;
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

    public Category getBiggestExpenseCategory() {
        return biggestExpenseCategory;
    }

    public void setBiggestExpenseCategory(Category biggestExpenseCategory) {
        this.biggestExpenseCategory = biggestExpenseCategory;
    }

    public BigDecimal getBiggestExpenseValue() {
        return biggestExpenseValue;
    }

    public void setBiggestExpenseValue(BigDecimal biggestExpenseValue) {
        this.biggestExpenseValue = biggestExpenseValue;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}