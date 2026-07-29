package br.com.financepro.financePro.mapper.transaction;

import br.com.financepro.financePro.account.dto.response.AccountResponseDTO;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.transaction.dto.request.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.response.AllTransactionResponseDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionSummaryDTO;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionMapper implements ObjectMapper<Transaction, TransactionResponseDTO, TransactionRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Transaction toEntity(TransactionRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + request.getCategoryId()));
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + request.getCategoryId()));
        return new Transaction(
            request.getId(),
            request.getAmount(),
            request.getDescription(),
            request.getObservation(),
            request.getType(),
            request.getStatus(),
            request.getRegisteredAt(),
            category,
            wallet,
            account
        );
    }

    @Override
    public TransactionResponseDTO toResponse(Transaction entity) {
        var category = entity.getCategory() != null
            ? categoryMapper.toResponse(entity.getCategory())
            : null;
        var wallet = walletMapper.toResponse(entity.getWallet());
        return new TransactionResponseDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getDescription(),
            entity.getType(),
            entity.getStatus(),
            category,
            entity.getRegisteredAt(),
            wallet,
            entity.getRecurrence() != null
                ? entity.getRecurrence().getId()
                : null
        );
    }

    public TransactionSummaryDTO toSummary(Transaction entity) {
        return new TransactionSummaryDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getDescription(),
            entity.getType(),
            entity.getStatus(),
            entity.getRegisteredAt()
        );
    }

    public Transaction getTransactionByRecurrence(Recurrence recurrence, LocalDate executionDate) {
        Transaction transaction = new Transaction();
        transaction.setAmount(recurrence.getAmount());
        transaction.setDescription(recurrence.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAccount(recurrence.getAccount());
        transaction.setCategory(recurrence.getCategory());
        transaction.setWallet(recurrence.getWallet());
        transaction.setRecurrence(recurrence);
        transaction.setRegisteredAt(executionDate.atStartOfDay());

        transaction.setType(
            recurrence.getType().equals(RecurrenceType.CREDIT)
                ? TransactionType.CREDIT
                : TransactionType.DEBIT
        );
        return transaction;
    }

    public AllTransactionResponseDTO getAllTransactionResponseDTO(
        BigDecimal commitment,
        AccountResponseDTO account,
        TransactionResponseDTO transactionBiggestIncome,
        TransactionResponseDTO transactionBiggestExpense,
        List<TransactionResponseDTO> transactions
    ) {
        AllTransactionResponseDTO response = new AllTransactionResponseDTO();
        response.setCurrentBalance(account.getCurrentBalance());
        response.setAvailableToSpend(account.getCurrentBalance().subtract(commitment));
        response.setIncome(account.getIncome());
        response.setExpenses(account.getExpenses());
        response.setNetIncome(account.getNetIncome());
        response.setCommitment(commitment);
        response.setTransactionBiggestIncome(transactionBiggestIncome);
        response.setTransactionBiggestExpense(transactionBiggestExpense);
        response.setTransactions(transactions);
        return response;
    }
}