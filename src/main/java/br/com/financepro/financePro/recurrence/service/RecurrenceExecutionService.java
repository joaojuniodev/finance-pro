package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.service.TransactionExecutionService;
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
    private TransactionExecutionService transactionExecutionService;

    @Transactional
    public void executeDueRecurrences(LocalDate today) {
        List<Recurrence> recurrences =
            recurrenceRepository.findPendingAutomaticRecurrences(ExecutionType.AUTOMATIC, today);

        for (Recurrence recurrence : recurrences) {
            execute(recurrence, today, false);
        }
    }

    protected void execute(Recurrence recurrence, LocalDate executionDate, boolean isManually) {
        transactionExecutionService.createByRecurrence(recurrence, executionDate);

        // Precisa ser calculado ANTES de sobrescrever lastExecutionDate/nextExecutionDate,
        // pois a lógica manual depende do estado anterior da recorrência.
        boolean isFirstConfirmation = recurrence.getLastExecutionDate() == null;

        LocalDate nextExecutionDate = isManually
            ? calculateNextManualExecutionDate(recurrence, executionDate, isFirstConfirmation)
            : calculateNextExecutionDate(recurrence, executionDate);

        // last = data real em que o usuário efetivamente pagou/confirmou
        recurrence.setLastExecutionDate(executionDate);
        // next = sempre segue o dia "oficial" da recorrência (dayOne/dayTwo/monthOfTheYear),
        // nunca a data real do pagamento
        recurrence.setNextExecutionDate(nextExecutionDate);

        recurrenceRepository.save(recurrence);
    }

    /**
     * Fluxo automático: a própria execução já ocorre na data agendada,
     * então a data de referência para calcular a próxima é a data de execução em si.
     */
    public LocalDate calculateNextExecutionDate(Recurrence recurrence, LocalDate scheduledDate) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> dateInMonth(
                YearMonth.from(scheduledDate).plusMonths(1),
                recurrence.getDayOne()
            );

            case YEARLY -> dateInYear(
                scheduledDate.getYear() + 1,
                recurrence.getMonthOfTheYear(),
                recurrence.getDayOne()
            );

            case BIWEEKLY -> calculateNextBiweeklyDate(recurrence, scheduledDate);
        };
    }

    /**
     * Fluxo manual: o usuário pode confirmar em uma data diferente da agendada
     * (ex: recorrência todo dia 15, mas pagou dia 12). A próxima execução deve
     * continuar seguindo o dia oficial da recorrência, não a data real do pagamento.
     *
     * Por isso, em vez de usar executionDate como base, usamos a última data
     * agendada (recurrence.getNextExecutionDate(), ou seja, o que estava "devido"
     * antes dessa confirmação) como âncora para calcular a próxima.
     *
     * Se for a primeira confirmação (ainda não existe lastExecutionDate/nextExecutionDate
     * de referência), a data agendada "atual" é inferida a partir do mês/ano da própria
     * executionDate combinada com dayOne/dayTwo/monthOfTheYear.
     */
    public LocalDate calculateNextManualExecutionDate(
        Recurrence recurrence,
        LocalDate executionDate,
        boolean isFirstConfirmation
    ) {
        LocalDate scheduledDate = recurrence.getNextExecutionDate();

        if (isFirstConfirmation || scheduledDate == null) {
            scheduledDate = calculateInitialScheduledDate(recurrence, executionDate);
        }

        return calculateNextExecutionDate(recurrence, scheduledDate);
    }

    /**
     * Infere qual seria a data agendada "corrente" (a que está sendo paga agora)
     * quando ainda não existe nenhum histórico de execução, com base no
     * mês/ano da data de pagamento e nos dias configurados na recorrência.
     */
    private LocalDate calculateInitialScheduledDate(Recurrence recurrence, LocalDate executionDate) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> dateInMonth(
                YearMonth.from(executionDate),
                recurrence.getDayOne()
            );

            case YEARLY -> dateInYear(
                executionDate.getYear(),
                recurrence.getMonthOfTheYear(),
                recurrence.getDayOne()
            );

            case BIWEEKLY -> calculateInitialBiweeklyScheduledDate(recurrence, executionDate);
        };
    }

    private LocalDate calculateInitialBiweeklyScheduledDate(Recurrence recurrence, LocalDate executionDate) {
        YearMonth currentMonth = YearMonth.from(executionDate);

        LocalDate firstDate = dateInMonth(currentMonth, recurrence.getDayOne());
        LocalDate secondDate = dateInMonth(currentMonth, recurrence.getDayTwo());

        // Pagou antes/no dia da primeira parcela do mês -> essa é a parcela corrente
        if (!executionDate.isAfter(firstDate)) {
            return firstDate;
        }

        // Pagou entre a primeira e a segunda parcela (ou no dia da segunda) -> segunda é a corrente
        if (!executionDate.isAfter(secondDate)) {
            return secondDate;
        }

        // Pagou depois das duas parcelas do mês -> considera como cobrindo a primeira do próximo mês
        return dateInMonth(currentMonth.plusMonths(1), recurrence.getDayOne());
    }

    private LocalDate calculateNextBiweeklyDate(Recurrence recurrence, LocalDate scheduledDate) {
        YearMonth currentMonth = YearMonth.from(scheduledDate);

        LocalDate firstDate = dateInMonth(currentMonth, recurrence.getDayOne());

        if (scheduledDate.equals(firstDate)) {
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