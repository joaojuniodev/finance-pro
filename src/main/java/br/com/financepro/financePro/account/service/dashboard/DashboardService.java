package br.com.financepro.financePro.account.service.dashboard;

import br.com.financepro.financePro.account.dto.response.DashboardActivityResponseDTO;
import br.com.financepro.financePro.account.dto.response.FinancialSummaryResponseDTO;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.transaction.dto.response.EvolutionDataPointDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Service
public class DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardService.class.getName());

    private static final int RECENT_TRANSACTION_LIMIT = 6;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    private Clock clock;

    @Transactional(readOnly = true)
    public FinancialSummaryResponseDTO getSummary(UUID accountId) {
        var account = accountRepository.findWithSummaryById(accountId)
            .orElseThrow(() -> new NotFoundException("Not found Account by id: " + accountId));
        var commitments = account.getRecurrences().stream()
            .map(Recurrence::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummaryResponseDTO(
            account.getCurrentBalance(),
            account.getIncome(),
            account.getExpenses(),
            account.getCurrentBalance().subtract(commitments)
        );
    }

    @Transactional(readOnly = true)
    public DashboardActivityResponseDTO getDashboard(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Not found Account by id: " + accountId);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        var rows = transactionRepository.findCompletedDailyFlowInPeriod(
            accountId,
            weekStart.atStartOfDay(),
            today.plusDays(1).atStartOfDay()
        );
        var incomeByDate = new HashMap<LocalDate, BigDecimal>();
        var expenseByDate = new HashMap<LocalDate, BigDecimal>();

        for (var row : rows) {
            if (row.getType() == TransactionType.CREDIT) {
                incomeByDate.merge(row.getDate(), row.getTotal(), BigDecimal::add);
            } else if (row.getType() == TransactionType.DEBIT) {
                expenseByDate.merge(row.getDate(), row.getTotal(), BigDecimal::add);
            }
        }

        var weeklyFlow = new ArrayList<EvolutionDataPointDTO>();
        BigDecimal balance = BigDecimal.ZERO;
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            BigDecimal income = incomeByDate.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal expense = expenseByDate.getOrDefault(date, BigDecimal.ZERO);
            balance = balance.add(income).subtract(expense);
            weeklyFlow.add(new EvolutionDataPointDTO(date, income, expense, balance));
        }

        var recentTransactions = transactionRepository.findRecentAccount(
            accountId, PageRequest.of(0, RECENT_TRANSACTION_LIMIT)
        ).stream().map(transactionMapper::toResponse).toList();

        return new DashboardActivityResponseDTO(weekStart, weekEnd, weeklyFlow, recentTransactions);
    }
}
