package br.com.financepro.financePro.recurrence.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AllRecurrenceResponseDTO {

    private Integer totalActives;
    private BigDecimal totalIncomeAmount;
    private BigDecimal totalExpenseAmount;
    private BigDecimal monthlyImpact;
    private List<RecurrenceResponseDTO> recurrences;
    private List<RecurrenceResponseDTO> recurrencesDueToday;
    private List<RecurrenceResponseDTO> recurrencesOverdue;
    private List<RecurrenceResponseDTO> recurrencesUpcoming;
    private List<RecurrenceResponseDTO> recurrencesHighlightsOfTheWeek;

    public AllRecurrenceResponseDTO() {}

    public Integer getTotalActives() {
        return totalActives;
    }

    public void setTotalActives(Integer totalActives) {
        this.totalActives = totalActives;
    }

    public List<RecurrenceResponseDTO> getRecurrences() {
        return recurrences;
    }

    public void setRecurrences(List<RecurrenceResponseDTO> recurrences) {
        this.recurrences = recurrences;
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

    public List<RecurrenceResponseDTO> getRecurrencesHighlightsOfTheWeek() {
        return recurrencesHighlightsOfTheWeek;
    }

    public void setRecurrencesHighlightsOfTheWeek(List<RecurrenceResponseDTO> recurrencesHighlightsOfTheWeek) {
        this.recurrencesHighlightsOfTheWeek = recurrencesHighlightsOfTheWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AllRecurrenceResponseDTO that = (AllRecurrenceResponseDTO) o;
        return Objects.equals(getTotalActives(), that.getTotalActives()) && Objects.equals(getTotalIncomeAmount(), that.getTotalIncomeAmount()) && Objects.equals(getTotalExpenseAmount(), that.getTotalExpenseAmount()) && Objects.equals(getMonthlyImpact(), that.getMonthlyImpact()) && Objects.equals(getRecurrences(), that.getRecurrences()) && Objects.equals(getRecurrencesDueToday(), that.getRecurrencesDueToday()) && Objects.equals(getRecurrencesOverdue(), that.getRecurrencesOverdue()) && Objects.equals(getRecurrencesUpcoming(), that.getRecurrencesUpcoming()) && Objects.equals(getRecurrencesHighlightsOfTheWeek(), that.getRecurrencesHighlightsOfTheWeek());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getTotalActives());
        result = 31 * result + Objects.hashCode(getTotalIncomeAmount());
        result = 31 * result + Objects.hashCode(getTotalExpenseAmount());
        result = 31 * result + Objects.hashCode(getMonthlyImpact());
        result = 31 * result + Objects.hashCode(getRecurrences());
        result = 31 * result + Objects.hashCode(getRecurrencesDueToday());
        result = 31 * result + Objects.hashCode(getRecurrencesOverdue());
        result = 31 * result + Objects.hashCode(getRecurrencesUpcoming());
        result = 31 * result + Objects.hashCode(getRecurrencesHighlightsOfTheWeek());
        return result;
    }
}