package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecurrenceDeletionTests {
    @Test
    void detachesTransactionsBeforeDeletingOwnedRecurrence() {
        UUID id = UUID.randomUUID();
        Recurrence recurrence = new Recurrence();
        recurrence.setId(id);
        List<String> actions = new ArrayList<>();
        RecurrenceSaveService service = service(id, recurrence, actions);

        service.delete(id, "alice");

        assertEquals(List.of("detach", "delete"), actions);
    }

    @Test
    void rejectsOtherUsersWithoutChangingTransactions() {
        UUID id = UUID.randomUUID();
        List<String> actions = new ArrayList<>();
        RecurrenceSaveService service = service(id, new Recurrence(), actions);

        assertThrows(NotFoundException.class, () -> service.delete(id, "bob"));
        assertEquals(List.of(), actions);
    }

    private RecurrenceSaveService service(UUID id, Recurrence recurrence, List<String> actions) {
        RecurrenceRepository recurrences = (RecurrenceRepository) Proxy.newProxyInstance(
            RecurrenceRepository.class.getClassLoader(), new Class<?>[]{RecurrenceRepository.class},
            (proxy, method, args) -> switch (method.getName()) {
                case "findByIdAndAccountUserUsername" -> id.equals(args[0]) && "alice".equals(args[1])
                    ? Optional.of(recurrence) : Optional.empty();
                case "delete" -> { actions.add("delete"); yield null; }
                default -> throw new UnsupportedOperationException(method.getName());
            });
        TransactionRepository transactions = (TransactionRepository) Proxy.newProxyInstance(
            TransactionRepository.class.getClassLoader(), new Class<?>[]{TransactionRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("detachRecurrence")) {
                    assertEquals(id, args[0]);
                    actions.add("detach");
                    return 1;
                }
                throw new UnsupportedOperationException(method.getName());
            });
        RecurrenceSaveService service = new RecurrenceSaveService();
        ReflectionTestUtils.setField(service, "repository", recurrences);
        ReflectionTestUtils.setField(service, "transactionRepository", transactions);
        return service;
    }
}
