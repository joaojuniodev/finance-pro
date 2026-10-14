package br.com.financepro.financePro.account.service.analytics;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.shared.AccountBalanceTrajectoryService;
import br.com.financepro.financePro.account.service.shared.AccountEvolutionService;
import br.com.financepro.financePro.account.service.shared.AccountSpendingCategoriesService;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RecurrenceRepository recurrenceRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RecurrenceMapper recurrenceMapper;

    @Mock
    private AccountSpendingCategoriesService spendingCategoriesService;

    @Mock
    private AccountEvolutionService evolutionService;

    @Mock
    private AccountBalanceTrajectoryService trajectoryService;

    @InjectMocks
    private AnalyticsService service;

    @Test
    void shouldMapTransactionsAndRecurrencesInsideReadOnlyTransaction() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setIncome(new BigDecimal("1000.00"));
        account.setExpenses(new BigDecimal("250.00"));
        Transaction transaction = new Transaction();
        Recurrence recurrence = new Recurrence();
        TransactionResponseDTO transactionResponse = mock(TransactionResponseDTO.class);
        RecurrenceResponseDTO recurrenceResponse = mock(RecurrenceResponseDTO.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findAll(anySpecification(), any(Sort.class)))
            .thenReturn(List.of(transaction));
        when(recurrenceRepository.findAll(anySpecification())).thenReturn(List.of(recurrence));
        when(transactionMapper.toResponse(transaction)).thenAnswer(invocation -> {
            assertReadOnlyTransactionActive();
            return transactionResponse;
        });
        when(recurrenceMapper.toResponse(recurrence)).thenAnswer(invocation -> {
            assertReadOnlyTransactionActive();
            return recurrenceResponse;
        });
        when(recurrenceResponse.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(spendingCategoriesService.getTopSpendingCategories(accountId, 6)).thenReturn(List.of());
        when(evolutionService.getWeeklyEvolution(accountId)).thenReturn(List.of());
        when(trajectoryService.getMonthlyBalanceTrajectory(eq(accountId), any(YearMonth.class)))
            .thenReturn(List.of());

        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(new ReadOnlyTestTransactionManager());
        interceptor.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(interceptor);
        AnalyticsService transactionalService = (AnalyticsService) proxyFactory.getProxy();

        var response = transactionalService.getAnalytics(accountId, 6);

        assertEquals(new BigDecimal("900.00"), response.availableToSpend());
        assertEquals(account.getIncome(), response.income());
        assertEquals(account.getExpenses(), response.expense());
        assertEquals(new BigDecimal("100.00"), response.commitments());
        assertEquals(List.of(transactionResponse), response.transactions());
        assertEquals(List.of(recurrenceResponse), response.recurrences());
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    private static <T> Specification<T> anySpecification() {
        return any();
    }

    private static void assertReadOnlyTransactionActive() {
        assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
        assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    }

    private static class ReadOnlyTestTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            assertTrue(definition.isReadOnly());
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
