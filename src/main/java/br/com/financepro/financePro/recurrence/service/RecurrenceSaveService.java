package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.common.exceptions.RecurrenceSaveException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.dto.request.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceConfirmResponseDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.service.TransactionExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import java.util.UUID;

@Service
public class RecurrenceSaveService {

    private final Logger log = LoggerFactory.getLogger(RecurrenceSaveService.class.getName());

    @Autowired
    private RecurrenceRepository repository;

    @Autowired
    private RecurrenceExecutionService executionService;

    @Autowired
    private TransactionExecutionService transactionExecutionService;

    @Autowired
    private RecurrenceMapper mapper;

    public RecurrenceResponseDTO create(RecurrenceRequestDTO recurrence) {
        log.info("Creating Recurrence");

        LocalDate today = LocalDate.now();
        validateSave(recurrence, today);

        var entity = mapper.toEntity(recurrence);
        entity.setLastExecutionDate(null);
        entity.setNextExecutionDate(calculateFirstExecutionDate(entity, today, entity.getLastExecutionDate()));

        var recurrenceCreated = repository.save(entity);

        if (recurrence.getDayOneAlreadyOccurred()) {
            LocalDate of = LocalDate.of(today.getYear(), today.getMonth(), recurrence.getDayOne());
            transactionExecutionService.createByRecurrence(recurrenceCreated, of);
            recurrenceCreated.setLastExecutionDate(of);
        }

        if (recurrence.getDayTwoAlreadyOccurred()) {
            LocalDate of = LocalDate.of(today.getYear(), today.getMonth(), recurrence.getDayTwo());
            transactionExecutionService.createByRecurrence(recurrenceCreated, of);
            recurrenceCreated.setLastExecutionDate(of);
        }

        repository.save(recurrenceCreated);
        return mapper.toResponse(recurrenceCreated);
    }

    @Transactional
    public RecurrenceResponseDTO update(RecurrenceRequestDTO recurrence) {
        log.info("Updating Recurrence");

        LocalDate today = LocalDate.now();
        validateUpdate(recurrence, today);

        var entity = repository.findById(recurrence.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrence.getId()));
        entity.setAmount(recurrence.getAmount());
        entity.setDescription(recurrence.getDescription());
        entity.setType(recurrence.getType());
        entity.setExecutionType(recurrence.getExecutionType());
        entity.setFrequencyType(recurrence.getFrequencyType());
        entity.setMonthOfTheYear(recurrence.getMonthOfTheYear());
        var recurrenceInDatabase = repository.save(entity);

        updateExecution(recurrence, recurrenceInDatabase, today);

        var recurrenceUpdated = repository.save(recurrenceInDatabase);
        return mapper.toResponse(recurrenceUpdated);
    }

    @Transactional
    public void confirm(UUID recurrenceId) {
        var entity = repository.findById(recurrenceId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        executionService.execute(entity, LocalDate.now(), true);
    }

    @Transactional
    public void pause(UUID recurrenceId) {
        var entity = repository.findById(recurrenceId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.PAUSED);
        repository.save(entity);
    }

    @Transactional
    public void activate(UUID recurrenceId) {
        var entity = repository.findById(recurrenceId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.ACTIVE);
        repository.save(entity);
    }

    @Transactional
    public void finish(UUID recurrenceId) {
        var entity = repository.findById(recurrenceId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.ENDED);
        repository.save(entity);
    }

    // TODO: ao deletar recorrência, as transações referentes também devem ser deletadas.
    public void delete(UUID id) {
        log.info("Deleting Recurrence");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }

    private void validateSave(RecurrenceRequestDTO recurrence, LocalDate today) {
        if (recurrence.getDayOneAlreadyOccurred() && recurrence.getExecutionType().equals(ExecutionType.MANUALLY)) {
            throw new RecurrenceSaveException("A Recorrência não pode ser efetivada se o tipo de execução é manual.");
        }

        if (recurrence.getMonthOfTheYear() != null) {
            if (recurrence.getMonthOfTheYear() > today.getMonthValue() && recurrence.getMonthOfTheYearAlreadyOccurred()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o mês precisa ser anterior ao atual.");
            }
        }

        if (recurrence.getDayOne() != null) {
            if (recurrence.getDayOne() > today.getDayOfMonth() && recurrence.getDayOneAlreadyOccurred()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o dia precisa ser anterior ao atual.");
            }
        }

        if (recurrence.getDayTwo() != null) {
            if (recurrence.getDayTwo() > today.getDayOfMonth() && recurrence.getDayTwoAlreadyOccurred()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o segundo dia precisa ser anterior ao atual.");
            }
        }
    }

    private void validateUpdate(RecurrenceRequestDTO recurrence, LocalDate today) {
        boolean anyAlreadyOccurredFlag = Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
            || Boolean.TRUE.equals(recurrence.getDayTwoAlreadyOccurred())
            || Boolean.TRUE.equals(recurrence.getMonthOfTheYearAlreadyOccurred());

        if (anyAlreadyOccurredFlag && recurrence.getExecutionType() == ExecutionType.MANUALLY) {
            throw new RecurrenceSaveException("A Recorrência não pode ser efetivada se o tipo de execução é manual");
        }

        if (recurrence.getFrequencyType() == FrequencyType.MONTHLY
            && recurrence.getDayOne() != null
            && Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
            && recurrence.getDayOne() > today.getDayOfMonth()) {
            throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o dia precisa ser anterior ao atual.");
        }

        if (recurrence.getFrequencyType() == FrequencyType.BIWEEKLY) {
            if (recurrence.getDayOne() != null
                && Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
                && recurrence.getDayOne() > today.getDayOfMonth()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o dia precisa ser anterior ao atual.");
            }
            if (recurrence.getDayTwo() != null
                && Boolean.TRUE.equals(recurrence.getDayTwoAlreadyOccurred())
                && recurrence.getDayTwo() > today.getDayOfMonth()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o segundo dia precisa ser anterior ao atual.");
            }
        }

        if (recurrence.getFrequencyType() == FrequencyType.YEARLY
            && recurrence.getMonthOfTheYear() != null
            && recurrence.getDayOne() != null
            && Boolean.TRUE.equals(recurrence.getMonthOfTheYearAlreadyOccurred())) {

            YearMonth targetYearMonth = YearMonth.of(today.getYear(), recurrence.getMonthOfTheYear());
            LocalDate targetDate = dateInMonth(targetYearMonth, recurrence.getDayOne());

            if (targetDate.isAfter(today)) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, a data (mês e dia) precisa ser anterior ou igual à data atual.");
            }
        }
    }

    private void updateExecution(RecurrenceRequestDTO recurrence, Recurrence recurrenceInDatabase, LocalDate today) {
        switch (recurrence.getFrequencyType()) {
            case MONTHLY -> {
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }

                if (recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
            case BIWEEKLY -> {
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }
                if (!Objects.equals(recurrenceInDatabase.getDayTwo(), recurrence.getDayTwo())) {
                    recurrenceInDatabase.setDayTwo(recurrence.getDayTwo());
                }

                if (recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }
                if (recurrence.getDayTwoAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayTwo());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
            case YEARLY -> {
                if (!Objects.equals(recurrenceInDatabase.getMonthOfTheYear(), recurrence.getMonthOfTheYear())) {
                    recurrenceInDatabase.setMonthOfTheYear(recurrence.getMonthOfTheYear());
                }
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }

                if (recurrence.getMonthOfTheYearAlreadyOccurred() || recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInYear(today.getYear(), recurrence.getMonthOfTheYear(), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
        }
    }

    private LocalDate calculateFirstExecutionDate(Recurrence recurrence, LocalDate today, LocalDate lastExecutionDate) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> {

                YearMonth current = YearMonth.from(today);

                if (lastExecutionDate != null &&
                    YearMonth.from(lastExecutionDate).equals(current)) {

                    yield dateInMonth(current.plusMonths(1), recurrence.getDayOne());
                }

                LocalDate candidate = dateInMonth(current, recurrence.getDayOne());

                if (candidate.isBefore(today)) {
                    candidate = dateInMonth(current.plusMonths(1), recurrence.getDayOne());
                }

                yield candidate;
            }

            case BIWEEKLY -> {
                YearMonth currentMonth = YearMonth.from(today);

                LocalDate firstDate = dateInMonth(currentMonth, recurrence.getDayOne());
                LocalDate secondDate = dateInMonth(currentMonth, recurrence.getDayTwo());

                if (!firstDate.isBefore(today)) {
                    yield firstDate;
                }

                if (!secondDate.isBefore(today)) {
                    yield secondDate;
                }

                yield dateInMonth(currentMonth.plusMonths(1), recurrence.getDayOne());
            }

            case YEARLY -> {
                LocalDate candidate = dateInYear(
                    today.getYear(),
                    recurrence.getMonthOfTheYear(),
                    recurrence.getDayOne()
                );

                if (candidate.isBefore(today)) {
                    candidate = dateInYear(
                        today.getYear() + 1,
                        recurrence.getMonthOfTheYear(),
                        recurrence.getDayOne()
                    );
                }

                yield candidate;
            }
        };
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