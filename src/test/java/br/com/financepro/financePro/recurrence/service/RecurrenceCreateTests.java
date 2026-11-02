package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.invoice.service.InvoiceRecurrenceService;
import br.com.financepro.financePro.common.exceptions.RecurrenceSaveException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.dto.request.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.service.TransactionExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecurrenceCreateTests {
    @Test
    void cardRecurrenceIsManualAndSentToTheInvoice() {
        RecurrenceSaveService service = service(new AtomicInteger());
        AtomicInteger linked = new AtomicInteger();
        RecurrenceMapper mapper = new RecurrenceMapper() {
            @Override public Recurrence toEntity(RecurrenceRequestDTO request) {
                Wallet card = new Wallet();
                card.setType(WalletType.CREDIT_CARD);
                return new Recurrence(null, request.getAmount(), request.getType(), request.getStatus(),
                    request.getFrequencyType(), request.getExecutionType(), request.getDayOne(), request.getDayTwo(),
                    request.getMonthOfTheYear(), request.getDescription(), null, card, null);
            }
            @Override public RecurrenceResponseDTO toResponse(Recurrence recurrence) {
                RecurrenceResponseDTO response = new RecurrenceResponseDTO();
                response.setExecutionType(recurrence.getExecutionType());
                return response;
            }
        };
        ReflectionTestUtils.setField(service, "mapper", mapper);
        ReflectionTestUtils.setField(service, "invoiceRecurrences", new InvoiceRecurrenceService(null, null, null, null) {
            @Override public void addToOpenInvoice(Recurrence recurrence) { linked.incrementAndGet(); }
        });
        var request = request(FrequencyType.MONTHLY);
        assertEquals(ExecutionType.MANUALLY, service.create(request).getExecutionType());
        assertEquals(1, linked.get());
    }

    @Test
    void createsRecurrencesWithFutureExecutionForEachFrequency() {
        for (FrequencyType frequency : FrequencyType.values()) {
            AtomicInteger saves = new AtomicInteger();
            RecurrenceSaveService service = service(saves);
            RecurrenceRequestDTO request = request(frequency);
            RecurrenceResponseDTO result = service.create(request);
            assertEquals(2, saves.get());
            assertFalse(result.getNextExecutionDate().isBefore(LocalDate.now()));
        }
    }

    @Test
    void rejectsInvalidScheduleBeforePersisting() {
        AtomicInteger saves = new AtomicInteger();
        RecurrenceRequestDTO request = request(FrequencyType.BIWEEKLY);
        request.setDayTwo(request.getDayOne());
        assertThrows(RecurrenceSaveException.class, () -> service(saves).create(request));
        assertEquals(0, saves.get());
    }

    @Test
    void annualAlreadyExecutedRecordsTransactionAndSchedulesNextYear() {
        LocalDate today = LocalDate.now();
        AtomicInteger executions = new AtomicInteger();
        RecurrenceSaveService service = service(new AtomicInteger());
        ReflectionTestUtils.setField(service, "transactionExecutionService", new TransactionExecutionService() {
            @Override public void createByRecurrence(Recurrence recurrence, LocalDate date) {
                assertEquals(today, date);
                executions.incrementAndGet();
            }
        });
        RecurrenceRequestDTO request = request(FrequencyType.YEARLY);
        request.setDayOne(today.getDayOfMonth());
        request.setMonthOfTheYear(today.getMonthValue());
        request.setMonthOfTheYearAlreadyOccurred(true);
        RecurrenceResponseDTO response = service.create(request);
        assertEquals(1, executions.get());
        assertEquals(today.plusYears(1), response.getNextExecutionDate());
    }

    private RecurrenceSaveService service(AtomicInteger saves) {
        RecurrenceRepository repository = (RecurrenceRepository) Proxy.newProxyInstance(
            RecurrenceRepository.class.getClassLoader(), new Class<?>[]{RecurrenceRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("save")) { saves.incrementAndGet(); return args[0]; }
                throw new UnsupportedOperationException(method.getName());
            });
        RecurrenceMapper mapper = new RecurrenceMapper() {
            @Override public Recurrence toEntity(RecurrenceRequestDTO request) {
                return new Recurrence(null, request.getAmount(), request.getType(), request.getStatus(),
                    request.getFrequencyType(), request.getExecutionType(), request.getDayOne(),
                    request.getDayTwo(), request.getMonthOfTheYear(), request.getDescription(), null, null, null);
            }
            @Override public RecurrenceResponseDTO toResponse(Recurrence recurrence) {
                RecurrenceResponseDTO response = new RecurrenceResponseDTO();
                response.setNextExecutionDate(recurrence.getNextExecutionDate());
                return response;
            }
        };
        RecurrenceSaveService service = new RecurrenceSaveService();
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "mapper", mapper);
        return service;
    }

    private RecurrenceRequestDTO request(FrequencyType frequency) {
        RecurrenceRequestDTO request = new RecurrenceRequestDTO();
        request.setAmount(new BigDecimal("20.00"));
        request.setType(RecurrenceType.DEBIT);
        request.setStatus(RecurrenceStatus.ACTIVE);
        request.setFrequencyType(frequency);
        request.setExecutionType(ExecutionType.AUTOMATIC);
        request.setDayOne(10);
        request.setDayTwo(20);
        request.setMonthOfTheYear(5);
        request.setDescription("Conta");
        request.setAccountId(UUID.randomUUID());
        request.setWalletId(UUID.randomUUID());
        request.setCategoryId(UUID.randomUUID());
        return request;
    }
}
