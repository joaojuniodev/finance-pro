package br.com.financepro.financePro.transaction.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.transaction.dto.request.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.exception.InvalidTransactionUpdateException;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionUpdateTests {
    private TransactionService service;
    private Account account;
    private Wallet source;
    private Wallet destination;
    private Transaction transaction;
    private Category category;
    private AtomicInteger balanceSaves;

    @BeforeEach
    void setUp() {
        account = new Account(UUID.randomUUID(), money("100.00"), money("100.00"), money("0.00"), money("100.00"));
        source = wallet(money("100.00"));
        destination = wallet(money("0.00"));
        category = new Category(UUID.randomUUID(), "Categoria", null, null, null, true, null);
        transaction = new Transaction(UUID.randomUUID(), money("100.00"), "Original", "Observação",
            TransactionType.CREDIT, TransactionStatus.COMPLETED, LocalDateTime.of(2026, 1, 1, 12, 30, 42), category, source, account);
        balanceSaves = new AtomicInteger();

        TransactionRepository transactions = proxy(TransactionRepository.class, (method, args) -> switch (method) {
            case "findOwnedForUpdate" -> Optional.ofNullable(transaction.getId().equals(args[0]) && "owner".equals(args[1]) ? transaction : null);
            case "findById" -> Optional.ofNullable(transaction.getId().equals(args[0]) ? transaction : null);
            case "save" -> args[0];
            default -> throw new UnsupportedOperationException(method);
        });
        WalletRepository wallets = proxy(WalletRepository.class, (method, args) -> switch (method) {
            case "findOwnedByIdForUpdate" -> Optional.ofNullable(!"owner".equals(args[1]) ? null :
                source.getId().equals(args[0]) ? source : destination.getId().equals(args[0]) ? destination : null);
            case "save" -> { balanceSaves.incrementAndGet(); yield args[0]; }
            default -> throw new UnsupportedOperationException(method);
        });
        CategoryRepository categories = proxy(CategoryRepository.class, (method, args) -> {
            if (method.equals("findById")) return Optional.ofNullable(category.getId().equals(args[0]) ? category : null);
            throw new UnsupportedOperationException(method);
        });
        AccountRepository accounts = proxy(AccountRepository.class, (method, args) -> {
            if (method.equals("save")) return args[0];
            throw new UnsupportedOperationException(method);
        });
        AccountBalanceService accountBalance = new AccountBalanceService();
        ReflectionTestUtils.setField(accountBalance, "accountRepository", accounts);
        service = new TransactionService();
        ReflectionTestUtils.setField(service, "repository", transactions);
        ReflectionTestUtils.setField(service, "walletRepository", wallets);
        ReflectionTestUtils.setField(service, "categoryRepository", categories);
        ReflectionTestUtils.setField(service, "walletBalanceService", new WalletBalanceService(wallets, accountBalance));
        ReflectionTestUtils.setField(service, "mapper", new TransactionMapper() {
            @Override public TransactionResponseDTO toResponse(Transaction value) { return null; }
        });
    }

    @Test
    void updatesAmountWithoutDuplicatingTheOriginalImpact() {
        var request = request();
        request.setAmount(money("120.00"));
        service.update(request, "owner");
        assertEquals(money("120.00"), source.getBalance());
        assertEquals(money("120.00"), account.getCurrentBalance());
        assertEquals(money("120.00"), account.getIncome());
        assertEquals(money("120.00"), account.getNetIncome());
        assertEquals("Observação", transaction.getObservation());
    }

    @Test
    void changesWalletAndTypeReversingPreviousTotals() {
        var request = request();
        request.setAmount(money("30.00"));
        request.setType(TransactionType.DEBIT);
        request.setWalletId(destination.getId());
        service.update(request, "owner");
        assertEquals(money("0.00"), source.getBalance());
        assertEquals(money("-30.00"), destination.getBalance());
        assertEquals(money("-30.00"), account.getCurrentBalance());
        assertEquals(money("0.00"), account.getIncome());
        assertEquals(money("30.00"), account.getExpenses());
        assertEquals(money("-30.00"), account.getNetIncome());
    }

    @Test
    void metadataOnlyDoesNotReapplyBalance() {
        var request = request();
        request.setDescription("Atualizada");
        service.update(request, "owner");
        assertEquals("Atualizada", transaction.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 1, 12, 30, 42), transaction.getRegisteredAt());
        assertEquals(0, balanceSaves.get());
    }

    @Test
    void rejectsEveryUpdateToARecurringTransaction() {
        transaction.setRecurrence(new Recurrence());
        List<Consumer<TransactionRequestDTO>> changes = List.of(
            request -> {},
            request -> request.setDescription("Atualizada"),
            request -> request.setAmount(money("200.00")),
            request -> request.setType(TransactionType.DEBIT),
            request -> request.setWalletId(destination.getId()),
            request -> request.setCategoryId(UUID.randomUUID()),
            request -> request.setRegisteredAt(request.getRegisteredAt().plusSeconds(1)),
            request -> request.setStatus(TransactionStatus.PENDING)
        );
        for (var change : changes) {
            var request = request();
            change.accept(request);
            assertThrows(InvalidTransactionUpdateException.class, () -> service.update(request, "owner"));
        }
        assertThrows(InvalidTransactionUpdateException.class, () -> service.conclude(transaction.getId()));
        transaction.setStatus(TransactionStatus.PENDING);
        assertThrows(InvalidTransactionUpdateException.class, () -> service.conclude(transaction.getId()));
        assertEquals("Original", transaction.getDescription());
        assertEquals(money("100.00"), source.getBalance());
        assertEquals(money("100.00"), account.getCurrentBalance());
        assertEquals(0, balanceSaves.get());
    }

    @Test
    void rejectsAnotherUserAndInvalidAmountBeforeChangingBalances() {
        assertThrows(NotFoundException.class, () -> service.update(request(), "other"));
        var request = request();
        request.setAmount(money("1.001"));
        assertThrows(InvalidTransactionUpdateException.class, () -> service.update(request, "owner"));
        assertEquals(0, balanceSaves.get());
    }

    private TransactionRequestDTO request() {
        return new TransactionRequestDTO(transaction.getId(), transaction.getAmount(), transaction.getDescription(), null,
            transaction.getType(), transaction.getStatus(), transaction.getRegisteredAt(), category.getId(), source.getId(), account.getId());
    }

    private Wallet wallet(BigDecimal balance) {
        return new Wallet(UUID.randomUUID(), "Carteira", "", balance, null, WalletType.CHECKING, "#000000", null, null, account);
    }

    private static BigDecimal money(String value) { return new BigDecimal(value); }

    private interface Invocation { Object invoke(String method, Object[] args); }

    private static <T> T proxy(Class<T> type, Invocation invocation) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
            (instance, method, args) -> invocation.invoke(method.getName(), args)));
    }
}
