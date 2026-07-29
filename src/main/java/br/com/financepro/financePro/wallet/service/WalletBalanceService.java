package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletBalanceService implements WalletBalanceOperations {

    private final Logger log = LoggerFactory.getLogger(WalletBalanceService.class.getName());

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Transactional
    @Override
    public void credit(Wallet wallet, BigDecimal amount, Boolean isTransaction, Boolean isDeletingTransaction) {
        validateAmount(amount);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        accountBalanceService.updateBalance(wallet.getAccount(), amount, TransactionType.CREDIT, isTransaction, isDeletingTransaction);

        log.info("Wallet {} creditada em {}", wallet.getId(), amount);
    }

    @Transactional
    @Override
    public void debit(Wallet wallet, BigDecimal amount, Boolean isTransaction, Boolean isDeletingTransaction) {
        validateAmount(amount);
        // validateSufficientBalance(wallet, amount);

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        accountBalanceService.updateBalance(wallet.getAccount(), amount, TransactionType.DEBIT, isTransaction, isDeletingTransaction);

        log.info("Wallet {} debitada em {}", wallet.getId(), amount);
    }

    @Transactional
    @Override
    public void transfer(Wallet from, Wallet to, BigDecimal amount) {
        validateSufficientBalance(from, amount);
        validateAmount(amount);

        debit(from, amount, false, false);
        credit(to, amount, false, false);

        log.info("Transferência de {} entre Wallet {} e Wallet {}", amount, from.getId(), to.getId());
    }

    public Wallet getWallet(UUID walletId) {
        return walletRepository
            .findById(walletId).orElseThrow(() -> new NotFoundException("Not found Wallet Id: " + walletId));
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