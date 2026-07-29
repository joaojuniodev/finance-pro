package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.wallet.model.Wallet;

import java.math.BigDecimal;

public interface WalletBalanceOperations {

    void credit(Wallet wallet, BigDecimal amount, Boolean isTransaction, Boolean isDeletingTransaction);
    void debit(Wallet wallet, BigDecimal amount, Boolean isTransaction, Boolean isDeletingTransaction);
    void transfer(Wallet from, Wallet to, BigDecimal amount);
}