package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.projection.TopCategoryProjection;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletBalanceService implements WalletBalanceOperations {

    private final Logger log = LoggerFactory.getLogger(WalletBalanceService.class.getName());

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public WalletBalanceService(AccountRepository accountRepository, WalletRepository walletRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public void credit(Wallet wallet, BigDecimal amount, Boolean isTransaction) {
        validateAmount(amount);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        updateAccountBalance(wallet.getAccount(), amount, TransactionType.CREDIT, isTransaction);

        log.info("Wallet {} creditada em {}", wallet.getId(), amount);
    }

    @Transactional
    @Override
    public void debit(Wallet wallet, BigDecimal amount, Boolean isTransaction) {
        validateAmount(amount);
        // validateSufficientBalance(wallet, amount);

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        updateAccountBalance(wallet.getAccount(), amount, TransactionType.DEBIT, isTransaction);

        log.info("Wallet {} debitada em {}", wallet.getId(), amount);
    }

    @Transactional
    @Override
    public void transfer(Wallet from, Wallet to, BigDecimal amount) {
        validateSufficientBalance(from, amount);
        validateAmount(amount);

        debit(from, amount, false);
        credit(to, amount, false);

        log.info("Transferência de {} entre Wallet {} e Wallet {}", amount, from.getId(), to.getId());
    }

    public void updateBiggestExpenseCategory(Account account) {
        TopCategoryProjection result = transactionRepository.findTopCategoryCurrentMonth(account.getId());

        if (result == null) return;

        Category category = categoryRepository.findById(result.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + result.getCategoryId()));
        BigDecimal total = result.getTotal();

        account.setBiggestExpenseCategory(category);
        account.setBiggestExpenseValue(total);
    }

    public Wallet getWallet(UUID walletId) {
        return walletRepository
            .findById(walletId).orElseThrow(() -> new NotFoundException("Not found Wallet Id: " + walletId));
    }

    private void updateAccountBalance(Account account, BigDecimal delta, TransactionType type, Boolean isTransaction) {
        if (type.equals(TransactionType.CREDIT)) {
            account.setCurrentBalance(account.getCurrentBalance().add(delta));
        }
        else {
            account.setCurrentBalance(account.getCurrentBalance().subtract(delta));
        }

        if (isTransaction) {
            if (type.equals(TransactionType.CREDIT)) {
                account.setIncome(account.getIncome().add(delta));
            }
            else {
                account.setExpenses(account.getExpenses().add(delta));
            }
        }

        final BigDecimal NET_INCOME = account.getIncome().subtract(account.getExpenses());
        account.setNetIncome(NET_INCOME);

        accountRepository.save(account);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor precisa ser positivo");
        }
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                "Saldo insuficiente na wallet " + wallet.getId()
            );
        }
    }
}