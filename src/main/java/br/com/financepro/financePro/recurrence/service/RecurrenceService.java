package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.dto.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.recurrence.repository.spec.RecurrenceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class RecurrenceService {

    private final Logger log = LoggerFactory.getLogger(RecurrenceService.class.getName());

    @Autowired
    private RecurrenceRepository repository;

    @Autowired
    private RecurrenceMapper mapper;

    public List<RecurrenceResponseDTO> getAll(UUID accountId) {
        log.info("Getting All Recurrences");

        RecurrenceSpecification spec = new RecurrenceSpecification();
        spec.addSpecifications(accountId);

        return repository
            .findAll(spec.apply())
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public RecurrenceResponseDTO getById(UUID id) {
        log.info("Getting Recurrence by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public RecurrenceResponseDTO create(RecurrenceRequestDTO recurrence) {
        log.info("Creating Recurrence");

        Recurrence entity = mapper.toEntity(recurrence);
        entity.setActive(true);
        entity.setLastExecutionDate(null);
        entity.setNextExecutionDate(calculateFirstExecutionDate(entity, LocalDate.now()));

        var recurrenceCreated = repository.save(entity);
        return mapper.toResponse(recurrenceCreated);
    }

    public RecurrenceResponseDTO update(RecurrenceRequestDTO recurrence) {
        log.info("Updating Recurrence");

        var entity = repository.findById(recurrence.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrence.getId()));
        entity.setAmount(recurrence.getAmount());
        entity.setDayOne(recurrence.getDayOne());
        entity.setDayTwo(recurrence.getDayTwo());
        entity.setFrequencyType(recurrence.getFrequencyType());
        entity.setMonthOfTheYear(recurrence.getMonthOfTheYear());

        var recurrenceUpdated = repository.save(entity);
        return mapper.toResponse(recurrenceUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Recurrence");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }

    private LocalDate calculateFirstExecutionDate(Recurrence recurrence, LocalDate today) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> {
                YearMonth currentMonth = YearMonth.from(today);
                LocalDate candidate = dateInMonth(currentMonth, recurrence.getDayOne());

                if (candidate.isBefore(today)) {
                    candidate = dateInMonth(currentMonth.plusMonths(1), recurrence.getDayOne());
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