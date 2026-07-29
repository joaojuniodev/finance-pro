package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.RecurrenceSort;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.dto.AllRecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.recurrence.repository.spec.RecurrenceSpecification;
import br.com.financepro.financePro.recurrence.service.params.RecurrenceSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public List<RecurrenceResponseDTO> getAll(RecurrenceSearchParams params) {
        log.info("Getting All Recurrences");

        RecurrenceSpecification spec = new RecurrenceSpecification(params);

        return repository
            .findAll(spec.apply(), sort(params.sort()))
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public AllRecurrenceResponseDTO getOverview(UUID accountId) {
        log.info("Getting Overview this Recurrence");

        RecurrenceSearchParams params = new RecurrenceSearchParams(accountId, null, null, null, null, null);
        RecurrenceSpecification spec = new RecurrenceSpecification(params);

        var recurrences = repository.findAll(spec.apply());

        final BigDecimal totalIncomeAmount = recurrences.stream()
            .filter(rec -> rec.getActive().equals(Boolean.TRUE))
            .filter(rec -> rec.getType().equals(RecurrenceType.CREDIT))
            .map(Recurrence::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal totalExpenseAmount = recurrences.stream()
            .filter(rec -> rec.getActive().equals(Boolean.TRUE))
            .filter(rec -> rec.getType().equals(RecurrenceType.DEBIT))
            .map(Recurrence::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return mapper.getAllRecurrenceResponseDTO(
            (long) recurrences.size(),
            getRecurrencesDueToday(),
            getRecurrencesOverdue(),
            getRecurrencesUpcoming(),
            totalIncomeAmount,
            totalExpenseAmount
        );
    }

    private Sort sort(RecurrenceSort sortType) {
        if (sortType == null) {
            return Sort.by("nextExecutionDate").ascending();
        }

        return switch (sortType) {
            case NEAREST_DATE -> Sort.by("nextExecutionDate").ascending();
            case HIGHEST_AMOUNT -> Sort.by("amount").descending();
            case ALPHABETICAL -> Sort.by("description").ascending();
        };
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
        return repository.findUpcomingRecurrences(today, today.plusDays(6))
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    public RecurrenceResponseDTO getById(UUID id) {
        log.info("Getting Recurrence by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }
}