package br.com.financepro.financePro.transaction.service;

import br.com.financepro.financePro.account.service.AccountService;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.service.RecurrenceReadService;
import br.com.financepro.financePro.transaction.dto.request.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.response.AllTransactionResponseDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import br.com.financepro.financePro.transaction.exception.InvalidTransactionUpdateException;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private WalletRepository walletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletBalanceService walletBalanceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RecurrenceReadService recurrenceService;

    @Autowired
    private TransactionMapper mapper;

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAll(UUID accountId, UUID walletId, Integer month, Integer year, String username) {
        log.info("Getting All Transactions");

        requireAccount(accountId, username);

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

    @Transactional(readOnly = true)
    public AllTransactionResponseDTO getOverview(UUID accountId, UUID walletId, Integer month, Integer year, String username) {
        log.info("Getting Overview by Transactions");

        requireAccount(accountId, username);

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
        var recurrences = recurrenceService.getAll(accountId);

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

    private void requireAccount(UUID accountId, String username) {
        if (username == null || accountId == null || accountRepository.findByUsername(username)
            .filter(account -> accountId.equals(account.getId())).isEmpty()) {
            throw new NotFoundException("Conta não encontrada.");
        }
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

    @Transactional(readOnly = true)
    public TransactionResponseDTO getById(UUID id) {
        log.info("Getting Transaction by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional
    public TransactionResponseDTO update(TransactionRequestDTO transaction, String username) {
        log.info("Updating Transaction");

        validateUpdate(transaction);

        var entity = repository.findOwnedForUpdate(transaction.getId(), username)
            .orElseThrow(() -> new NotFoundException("Transação não encontrada."));

        UUID accountId = entity.getAccount().getId();
        if (!accountId.equals(transaction.getAccountId())) {
            throw new NotFoundException("Conta não encontrada.");
        }
        requireNonRecurring(entity);

        Category category = categoryRepository.findById(transaction.getCategoryId())
            .filter(value -> Boolean.TRUE.equals(value.getSystem()) ||
                (value.getAccount() != null && accountId.equals(value.getAccount().getId())))
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        UUID oldWalletId = entity.getWallet().getId();
        UUID newWalletId = transaction.getWalletId();
        UUID firstId = oldWalletId.compareTo(newWalletId) <= 0 ? oldWalletId : newWalletId;
        UUID secondId = firstId.equals(oldWalletId) ? newWalletId : oldWalletId;
        Wallet first = walletRepository.findOwnedByIdForUpdate(firstId, username)
            .orElseThrow(() -> new NotFoundException("Carteira não encontrada."));
        Wallet second = firstId.equals(secondId) ? first : walletRepository.findOwnedByIdForUpdate(secondId, username)
            .orElseThrow(() -> new NotFoundException("Carteira não encontrada."));
        Wallet oldWallet = firstId.equals(oldWalletId) ? first : second;
        Wallet wallet = firstId.equals(newWalletId) ? first : second;

        if (!accountId.equals(wallet.getAccount().getId()) || !accountId.equals(oldWallet.getAccount().getId())) {
            throw new NotFoundException("Carteira não encontrada.");
        }

        boolean balanceChanged = !oldWalletId.equals(newWalletId)
            || entity.getAmount().compareTo(transaction.getAmount()) != 0 || entity.getType() != transaction.getType();

        if (balanceChanged) {
            TransactionType reverse = entity.getType() == TransactionType.CREDIT ? TransactionType.DEBIT : TransactionType.CREDIT;
            perform(oldWallet, entity.getAmount(), reverse, false, true);
            perform(wallet, transaction.getAmount(), transaction.getType(), true, false);
        }

        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setRegisteredAt(transaction.getRegisteredAt());
        entity.setType(transaction.getType());
        entity.setStatus(transaction.getStatus());
        entity.setWallet(wallet);
        entity.setCategory(category);

        var transactionUpdated = repository.save(entity);
        return mapper.toResponse(transactionUpdated);
    }

    private void validateUpdate(TransactionRequestDTO request) {
        if (request == null || request.getId() == null || request.getAccountId() == null
            || request.getWalletId() == null || request.getCategoryId() == null
            || request.getType() == null || request.getStatus() == null || request.getRegisteredAt() == null
            || request.getAmount() == null || request.getAmount().signum() <= 0
            || request.getAmount().stripTrailingZeros().scale() > 2
            || request.getAmount().precision() - request.getAmount().scale() > 36
            || request.getDescription() == null || request.getDescription().isBlank() || request.getDescription().length() > 200) {
            throw new InvalidTransactionUpdateException("Confira os dados da transação.");
        }
    }

    public TransactionResponseDTO conclude(UUID transactionId) {
        log.info("Complete Transaction");

        var entity = repository.findById(transactionId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + transactionId));
        requireNonRecurring(entity);
        entity.setStatus(TransactionStatus.COMPLETED);

        var transactionUpdated = repository.save(entity);
        return mapper.toResponse(transactionUpdated);
    }

    private void requireNonRecurring(Transaction transaction) {
        if (transaction.getRecurrence() != null) {
            throw new InvalidTransactionUpdateException("Transações vinculadas a uma recorrência não podem ser editadas. Altere a recorrência.");
        }
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
