package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class RecurrenceExecutionService {

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    public void executeDueRecurrences(LocalDate today) {
        List<Recurrence> recurrences =
            recurrenceRepository.findByActiveTrueAndNextExecutionDateLessThanEqual(today);

        for (Recurrence recurrence : recurrences) {
            execute(recurrence, today);
        }
    }

    private void execute(Recurrence recurrence, LocalDate executionDate) {
        transactionService.createByRecurrence(recurrence, executionDate);

        recurrence.setLastExecutionDate(executionDate);
        recurrence.setNextExecutionDate(calculateNextExecutionDate(recurrence, executionDate));

        recurrenceRepository.save(recurrence);
    }

    private LocalDate calculateNextExecutionDate(Recurrence recurrence, LocalDate lastExecutionDate) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> dateInMonth(
                YearMonth.from(lastExecutionDate).plusMonths(1),
                recurrence.getDayOne()
            );

            case YEARLY -> dateInYear(
                lastExecutionDate.getYear() + 1,
                recurrence.getMonthOfTheYear(),
                recurrence.getDayOne()
            );

            case BIWEEKLY -> calculateNextBiweeklyDate(recurrence, lastExecutionDate);
        };
    }

    private LocalDate calculateNextBiweeklyDate(Recurrence recurrence, LocalDate lastExecutionDate) {
        YearMonth currentMonth = YearMonth.from(lastExecutionDate);

        LocalDate firstDate = dateInMonth(currentMonth, recurrence.getDayOne());

        if (lastExecutionDate.equals(firstDate)) {
            return dateInMonth(currentMonth, recurrence.getDayTwo());
        }

        return dateInMonth(currentMonth.plusMonths(1), recurrence.getDayOne());
    }

    private LocalDate dateInMonth(YearMonth yearMonth, Integer day) {
        int validDay = Math.min(day, yearMonth.lengthOfMonth());
        return yearMonth.atDay(validDay);
    }

    private LocalDate dateInYear(int year, Integer month, Integer day) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return dateInMonth(yearMonth, day);
    }
}