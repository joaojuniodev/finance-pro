package br.com.financepro.financePro.transaction.model;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.common.enums.CategoryType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal amount;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(length = 200)
    private String observation;

    @Enumerated(EnumType.STRING)
    @Column
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column
    private TransactionStatus status;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurrence_id")
    private Recurrence recurrence;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {}

    public Transaction(UUID id, BigDecimal amount, String description, String observation, TransactionType type, TransactionStatus status, LocalDateTime registeredAt, Category category, Wallet wallet, Account account) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.observation = observation;
        this.type = type;
        this.status = status;
        this.registeredAt = registeredAt;
        this.category = category;
        this.wallet = wallet;
        this.account = account;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}