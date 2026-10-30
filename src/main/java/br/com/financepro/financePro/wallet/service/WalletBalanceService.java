package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.movement.exception.InvalidTransferException;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
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

    private final WalletRepository walletRepository;
    private final AccountBalanceService accountBalanceService;

    public WalletBalanceService(WalletRepository walletRepository, AccountBalanceService accountBalanceService) {
        this.walletRepository = walletRepository;
        this.accountBalanceService = accountBalanceService;
    }

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
        if (amount == null || amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 2) {
            throw new InvalidTransferException("Informe um valor positivo com até duas casas decimais.");
        }
        if (from == null || to == null || from.getId() == null || from.getId().equals(to.getId())
            || from.getAccount() == null || to.getAccount() == null || from.getAccount().getId() == null
            || !from.getAccount().getId().equals(to.getAccount().getId())) {
            throw new InvalidTransferException("Escolha carteiras diferentes da mesma conta.");
        }
        if (from.getType() == WalletType.CREDIT_CARD || to.getType() == WalletType.CREDIT_CARD) {
            throw new InvalidTransferException("Cartões de crédito devem usar o fluxo de pagamento de fatura.");
        }
        if (from.getBalance() == null || to.getBalance() == null) {
            throw new InvalidTransferException("Não foi possível consultar o saldo das carteiras.");
        }
        validateSufficientBalance(from, amount);
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        walletRepository.save(from);
        walletRepository.save(to);
        // Internal transfers do not change consolidated balance, income or expenses.
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
