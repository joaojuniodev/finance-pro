package br.com.financepro.financePro.account.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.shared.AccountSpendingCategoriesService;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.transaction.dto.projection.CategorySpendingDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountBalanceService implements AccountBalanceOperations {
    private final Logger log = LoggerFactory.getLogger(AccountBalanceService.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccountSpendingCategoriesService spendingCategoriesService;

    @Transactional
    @Override
    public void updateBalance(Account account, BigDecimal amount, TransactionType type, Boolean isTransaction, Boolean isDeletingTransaction) {
        log.info("Updating balance this Account");

        if (type.name().equals("CREDIT")) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        } else {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }

        if (isTransaction) updateWhenCreateTransaction(account, amount, type);
        if (isDeletingTransaction) updateWhenDeletingTransaction(account, amount, type);

        final BigDecimal NET_INCOME = account.getIncome().subtract(account.getExpenses());
        account.setNetIncome(NET_INCOME);
        //updateBiggestCategory(account);

        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void updateBiggestCategory(Account account) {
        log.info("Updating biggest category this Account");

        CategorySpendingDTO result = spendingCategoriesService.getTopSpendingCategory(account.getId());

        if (result == null) return;

        Category category = categoryRepository.findById(result.id())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + result.id()));
        BigDecimal total = result.amount();

        account.setBiggestExpenseCategory(category);
        account.setBiggestExpenseValue(total);
    }

    private void updateWhenCreateTransaction(Account account, BigDecimal amount, TransactionType type) {
        if (type.name().equals("CREDIT")) {
            account.setIncome(account.getIncome().add(amount));
        } else {
            account.setExpenses(account.getExpenses().add(amount));
        }
    }

    private void updateWhenDeletingTransaction(Account account, BigDecimal amount, TransactionType type) {
        if (type.name().equals("CREDIT")) {
            account.setExpenses(account.getExpenses().subtract(amount));
        } else {
            account.setIncome(account.getIncome().subtract(amount));
        }
    }
}