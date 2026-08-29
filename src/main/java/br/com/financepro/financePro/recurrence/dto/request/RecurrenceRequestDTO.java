package br.com.financepro.financePro.recurrence.dto.request;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class RecurrenceRequestDTO {

    private UUID id;
    private BigDecimal amount;
    private RecurrenceType type;
    private FrequencyType frequencyType;
    private ExecutionType executionType;
    private RecurrenceStatus status;
    private Integer dayOne;
    private Integer dayTwo;
    private Integer monthOfTheYear;
    private String description;
    private Boolean dayOneAlreadyOccurred;
    private Boolean dayTwoAlreadyOccurred;
    private Boolean monthOfTheYearAlreadyOccurred;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID categoryId;
    private UUID walletId;
    private UUID accountId;

    public RecurrenceRequestDTO() {}

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

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
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

    public RecurrenceStatus getStatus() {
        return status;
    }

    public void setStatus(RecurrenceStatus status) {
        this.status = status;
    }

    public Integer getMonthOfTheYear() {
        return monthOfTheYear;
    }

    public void setMonthOfTheYear(Integer monthOfTheYear) {
        this.monthOfTheYear = monthOfTheYear;
    }

    public Boolean getDayOneAlreadyOccurred() {
        return dayOneAlreadyOccurred;
    }

    public void setDayOneAlreadyOccurred(Boolean dayOneAlreadyOccurred) {
        this.dayOneAlreadyOccurred = dayOneAlreadyOccurred;
    }

    public Boolean getDayTwoAlreadyOccurred() {
        return dayTwoAlreadyOccurred;
    }

    public void setDayTwoAlreadyOccurred(Boolean dayTwoAlreadyOccurred) {
        this.dayTwoAlreadyOccurred = dayTwoAlreadyOccurred;
    }

    public Boolean getMonthOfTheYearAlreadyOccurred() {
        return monthOfTheYearAlreadyOccurred;
    }

    public void setMonthOfTheYearAlreadyOccurred(Boolean monthOfTheYearAlreadyOccurred) {
        this.monthOfTheYearAlreadyOccurred = monthOfTheYearAlreadyOccurred;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && getType() == that.getType() && getFrequencyType() == that.getFrequencyType() && getExecutionType() == that.getExecutionType() && getStatus() == that.getStatus() && Objects.equals(getDayOne(), that.getDayOne()) && Objects.equals(getDayTwo(), that.getDayTwo()) && Objects.equals(getMonthOfTheYear(), that.getMonthOfTheYear()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDayOneAlreadyOccurred(), that.getDayOneAlreadyOccurred()) && Objects.equals(getDayTwoAlreadyOccurred(), that.getDayTwoAlreadyOccurred()) && Objects.equals(getMonthOfTheYearAlreadyOccurred(), that.getMonthOfTheYearAlreadyOccurred()) && Objects.equals(getStartDate(), that.getStartDate()) && Objects.equals(getEndDate(), that.getEndDate()) && Objects.equals(getCategoryId(), that.getCategoryId()) && Objects.equals(getWalletId(), that.getWalletId()) && Objects.equals(getAccountId(), that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getFrequencyType());
        result = 31 * result + Objects.hashCode(getExecutionType());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getDayOne());
        result = 31 * result + Objects.hashCode(getDayTwo());
        result = 31 * result + Objects.hashCode(getMonthOfTheYear());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getDayOneAlreadyOccurred());
        result = 31 * result + Objects.hashCode(getDayTwoAlreadyOccurred());
        result = 31 * result + Objects.hashCode(getMonthOfTheYearAlreadyOccurred());
        result = 31 * result + Objects.hashCode(getStartDate());
        result = 31 * result + Objects.hashCode(getEndDate());
        result = 31 * result + Objects.hashCode(getCategoryId());
        result = 31 * result + Objects.hashCode(getWalletId());
        result = 31 * result + Objects.hashCode(getAccountId());
        return result;
    }
}