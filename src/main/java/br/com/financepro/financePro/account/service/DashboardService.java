package br.com.financepro.financePro.account.service;

import br.com.financepro.financePro.account.dto.response.DashboardOverviewResponseDTO;
import br.com.financepro.financePro.account.dto.response.ExpenseByCategoryResponseDTO;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.dto.projection.CategoryExpenseProjection;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardService.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private RecurrenceMapper recurrenceMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public DashboardOverviewResponseDTO getDashboardOverview(UUID accountId) {
        log.info("Getting Dashboard Overview this Account");

        DashboardOverviewResponseDTO overview = new DashboardOverviewResponseDTO();

        var entity = accountRepository.findWithDashboardById(accountId)
            .orElseThrow(() -> new NotFoundException("Not found Account by id: " + accountId));
        var wallets = entity.getWallets().stream()
            .map(walletMapper::toResponse)
            .toList();
        var transactions = transactionRepository.findRecentAccount(accountId, PageRequest.of(0, 10)).stream()
            .map(transactionMapper::toResponse)
            .toList();
        var recurrences = recurrenceRepository.findUpcomingRecurrences(LocalDate.now(), LocalDate.now().plusWeeks(2)).stream()
            .map(recurrenceMapper::toSummary)
            .toList();
        var expensesByCategory = assemblyOfExpensesByCategory(accountId);

        overview.setCurrentBalance(entity.getCurrentBalance());
        overview.setIncome(entity.getIncome());
        overview.setExpenses(entity.getExpenses());
        overview.setWallets(wallets);
        overview.setTransactions(transactions);
        overview.setRecurrences(recurrences);
        overview.setExpensesByCategory(expensesByCategory);
        return overview;
    }

    private List<ExpenseByCategoryResponseDTO> assemblyOfExpensesByCategory(UUID accountId) {
        LocalDateTime today = LocalDateTime.now();

        LocalDateTime startDate = today
            .withDayOfMonth(1)
            .toLocalDate().atStartOfDay();

        LocalDateTime endDate = today
            .with(TemporalAdjusters.lastDayOfMonth())
            .toLocalDate().atTime(LocalTime.MAX);

        var expenses = transactionRepository.findExpensesByCategory(
            accountId,
            startDate,
            endDate
        );

        BigDecimal totalExpenses = expenses.stream()
            .map(CategoryExpenseProjection::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return expenses.stream()
            .map(item -> {
                BigDecimal percentage = BigDecimal.ZERO;

                if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                    percentage = item.amount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalExpenses, 2, RoundingMode.HALF_UP);
                }

                return new ExpenseByCategoryResponseDTO(
                    categoryMapper.toResponse(item.category()),
                    percentage,
                    item.amount()
                );
            })
            .toList();
    }
}