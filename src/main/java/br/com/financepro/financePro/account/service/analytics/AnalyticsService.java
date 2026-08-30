package br.com.financepro.financePro.account.service.analytics;

import br.com.financepro.financePro.account.dto.response.AnalyticsResponseDTO;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.shared.AccountBalanceTrajectoryService;
import br.com.financepro.financePro.account.service.shared.AccountEvolutionService;
import br.com.financepro.financePro.account.service.shared.AccountSpendingCategoriesService;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.recurrence.repository.spec.RecurrenceSpecification;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class AnalyticsService {

    private final Logger log = LoggerFactory.getLogger(AnalyticsService.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private RecurrenceMapper recurrenceMapper;

    @Autowired
    private AccountSpendingCategoriesService spendingCategoriesService;

    @Autowired
    private AccountEvolutionService evolutionService;

    @Autowired
    private AccountBalanceTrajectoryService trajectoryService;

    public AnalyticsResponseDTO getAnalytics(UUID accountId, int limit) {

        log.info("Getting Analytics by Account ID");

        LocalDate today = LocalDate.now();

        TransactionSpecification txSpec = new TransactionSpecification();
        txSpec.addToSpecifications(accountId, null, today.getMonthValue(), today.getYear());

        RecurrenceSpecification recSpec = new RecurrenceSpecification();
        recSpec.addSpecifications(accountId);

        var account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("Not found Account by id: " + accountId));

        var transactions = transactionRepository
            .findAll(
                txSpec.apply(),
                Sort.by(Sort.Direction.DESC, "registeredAt"))
            .stream()
            .map(transactionMapper::toResponse)
            .toList();

        var recurrences = recurrenceRepository
            .findAll(recSpec.apply())
            .stream()
            .map(entity -> recurrenceMapper.toResponse(entity))
            .toList();

        var categorySpending = spendingCategoriesService.getTopSpendingCategories(accountId, limit);

        var evolution = evolutionService.getWeeklyEvolution(accountId);

        var trajectory = trajectoryService.getMonthlyBalanceTrajectory(accountId, YearMonth.now());

        final BigDecimal commitments = recurrences.stream()
            .map(RecurrenceResponseDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal availableToSpend = account.getIncome().subtract(commitments);

        return new AnalyticsResponseDTO(
            availableToSpend,
            account.getIncome(),
            account.getExpenses(),
            commitments,
            transactions,
            recurrences,
            categorySpending,
            evolution,
            trajectory
        );
    }
}