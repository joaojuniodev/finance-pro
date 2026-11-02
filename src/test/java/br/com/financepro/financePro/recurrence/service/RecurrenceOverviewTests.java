package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecurrenceOverviewTests {
    @Test
    void overviewUsesOneAccountScopedListAndGroupsByStatusAndDate() {
        LocalDate today = LocalDate.now();
        List<Recurrence> items = List.of(
            recurrence(RecurrenceStatus.ACTIVE, ExecutionType.MANUALLY, today),
            recurrence(RecurrenceStatus.ACTIVE, ExecutionType.AUTOMATIC, today.minusDays(1)),
            recurrence(RecurrenceStatus.ACTIVE, ExecutionType.AUTOMATIC, today.plusMonths(2)),
            recurrence(RecurrenceStatus.PAUSED, ExecutionType.MANUALLY, today.plusDays(2))
        );
        RecurrenceRepository repository = (RecurrenceRepository) Proxy.newProxyInstance(
            RecurrenceRepository.class.getClassLoader(), new Class<?>[]{RecurrenceRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("findAll")) return items;
                throw new AssertionError("Consulta inesperada: " + method.getName());
            });
        RecurrenceMapper mapper = new RecurrenceMapper() {
            @Override
            public RecurrenceResponseDTO toResponse(Recurrence entity) {
                return new RecurrenceResponseDTO(entity.getId(), entity.getAmount(), entity.getType(),
                    entity.getFrequencyType(), entity.getExecutionType(), 10, null, null,
                    entity.getNextExecutionDate(), null, entity.getStatus(), "Teste", null, null);
            }
        };
        RecurrenceReadService service = new RecurrenceReadService();
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "mapper", mapper);

        var result = service.getOverview(UUID.randomUUID());

        assertEquals(4, result.getRecurrences().size());
        assertEquals(3, result.getTotalActives());
        assertEquals(1, result.getRecurrencesDueToday().size());
        assertEquals(1, result.getRecurrencesOverdue().size());
        assertEquals(1, result.getRecurrencesUpcoming().size());
    }

    private Recurrence recurrence(RecurrenceStatus status, ExecutionType type, LocalDate date) {
        Recurrence recurrence = new Recurrence();
        recurrence.setId(UUID.randomUUID());
        recurrence.setStatus(status);
        recurrence.setExecutionType(type);
        recurrence.setFrequencyType(FrequencyType.MONTHLY);
        recurrence.setType(RecurrenceType.DEBIT);
        recurrence.setAmount(BigDecimal.TEN);
        recurrence.setNextExecutionDate(date);
        return recurrence;
    }
}
