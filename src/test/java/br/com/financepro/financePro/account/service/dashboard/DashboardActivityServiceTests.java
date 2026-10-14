package br.com.financepro.financePro.account.service.dashboard;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.transaction.dto.projection.DailyFlowProjection;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardActivityServiceTests {

    @Mock
    private AccountRepository accounts;

    @Mock
    private TransactionRepository transactions;

    @Mock
    private TransactionMapper mapper;

    private final UUID accountId = UUID.randomUUID();
    private DashboardService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-14T15:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        service = new DashboardService();
    }

    @Test
    void shouldGroupCompletedFlowAndFillAllSevenDays() {
        when(accounts.existsById(accountId)).thenReturn(true);
        var dailyRows = List.of(
            row(LocalDate.of(2026, 10, 12), TransactionType.CREDIT, "200"),
            row(LocalDate.of(2026, 10, 12), TransactionType.DEBIT, "50"),
            row(LocalDate.of(2026, 10, 14), TransactionType.DEBIT, "25")
        );
        when(transactions.findCompletedDailyFlowInPeriod(eq(accountId), any(), any())).thenReturn(dailyRows);
        when(transactions.findRecentAccount(eq(accountId), any())).thenReturn(List.of());

        var response = service.getDashboard(accountId);

        assertEquals(LocalDate.of(2026, 10, 12), response.weekStart());
        assertEquals(LocalDate.of(2026, 10, 18), response.weekEnd());
        assertEquals(7, response.weeklyFlow().size());
        assertEquals(new BigDecimal("200"), response.weeklyFlow().get(0).income());
        assertEquals(new BigDecimal("50"), response.weeklyFlow().get(0).expense());
        assertEquals(BigDecimal.ZERO, response.weeklyFlow().get(1).income());
        assertEquals(new BigDecimal("125"), response.weeklyFlow().get(6).balance());
        assertEquals(BigDecimal.ZERO, response.weeklyFlow().get(6).expense());
        verify(transactions).findCompletedDailyFlowInPeriod(
            accountId,
            LocalDate.of(2026, 10, 12).atStartOfDay(),
            LocalDate.of(2026, 10, 15).atStartOfDay()
        );
    }

    @Test
    void shouldLimitRecentTransactionsToSixAndKeepRepositoryOrder() {
        when(accounts.existsById(accountId)).thenReturn(true);
        when(transactions.findCompletedDailyFlowInPeriod(eq(accountId), any(), any())).thenReturn(List.of());
        Transaction first = mock(Transaction.class);
        Transaction second = mock(Transaction.class);
        TransactionResponseDTO firstResponse = mock(TransactionResponseDTO.class);
        TransactionResponseDTO secondResponse = mock(TransactionResponseDTO.class);
        when(transactions.findRecentAccount(eq(accountId), any())).thenReturn(List.of(first, second));
        when(mapper.toResponse(first)).thenReturn(firstResponse);
        when(mapper.toResponse(second)).thenReturn(secondResponse);

        var response = service.getDashboard(accountId);

        assertEquals(List.of(firstResponse, secondResponse), response.recentTransactions());
        ArgumentCaptor<Pageable> page = ArgumentCaptor.forClass(Pageable.class);
        verify(transactions).findRecentAccount(eq(accountId), page.capture());
        assertEquals(6, page.getValue().getPageSize());
        assertEquals(0, page.getValue().getPageNumber());
        assertTrue(response.weeklyFlow().stream().allMatch(point -> point.income().signum() == 0 && point.expense().signum() == 0));
    }

    @Test
    void shouldRejectUnknownAccountBeforeQueryingTransactions() {
        when(accounts.existsById(accountId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getDashboard(accountId));
        verifyNoInteractions(transactions, mapper);
    }

    private DailyFlowProjection row(LocalDate date, TransactionType type, String amount) {
        DailyFlowProjection row = mock(DailyFlowProjection.class);
        when(row.getDate()).thenReturn(date);
        when(row.getType()).thenReturn(type);
        when(row.getTotal()).thenReturn(new BigDecimal(amount));
        return row;
    }
}
