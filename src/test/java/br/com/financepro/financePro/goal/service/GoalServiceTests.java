package br.com.financepro.financePro.goal.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.exception.InvalidGoalException;
import br.com.financepro.financePro.goal.model.Goal;
import br.com.financepro.financePro.goal.repository.GoalRepository;
import br.com.financepro.financePro.mapper.goal.GoalMapper;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoalServiceTests {
    private GoalService service;
    private Account account;
    private List<Goal> stored;

    @BeforeEach
    void setUp() {
        account = new Account(UUID.randomUUID(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        stored = new ArrayList<>();
        AccountRepository accounts = proxy(AccountRepository.class, (instance, method, args) -> {
            if (method.getName().equals("findByUsername")) {
                return "alice".equals(args[0]) ? Optional.of(account) : Optional.empty();
            }
            throw new UnsupportedOperationException(method.getName());
        });
        GoalRepository goals = proxy(GoalRepository.class, (instance, method, args) -> {
            if (method.getName().equals("save")) {
                Goal goal = (Goal) args[0];
                if (goal.getId() == null) goal.setId(UUID.randomUUID());
                stored.add(goal);
                return goal;
            }
            if (method.getName().equals("findByAccountIdOrderByNameAsc")) {
                return stored.stream().filter(goal -> goal.getAccount().getId().equals(args[0])).toList();
            }
            if (method.getName().equals("findByIdAndAccountId")) {
                return stored.stream().filter(goal -> goal.getId().equals(args[0])
                    && goal.getAccount().getId().equals(args[1])).findFirst();
            }
            throw new UnsupportedOperationException(method.getName());
        });
        CategoryRepository categories = proxy(CategoryRepository.class, (instance, method, args) -> {
            throw new UnsupportedOperationException(method.getName());
        });
        WalletRepository wallets = proxy(WalletRepository.class, (instance, method, args) -> {
            throw new UnsupportedOperationException(method.getName());
        });
        service = new GoalService(goals, new GoalMapper(), accounts, categories, wallets);
    }

    @Test
    void createsGoalForAuthenticatedAccountAndReturnsItInList() {
        var created = service.create(request("Viagem", "5000.00", "250.00"), "alice");

        assertEquals("Viagem", created.getName());
        assertEquals(new BigDecimal("250.00"), created.getCurrentAmount());
        assertEquals(account.getId(), stored.get(0).getAccount().getId());
        assertEquals(1, service.getByAccount(account.getId(), "alice").size());
    }

    @Test
    void rejectsAnotherAccountAndUnknownUser() {
        service.create(request("Viagem", "5000.00", "0.00"), "alice");
        assertThrows(NotFoundException.class, () -> service.getByAccount(UUID.randomUUID(), "alice"));
        assertThrows(NotFoundException.class, () -> service.getById(stored.get(0).getId(), "bob"));
        assertThrows(NotFoundException.class,
            () -> service.create(request("Outra", "100.00", "0.00"), "bob"));
        assertEquals(1, stored.size());
    }

    @Test
    void rejectsInvalidAmountsWithoutSaving() {
        assertThrows(InvalidGoalException.class,
            () -> service.create(request("Viagem", "100.00", "100.01"), "alice"));
        assertThrows(InvalidGoalException.class,
            () -> service.create(request("Viagem", "0.00", "0.00"), "alice"));
        assertThrows(InvalidGoalException.class,
            () -> service.create(request("Viagem", "100.001", "0.00"), "alice"));
        assertEquals(0, stored.size());
    }

    private GoalRequestDTO request(String name, String target, String initial) {
        return new GoalRequestDTO(null, name, "", new BigDecimal(target),
            new BigDecimal(initial), null, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }
}
