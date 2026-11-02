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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

        LocalDate today = LocalDate.now();
        var dueToday = recurrences.stream().filter(rec -> rec.getStatus() == RecurrenceStatus.ACTIVE
            && rec.getExecutionType() == ExecutionType.MANUALLY && today.equals(rec.getNextExecutionDate())).toList();
        var overdue = recurrences.stream().filter(rec -> rec.getStatus() == RecurrenceStatus.ACTIVE
            && rec.getNextExecutionDate() != null && rec.getNextExecutionDate().isBefore(today)).toList();
        var upcoming = recurrences.stream().filter(rec -> rec.getStatus() == RecurrenceStatus.ACTIVE
            && rec.getNextExecutionDate() != null && !rec.getNextExecutionDate().isBefore(today)
            && !dueToday.contains(rec)).toList();

        return mapper.getAllRecurrenceResponseDTO(
            totalActives,
            recurrences,
            dueToday,
            overdue,
            upcoming,
            getHighlightsOfTheWeek(recurrences),
            totalIncomeAmount,
            totalExpenseAmount
        );
    }

    private List<RecurrenceResponseDTO> getHighlightsOfTheWeek(List<RecurrenceResponseDTO> recurrences) {
        LocalDate startDate = LocalDate.now().plusDays(7);
        return recurrences.stream()
            .filter(rec -> rec.getStatus().equals(RecurrenceStatus.ACTIVE))
            .filter(rec -> rec.getNextExecutionDate() != null && !rec.getNextExecutionDate().isBefore(LocalDate.now()) && rec.getNextExecutionDate().isBefore(startDate))
            .toList();
    }

    @Transactional(readOnly = true)
    public RecurrenceResponseDTO getById(UUID id, String username) {
        log.info("Getting Recurrence by Id");

        var entity = repository.findByIdAndAccountUserUsername(id, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }
}
