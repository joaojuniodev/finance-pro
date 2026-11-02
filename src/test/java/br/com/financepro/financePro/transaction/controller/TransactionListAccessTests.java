package br.com.financepro.financePro.transaction.controller;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionListAccessTests {
    @Test
    void listsOnlyTheAuthenticatedUsersAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);
        AccountRepository accounts = (AccountRepository) Proxy.newProxyInstance(
            AccountRepository.class.getClassLoader(), new Class<?>[]{AccountRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("findByUsername")) return Optional.of(account);
                throw new UnsupportedOperationException(method.getName());
            });
        TransactionRepository transactions = (TransactionRepository) Proxy.newProxyInstance(
            TransactionRepository.class.getClassLoader(), new Class<?>[]{TransactionRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("findAll")) return List.of();
                throw new UnsupportedOperationException(method.getName());
            });
        TransactionService service = new TransactionService();
        ReflectionTestUtils.setField(service, "accountRepository", accounts);
        ReflectionTestUtils.setField(service, "repository", transactions);
        ReflectionTestUtils.setField(service, "mapper", new TransactionMapper());
        TransactionController controller = new TransactionController();
        ReflectionTestUtils.setField(controller, "service", service);
        Principal principal = () -> "alice";

        assertEquals(200, controller.getAll(accountId, null, 11, 2026, principal).getStatusCode().value());
        assertThrows(NotFoundException.class, () -> controller.getAll(UUID.randomUUID(), null, 11, 2026, principal));
        assertThrows(NotFoundException.class, () -> controller.getAll(null, null, 11, 2026, principal));
        assertThrows(NotFoundException.class, () -> service.getAll(accountId, null, 11, 2026, null));
        assertThrows(NotFoundException.class, () -> service.getOverview(UUID.randomUUID(), null, 11, 2026, "alice"));
    }
}
