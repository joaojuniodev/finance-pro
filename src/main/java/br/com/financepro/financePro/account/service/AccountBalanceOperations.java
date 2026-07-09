package br.com.financepro.financePro.account.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.common.enums.TransactionType;

import java.math.BigDecimal;

public interface AccountBalanceOperations {

    void updateBalance(Account account, BigDecimal amount, TransactionType type, Boolean isTransaction);
    void updateBiggestCategory(Account account);
}