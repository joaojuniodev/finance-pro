package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.dto.response.AllRecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.recurrence.repository.spec.RecurrenceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RecurrenceReadService {

    private final Logger log = LoggerFactory.getLogger(RecurrenceReadService.class.getName());

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

    public AllRecurrenceResponseDTO getOverview(UUID accountId) {
        log.info("Getting Overview this Recurrence");

        RecurrenceSpecification spec = new RecurrenceSpecification();
        spec.addSpecifications(accountId);

        var recurrences = repository
            .findAll(spec.apply())
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();

        final Integer totalActives = (int) recurrences.stream()
            .filter(rec -> rec.getStatus().equals(RecurrenceStatus.ACTIVE))
            .count();

        final BigDecimal totalIncomeAmount = recurrences.stream()
            .filter(rec -> rec.getStatus().equals(RecurrenceStatus.ACTIVE))
            .filter(rec -> rec.getType().equals(RecurrenceType.CREDIT))
            .map(RecurrenceResponseDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalExpenseAmount = recurrences.stream()
            .filter(rec -> rec.getStatus().equals(RecurrenceStatus.ACTIVE))
            .filter(rec -> rec.getType().equals(RecurrenceType.DEBIT))
            .map(RecurrenceResponseDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return mapper.getAllRecurrenceResponseDTO(
            totalActives,
            recurrences,
            getRecurrencesDueToday(),
            getRecurrencesOverdue(),
            getRecurrencesUpcoming(),
            getHighlightsOfTheWeek(recurrences),
            totalIncomeAmount,
            totalExpenseAmount
        );
    }

    private List<RecurrenceResponseDTO> getRecurrencesDueToday() {
        return repository.findPendingThisTodayManualRecurrences(ExecutionType.MANUALLY, LocalDate.now())
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    private List<RecurrenceResponseDTO> getRecurrencesOverdue() {
        return repository.findDelayedRecurrences(LocalDate.now())
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    private List<RecurrenceResponseDTO> getRecurrencesUpcoming() {
        LocalDate today = LocalDate.now();
        return repository.findUpcomingRecurrences(today, today.plusMonths(1))
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    private List<RecurrenceResponseDTO> getHighlightsOfTheWeek(List<RecurrenceResponseDTO> recurrences) {
        LocalDate startDate = LocalDate.now().plusDays(7);
        return recurrences.stream()
            .filter(rec -> rec.getStatus().equals(RecurrenceStatus.ACTIVE))
            .filter(rec -> rec.getNextExecutionDate().isBefore(startDate))
            .toList();
    }

    public RecurrenceResponseDTO getById(UUID id) {
        log.info("Getting Recurrence by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }
}