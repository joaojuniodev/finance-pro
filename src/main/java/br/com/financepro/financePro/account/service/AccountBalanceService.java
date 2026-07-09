package br.com.financepro.financePro.account.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.projection.TopCategoryProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountBalanceService implements AccountBalanceOperations {
    private final Logger log = LoggerFactory.getLogger(AccountBalanceService.class.getName());

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public AccountBalanceService(AccountRepository accountRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public void updateBalance(Account account, BigDecimal amount, TransactionType type, Boolean isTransaction) {
        log.info("Updating balance this Account");

        if (type.equals(TransactionType.CREDIT)) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }
        else {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }

        if (isTransaction) {
            if (type.equals(TransactionType.CREDIT)) {
                account.setIncome(account.getIncome().add(amount));
            }
            else {
                account.setExpenses(account.getExpenses().add(amount));
            }
        }

        final BigDecimal NET_INCOME = account.getIncome().subtract(account.getExpenses());
        account.setNetIncome(NET_INCOME);

        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void updateBiggestCategory(Account account) {
        log.info("Updating biggest category this Account");

        TopCategoryProjection result = transactionRepository.findTopCategoryCurrentMonth(account.getId());

        if (result == null) return;

        Category category = categoryRepository.findById(result.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + result.getCategoryId()));
        BigDecimal total = result.getTotal();

        account.setBiggestExpenseCategory(category);
        account.setBiggestExpenseValue(total);
    }
}