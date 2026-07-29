package br.com.financepro.financePro.recurrence.dto;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class RecurrenceResponseDTO {

    private UUID id;
    private BigDecimal amount;
    private RecurrenceType type;
    private FrequencyType frequencyType;
    private ExecutionType executionType;
    private Integer dayOne;
    private Integer dayTwo;
    private Integer monthOfTheYear;
    private LocalDate nextExecutionDate;
    private LocalDate lastExecutionDate;
    private Boolean active = true;
    private String description;
    private CategoryResponseDTO category;
    private WalletResponseDTO wallet;

    public RecurrenceResponseDTO() {}

    public RecurrenceResponseDTO(UUID id, BigDecimal amount, RecurrenceType type, FrequencyType frequencyType, ExecutionType executionType, Integer dayOne, Integer dayTwo, Integer monthOfTheYear, LocalDate nextExecutionDate, LocalDate lastExecutionDate, Boolean active, String description, CategoryResponseDTO category, WalletResponseDTO wallet) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.frequencyType = frequencyType;
        this.executionType = executionType;
        this.dayOne = dayOne;
        this.dayTwo = dayTwo;
        this.monthOfTheYear = monthOfTheYear;
        this.nextExecutionDate = nextExecutionDate;
        this.lastExecutionDate = lastExecutionDate;
        this.active = active;
        this.description = description;
        this.category = category;
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

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
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

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    public WalletResponseDTO getWallet() {
        return wallet;
    }

    public void setWallet(WalletResponseDTO wallet) {
        this.wallet = wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RecurrenceResponseDTO that = (RecurrenceResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAmount(), that.getAmount()) && getType() == that.getType() && getFrequencyType() == that.getFrequencyType() && getExecutionType() == that.getExecutionType() && Objects.equals(getDayOne(), that.getDayOne()) && Objects.equals(getDayTwo(), that.getDayTwo()) && Objects.equals(getMonthOfTheYear(), that.getMonthOfTheYear()) && Objects.equals(getNextExecutionDate(), that.getNextExecutionDate()) && Objects.equals(getLastExecutionDate(), that.getLastExecutionDate()) && Objects.equals(getActive(), that.getActive()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getCategory(), that.getCategory()) && Objects.equals(getWallet(), that.getWallet());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getAmount());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getFrequencyType());
        result = 31 * result + Objects.hashCode(getExecutionType());
        result = 31 * result + Objects.hashCode(getDayOne());
        result = 31 * result + Objects.hashCode(getDayTwo());
        result = 31 * result + Objects.hashCode(getMonthOfTheYear());
        result = 31 * result + Objects.hashCode(getNextExecutionDate());
        result = 31 * result + Objects.hashCode(getLastExecutionDate());
        result = 31 * result + Objects.hashCode(getActive());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getWallet());
        return result;
    }
}