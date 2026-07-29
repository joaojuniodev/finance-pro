package br.com.financepro.financePro.recurrence.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AllRecurrenceResponseDTO {

    private Long totalRegistered;
    private BigDecimal totalIncomeAmount;
    private BigDecimal totalExpenseAmount;
    private BigDecimal monthlyImpact;
    private List<RecurrenceResponseDTO> recurrencesDueToday;
    private List<RecurrenceResponseDTO> recurrencesOverdue;
    private List<RecurrenceResponseDTO> recurrencesUpcoming;

    public AllRecurrenceResponseDTO() {}

    public Long getTotalRegistered() {
        return totalRegistered;
    }

    public void setTotalRegistered(Long totalRegistered) {
        this.totalRegistered = totalRegistered;
    }

    public BigDecimal getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    public void setTotalIncomeAmount(BigDecimal totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    public BigDecimal getTotalExpenseAmount() {
        return totalExpenseAmount;
    }

    public void setTotalExpenseAmount(BigDecimal totalExpenseAmount) {
        this.totalExpenseAmount = totalExpenseAmount;
    }

    public BigDecimal getMonthlyImpact() {
        return monthlyImpact;
    }

    public void setMonthlyImpact(BigDecimal monthlyImpact) {
        this.monthlyImpact = monthlyImpact;
    }

    public List<RecurrenceResponseDTO> getRecurrencesDueToday() {
        return recurrencesDueToday;
    }

    public void setRecurrencesDueToday(List<RecurrenceResponseDTO> recurrencesDueToday) {
        this.recurrencesDueToday = recurrencesDueToday;
    }

    public List<RecurrenceResponseDTO> getRecurrencesOverdue() {
        return recurrencesOverdue;
    }

    public void setRecurrencesOverdue(List<RecurrenceResponseDTO> recurrencesOverdue) {
        this.recurrencesOverdue = recurrencesOverdue;
    }

    public List<RecurrenceResponseDTO> getRecurrencesUpcoming() {
        return recurrencesUpcoming;
    }

    public void setRecurrencesUpcoming(List<RecurrenceResponseDTO> recurrencesUpcoming) {
        this.recurrencesUpcoming = recurrencesUpcoming;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AllRecurrenceResponseDTO that = (AllRecurrenceResponseDTO) o;
        return Objects.equals(getTotalRegistered(), that.getTotalRegistered()) && Objects.equals(getTotalIncomeAmount(), that.getTotalIncomeAmount()) && Objects.equals(getTotalExpenseAmount(), that.getTotalExpenseAmount()) && Objects.equals(getMonthlyImpact(), that.getMonthlyImpact()) && Objects.equals(getRecurrencesDueToday(), that.getRecurrencesDueToday()) && Objects.equals(getRecurrencesOverdue(), that.getRecurrencesOverdue()) && Objects.equals(getRecurrencesUpcoming(), that.getRecurrencesUpcoming());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getTotalRegistered());
        result = 31 * result + Objects.hashCode(getTotalIncomeAmount());
        result = 31 * result + Objects.hashCode(getTotalExpenseAmount());
        result = 31 * result + Objects.hashCode(getMonthlyImpact());
        result = 31 * result + Objects.hashCode(getRecurrencesDueToday());
        result = 31 * result + Objects.hashCode(getRecurrencesOverdue());
        result = 31 * result + Objects.hashCode(getRecurrencesUpcoming());
        return result;
    }
}