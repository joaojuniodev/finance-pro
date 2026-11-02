package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletDeletionTests {
    @Test
    void subtractsPositiveAndNegativeBalancesWithoutChangingIncomeOrExpenses() {
        assertDeletion("80.00", "100.00", "20.00");
        assertDeletion("-30.00", "100.00", "130.00");
        assertDeletion("0.00", "100.00", "100.00");
    }

    @Test
    void missingWalletDoesNotChangeTheAccount() {
        AtomicInteger accountSaves = new AtomicInteger();
        AtomicInteger deletes = new AtomicInteger();
        WalletService service = service(null, accountSaves, deletes);

        assertThrows(NotFoundException.class, () -> service.delete(UUID.randomUUID()));
        assertEquals(0, accountSaves.get());
        assertEquals(0, deletes.get());
    }

    private void assertDeletion(String walletBalance, String accountBalance, String expectedBalance) {
        Account account = new Account(UUID.randomUUID(), money(accountBalance), money("150.00"), money("50.00"), money("100.00"));
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setAccount(account);
        wallet.setBalance(money(walletBalance));
        AtomicInteger accountSaves = new AtomicInteger();
        AtomicInteger deletes = new AtomicInteger();

        service(wallet, accountSaves, deletes).delete(wallet.getId());

        assertEquals(money(expectedBalance), account.getCurrentBalance());
        assertEquals(money("150.00"), account.getIncome());
        assertEquals(money("50.00"), account.getExpenses());
        assertEquals(money("100.00"), account.getNetIncome());
        assertEquals(1, accountSaves.get());
        assertEquals(1, deletes.get());
    }

    private WalletService service(Wallet wallet, AtomicInteger accountSaves, AtomicInteger deletes) {
        WalletRepository wallets = (WalletRepository) Proxy.newProxyInstance(
            WalletRepository.class.getClassLoader(), new Class<?>[]{WalletRepository.class},
            (proxy, method, args) -> switch (method.getName()) {
                case "findByIdForUpdate" -> Optional.ofNullable(wallet);
                case "delete" -> { deletes.incrementAndGet(); yield null; }
                default -> throw new UnsupportedOperationException(method.getName());
            });
        AccountRepository accounts = (AccountRepository) Proxy.newProxyInstance(
            AccountRepository.class.getClassLoader(), new Class<?>[]{AccountRepository.class},
            (proxy, method, args) -> {
                if (method.getName().equals("save")) {
                    accountSaves.incrementAndGet();
                    return args[0];
                }
                throw new UnsupportedOperationException(method.getName());
            });
        AccountBalanceService accountBalanceService = new AccountBalanceService();
        ReflectionTestUtils.setField(accountBalanceService, "accountRepository", accounts);
        WalletService service = new WalletService();
        ReflectionTestUtils.setField(service, "repository", wallets);
        ReflectionTestUtils.setField(service, "accountBalanceService", accountBalanceService);
        return service;
    }

    private static BigDecimal money(String value) {
        return new BigDecimal(value);
    }
}
