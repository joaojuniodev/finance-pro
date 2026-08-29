package br.com.financepro.financePro.wallet.model;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.bank.model.Bank;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.goal.model.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallets")
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

    @Column(name = "card_digits", length = 4)
    private String cardDigits;

    @Enumerated(EnumType.STRING)
    private WalletType type;

    @Column
    private String color;

    @OneToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Wallet() {}

    public Wallet(UUID id, String name, String description, BigDecimal balance, String cardDigits, WalletType type, String color, Goal goal, Account account, Bank bank) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.cardDigits = cardDigits;
        this.type = type;
        this.color = color;
        this.goal = goal;
        this.account = account;
        this.bank = bank;
    }

    public Wallet(String initialWalletName, String initialWalletDescription, BigDecimal initialBalance, Account accountCreated) {
        this.name = initialWalletName;
        this.description = initialWalletDescription;
        this.balance = initialBalance;
        this.account = accountCreated;
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

    public String getCardDigits() {
        return cardDigits;
    }

    public void setCardDigits(String cardDigits) {
        this.cardDigits = cardDigits;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonIgnore
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