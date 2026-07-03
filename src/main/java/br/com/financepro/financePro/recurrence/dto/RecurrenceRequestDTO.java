package br.com.financepro.financePro.recurrence.dto;

import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class RecurrenceRequestDTO {

    private UUID id;
    private BigDecimal amount;
    private RecurrenceType type;
    private FrequencyType frequencyType;
    private Integer dayOne;
    private Integer dayTwo;
    private Integer monthOfTheYear;
    private String description;
    private UUID walletId;
    private UUID accountId;

    public RecurrenceRequestDTO() {}

    public RecurrenceRequestDTO(UUID id, BigDecimal amount, RecurrenceType type, FrequencyType frequencyType, Integer dayOne, Integer dayTwo, Integer monthOfTheYear, String description, UUID walletId, UUID accountId) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.frequencyType = frequencyType;
        this.dayOne = dayOne;
        this.dayTwo = dayTwo;
        this.monthOfTheYear = monthOfTheYear;
        this.description = description;
        this.walletId = walletId;
        this.accountId = accountId;
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

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RecurrenceRequestDTO that = (RecurrenceRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && getType() == that.getType() && getFrequencyType() == that.getFrequencyType() && Objects.equals(getDayOne(), that.getDayOne()) && Objects.equals(getDayTwo(), that.getDayTwo()) && Objects.equals(getMonthOfTheYear(), that.getMonthOfTheYear()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getWalletId(), that.getWalletId()) && Objects.equals(getAccountId(), that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getFrequencyType());
        result = 31 * result + Objects.hashCode(getDayOne());
        result = 31 * result + Objects.hashCode(getDayTwo());
        result = 31 * result + Objects.hashCode(getMonthOfTheYear());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getWalletId());
        result = 31 * result + Objects.hashCode(getAccountId());
        return result;
    }
}