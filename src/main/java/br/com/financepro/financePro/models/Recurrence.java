package br.com.financepro.financePro.models;

import br.com.financepro.financePro.models.enums.BillingTimeType;
import br.com.financepro.financePro.models.enums.RecurrenceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @Column
    private RecurrenceType type;

    @Column(name = "billing_time_type")
    private BillingTimeType billingTimeType;

    @Column(name = "day_one")
    private Integer dayOne;

    @Column(name = "day_two")
    private Integer dayTwo;

    @Column(name = "month_of_the_year")
    private Integer monthOfTheYear;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Recurrence() {}

    public Recurrence(UUID id, BigDecimal amount, RecurrenceType type, BillingTimeType billingTimeType, Integer dayOne, Integer dayTwo, Integer monthOfTheYear, Account account) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.billingTimeType = billingTimeType;
        this.dayOne = dayOne;
        this.dayTwo = dayTwo;
        this.monthOfTheYear = monthOfTheYear;
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

    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public BillingTimeType getBillingTimeType() {
        return billingTimeType;
    }

    public void setBillingTimeType(BillingTimeType billingTimeType) {
        this.billingTimeType = billingTimeType;
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

    public Integer getMonthOfTheYear() {
        return monthOfTheYear;
    }

    public void setMonthOfTheYear(Integer monthOfTheYear) {
        this.monthOfTheYear = monthOfTheYear;
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