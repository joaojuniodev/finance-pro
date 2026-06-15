package br.com.financepro.financePro.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private BigDecimal balance;

    @OneToOne(mappedBy = "wallet")
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Wallet() {}

    public Wallet(UUID id, String name, String description, BigDecimal balance, Goal goal, Account account) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.goal = goal;
        this.account = account;
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

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
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

        Wallet wallet = (Wallet) o;
        return Objects.equals(getId(), wallet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
