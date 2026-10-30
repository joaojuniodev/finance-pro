package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.movement.exception.InvalidTransferException;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletBalanceServiceTests {
    private WalletBalanceService service;
    private Account account;
    private AtomicInteger saves;

    @BeforeEach
    void setUp() {
        saves = new AtomicInteger();
        WalletRepository walletRepository = (WalletRepository) Proxy.newProxyInstance(
            WalletRepository.class.getClassLoader(), new Class<?>[]{WalletRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("save")) {
                    saves.incrementAndGet();
                    return args[0];
                }
                throw new UnsupportedOperationException(method.getName());
            });
        service = new WalletBalanceService(walletRepository, new AccountBalanceService());
        account = new Account(UUID.randomUUID(), new BigDecimal("110.00"),
            new BigDecimal("200.00"), new BigDecimal("90.00"), new BigDecimal("110.00"));
    }

    @Test
    void transfersBalanceWithoutChangingAccountTotals() {
        Wallet source = wallet(new BigDecimal("100.00"), WalletType.CHECKING, account);
        Wallet destination = wallet(new BigDecimal("10.00"), WalletType.SAVING, account);

        service.transfer(source, destination, new BigDecimal("25.01"));

        assertEquals(new BigDecimal("74.99"), source.getBalance());
        assertEquals(new BigDecimal("35.01"), destination.getBalance());
        assertEquals(new BigDecimal("110.00"), account.getCurrentBalance());
        assertEquals(2, saves.get());
    }

    @Test
    void rejectsInsufficientBalanceBeforeSaving() {
        Wallet source = wallet(new BigDecimal("5.00"), WalletType.CHECKING, account);
        Wallet destination = wallet(BigDecimal.ZERO, WalletType.SAVING, account);

        assertThrows(InsufficientBalanceException.class,
            () -> service.transfer(source, destination, new BigDecimal("5.01")));
        assertEquals(0, saves.get());
    }

    @Test
    void rejectsCreditCardsAndDifferentAccounts() {
        Wallet card = wallet(new BigDecimal("100.00"), WalletType.CREDIT_CARD, account);
        Wallet destination = wallet(BigDecimal.ZERO, WalletType.SAVING, account);
        assertThrows(InvalidTransferException.class,
            () -> service.transfer(card, destination, BigDecimal.ONE));

        Account anotherAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        Wallet anotherWallet = wallet(BigDecimal.ZERO, WalletType.SAVING, anotherAccount);
        assertThrows(InvalidTransferException.class,
            () -> service.transfer(destination, anotherWallet, BigDecimal.ONE));
    }

    @Test
    void rejectsInvalidAmountsAndSameWallet() {
        Wallet wallet = wallet(new BigDecimal("100.00"), WalletType.CHECKING, account);
        assertThrows(InvalidTransferException.class, () -> service.transfer(wallet, wallet, BigDecimal.ONE));
        assertThrows(InvalidTransferException.class, () -> service.transfer(wallet,
            wallet(BigDecimal.ZERO, WalletType.SAVING, account), new BigDecimal("1.001")));
        assertThrows(InvalidTransferException.class, () -> service.transfer(wallet,
            wallet(BigDecimal.ZERO, WalletType.SAVING, account), BigDecimal.ZERO));
    }

    private Wallet wallet(BigDecimal balance, WalletType type, Account owner) {
        return new Wallet(UUID.randomUUID(), "Carteira", "", balance, null, type,
            "#000000", null, null, owner);
    }
}
