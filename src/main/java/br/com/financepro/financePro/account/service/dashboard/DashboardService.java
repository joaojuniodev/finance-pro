package br.com.financepro.financePro.account.service.dashboard;

import br.com.financepro.financePro.account.dto.response.DashboardResponseDTO;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.shared.AccountSpendingCategoriesService;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private AccountSpendingCategoriesService spendingCategoriesService;

    public DashboardResponseDTO getDashboard(UUID accountId, int limit) {
        log.info("Getting Dashboard Overview this Account");

        var entity = accountRepository.findWithDashboardById(accountId)
            .orElseThrow(() -> new NotFoundException("Not found Account by id: " + accountId));
        var wallets = entity.getWallets().stream()
            .map(walletMapper::toResponse)
            .toList();
        var transactions = transactionRepository.findRecentAccount(accountId, PageRequest.of(0, 7)).stream()
            .map(transactionMapper::toResponse)
            .toList();
        var recurrences = recurrenceRepository.findUpcomingRecurrences(LocalDate.now(), LocalDate.now().plusWeeks(2)).stream()
            .map(recurrenceMapper::toSummary)
            .toList();
        var expensesByCategory = spendingCategoriesService.getTopSpendingCategories(accountId, limit);

        var availableToSpend = entity.getCurrentBalance().subtract(
            entity.getRecurrences().stream()
                .map(Recurrence::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return new DashboardResponseDTO(
            entity.getCurrentBalance(),
            entity.getIncome(),
            entity.getExpenses(),
            availableToSpend,
            wallets,
            transactions,
            recurrences,
            expensesByCategory
        );
    }
}