package br.com.financepro.financePro.transaction.service;


import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.service.RecurrenceService;
import br.com.financepro.financePro.transaction.dto.OverviewResponseDTO;
import br.com.financepro.financePro.transaction.dto.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.dto.WeekOverviewResponse;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.projection.WeeklyOverviewProjection;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class.getName());

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private WalletBalanceService walletBalanceService;

    @Autowired
    private RecurrenceService recurrenceService;

    @Autowired
    private TransactionMapper mapper;

    public List<TransactionResponseDTO> getAll(UUID accountId, Integer month, Integer year) {
        log.info("Getting All Transactions");

        TransactionSpecification spec = new TransactionSpecification();
        spec.addToSpecifications(accountId, month, year);

        return repository
            .findAll(spec.apply())
            .stream()
            .map(this.mapper::toResponse)
            .toList();
    }

    public TransactionResponseDTO getById(UUID id) {
        log.info("Getting Transaction by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public OverviewResponseDTO overview(UUID accountId) {
        Map<String, List<WeekOverviewResponse>> overview = new HashMap<>();

        for (Month month : Month.values()) {
            overview.put(
                month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                new ArrayList<>()
            );
        }

        List<WeeklyOverviewProjection> result = repository.findWeeklyOverview(accountId, LocalDateTime.now().getYear());

        for (WeeklyOverviewProjection item : result) {
            String monthName = Month.of(item.getMonth())
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            overview.get(monthName).add(
                new WeekOverviewResponse(
                    item.getWeek(),
                    item.getIncome(),
                    item.getExpenses()
                )
            );
        }

        return new OverviewResponseDTO(overview);
    }

    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO transaction) {
        log.info("Creating Transaction");

        final BigDecimal amount = transaction.getAmount();
        Wallet wallet = walletBalanceService.getWallet(transaction.getWalletId());

        if (transaction.getType().equals(TransactionType.CREDIT)) {
            walletBalanceService.credit(wallet, amount, true);
        } else {
            walletBalanceService.debit(wallet, amount, true);
        }

        var transactionCreated = repository.save(mapper.toEntity(transaction));
        walletBalanceService.updateBiggestExpenseCategory(wallet.getAccount());
        return mapper.toResponse(transactionCreated);
    }

    @Transactional
    public void createByRecurrence(Recurrence recurrence, LocalDate executionDate) {
        log.info("Creating Transaction by Recurrence");

        final BigDecimal amount = recurrence.getAmount();
        Wallet wallet = recurrence.getWallet();

        if (recurrence.getType().equals(RecurrenceType.CREDIT)) {
            walletBalanceService.credit(wallet, amount, true);
        } else {
            walletBalanceService.debit(wallet, amount, true);
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(recurrence.getAmount());
        transaction.setDescription(recurrence.getDescription());
        transaction.setAccount(recurrence.getAccount());
        transaction.setWallet(recurrence.getWallet());
        transaction.setRecurrence(recurrence);
        transaction.setRegisteredAt(executionDate.atStartOfDay());

        transaction.setType(
            recurrence.getType().equals(RecurrenceType.CREDIT)
                ? TransactionType.CREDIT
                : TransactionType.DEBIT
        );

        repository.save(transaction);

        walletBalanceService.updateBiggestExpenseCategory(wallet.getAccount());
    }

    public TransactionResponseDTO update(TransactionRequestDTO transaction) {
        log.info("Updating Transaction");

        var entity = repository.findById(transaction.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + transaction.getId()));
        entity.setAmount(transaction.getAmount());
        entity.setType(transaction.getType());

        var transactionUpdated = repository.save(entity);
        return mapper.toResponse(transactionUpdated);
    }

    public TransactionResponseDTO conclude(UUID transactionId) {
        log.info("Complete Transaction");

        var entity = repository.findById(transactionId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + transactionId));
        entity.setStatus(TransactionStatus.COMPLETED);

        var transactionUpdated = repository.save(entity);
        return mapper.toResponse(transactionUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Transaction");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }
}