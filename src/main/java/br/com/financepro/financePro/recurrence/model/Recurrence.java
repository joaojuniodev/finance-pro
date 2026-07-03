package br.com.financepro.financePro.recurrence.model;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "recurrences")
public class Recurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column
    private RecurrenceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type")
    private FrequencyType frequencyType;

    @Column(name = "day_one")
    private Integer dayOne;

    @Column(name = "day_two")
    private Integer dayTwo;

    @Column(name = "month_of_the_year")
    private Integer monthOfTheYear;

    @Column(name = "next_execution_date")
    private LocalDate nextExecutionDate;

    @Column(name = "last_execution_date")
    private LocalDate lastExecutionDate;

    @Column
    private Boolean active = true;

    @Column(nullable = false, length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Recurrence() {}

    public Recurrence(UUID id, BigDecimal amount, RecurrenceType type, FrequencyType frequencyType, Integer dayOne, Integer dayTwo, Integer monthOfTheYear, String description, Account account, Wallet wallet) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.frequencyType = frequencyType;
        this.dayOne = dayOne;
        this.dayTwo = dayTwo;
        this.monthOfTheYear = monthOfTheYear;
        this.active = true;
        this.description = description;
        this.account = account;
        this.wallet = wallet;
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

    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public Integer getDayOne() {
        return dayOne;
    }

    public void setDayOne(Integer dayOne) {
        this.dayOne = dayOne;
    }

    public Integer getDayTwo() {
        return dayTwo;
    }

    public void setDayTwo(Integer dayTwo) {
        this.dayTwo = dayTwo;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public LocalDate getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(LocalDate lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMonthOfTheYear() {
        return monthOfTheYear;
    }

    public void setMonthOfTheYear(Integer monthOfTheYear) {
        this.monthOfTheYear = monthOfTheYear;
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

        Recurrence that = (Recurrence) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}