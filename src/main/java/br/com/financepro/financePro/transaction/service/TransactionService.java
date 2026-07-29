package br.com.financepro.financePro.transaction.service;

import br.com.financepro.financePro.account.service.AccountService;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.service.RecurrenceReadService;
import br.com.financepro.financePro.recurrence.service.params.RecurrenceSearchParams;
import br.com.financepro.financePro.transaction.dto.request.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.response.AllTransactionResponseDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class.getName());

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletBalanceService walletBalanceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RecurrenceReadService recurrenceService;

    @Autowired
    private TransactionMapper mapper;

    public List<TransactionResponseDTO> getAll(UUID accountId, UUID walletId, Integer month, Integer year) {
        log.info("Getting All Transactions");

        TransactionSpecification spec = new TransactionSpecification();
        spec.addToSpecifications(accountId, walletId, month, year);

        return repository
            .findAll(
                spec.apply(),
                Sort.by(Sort.Direction.DESC, "registeredAt"))
            .stream()
            .map(this.mapper::toResponse)
            .toList();
    }

    public AllTransactionResponseDTO getOverview(UUID accountId, UUID walletId, Integer month, Integer year) {
        log.info("Getting Overview by Transactions");

        TransactionSpecification spec = new TransactionSpecification();
        spec.addToSpecifications(accountId, walletId, month, year);

        var transactions = repository
            .findAll(
                spec.apply(),
                Sort.by(Sort.Direction.DESC, "registeredAt"))
            .stream()
            .map(this.mapper::toResponse)
            .toList();

        var account = accountService.getById(accountId);
        var recurrences = recurrenceService.getAll(new RecurrenceSearchParams(accountId, null, null, null, null, null));

        BigDecimal commitment = recurrences.stream()
            .filter(rec -> rec.getType() == RecurrenceType.DEBIT)
            .filter(rec -> rec.getNextExecutionDate().isAfter(LocalDate.now()))
            .map(RecurrenceResponseDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return mapper.getAllTransactionResponseDTO(
            commitment,
            account,
            getTransactionBiggestIncome(transactions),
            getTransactionBiggestExpense(transactions),
            transactions
        );
    }

    private TransactionResponseDTO getTransactionBiggestIncome(List<TransactionResponseDTO> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getType() == TransactionType.CREDIT)
            .max(Comparator.comparing(TransactionResponseDTO::getAmount))
            .orElse(null);
    }

    private TransactionResponseDTO getTransactionBiggestExpense(List<TransactionResponseDTO> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getType() == TransactionType.DEBIT)
            .max(Comparator.comparing(TransactionResponseDTO::getAmount))
            .orElse(null);
    }

    public TransactionResponseDTO getById(UUID id) {
        log.info("Getting Transaction by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public TransactionResponseDTO update(TransactionRequestDTO transaction) {
        log.info("Updating Transaction");

        Wallet wallet = walletBalanceService.getWallet(transaction.getWalletId());
        Category category = categoryRepository.findById(transaction.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + transaction.getCategoryId()));

        var entity = repository.findById(transaction.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + transaction.getId()));
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setRegisteredAt(transaction.getRegisteredAt());
        entity.setType(transaction.getType());
        entity.setWallet(wallet);
        entity.setCategory(category);

        perform(entity.getWallet(), entity.getAmount(), entity.getType(), true, false);

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

        TransactionType type = entity.getType().name().equals("CREDIT")
            ? TransactionType.DEBIT
            : TransactionType.CREDIT;

        perform(entity.getWallet(), entity.getAmount(), type, false, true);

        repository.delete(entity);
    }

    private void perform(Wallet wallet, BigDecimal amount, TransactionType type, Boolean isTransaction, Boolean isDeletingTransaction) {
        if (type.name().equals("CREDIT")) {
            walletBalanceService.credit(wallet, amount, isTransaction, isDeletingTransaction);
        } else {
            walletBalanceService.debit(wallet, amount, isTransaction, isDeletingTransaction);
        }
    }
}