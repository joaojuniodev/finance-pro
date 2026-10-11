package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.bank.model.Bank;
import br.com.financepro.financePro.bank.repository.BankRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.transaction.dto.response.TransactionSummaryDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletSummaryDTO;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.repository.spec.WalletSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final Logger log = LoggerFactory.getLogger(WalletService.class.getName());

    @Autowired
    private WalletRepository repository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private WalletMapper mapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> getAll(UUID accountId) {
        log.info("Getting All Wallets");

        WalletSpecification spec = new WalletSpecification();
        spec.addToSpecifications(accountId);

        return repository
            .findAll(
                spec.apply(),
                Sort.by(Sort.Direction.ASC, "name")
            )
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    @Transactional(readOnly = true)
    public WalletResponseDTO getById(UUID id) {
        log.info("Getting Wallet by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public WalletSummaryDTO summary(UUID accountId, UUID walletId) {
        log.info("Summary Wallet by Id");

        var entity = repository.findById(walletId)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + walletId));

        TransactionSpecification transactionSpec = new TransactionSpecification();
        transactionSpec.addToSpecifications(accountId, walletId, LocalDate.now().getMonthValue(), LocalDate.now().getYear());

        var transactions = transactionRepository
            .findAll(
                transactionSpec.apply(),
                Sort.by(Sort.Direction.DESC, "registeredAt"))
            .stream()
            .map(transactionMapper::toSummary)
            .toList();

        final BigDecimal INCOME = transactions.stream()
            .filter(transaction -> transaction.getType().equals(TransactionType.CREDIT))
            .map(TransactionSummaryDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal EXPENSES = transactions.stream()
            .filter(transaction -> transaction.getType().equals(TransactionType.DEBIT))
            .map(TransactionSummaryDTO::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return mapper.toSummary(entity, INCOME, EXPENSES, transactions);
    }

    @Transactional
    public WalletResponseDTO create(WalletRequestDTO wallet) {
        log.info("Creating Wallet");

        var entity = mapper.toEntity(wallet);

        var walletCreated = repository.save(entity);
        syncCreditCardInvoice(walletCreated, wallet);
        accountBalanceService.updateBalance(
            walletCreated.getAccount(),
            walletCreated.getBalance(),
            TransactionType.CREDIT,
            true,
            false
        );
        return mapper.toResponse(walletCreated);
    }

    @Transactional
    public WalletResponseDTO update(WalletRequestDTO wallet) {
        log.info("Updating Wallet");

        Bank bank = null;

        var entity = repository.findById(wallet.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + wallet.getId()));
        if (wallet.getBankId() != null) {
            bank = bankRepository.findById(wallet.getBankId()).get();
        }

        entity.setName(wallet.getName());
        entity.setDescription(wallet.getDescription());
        entity.setBalance(wallet.getBalance());
        entity.setCardDigits(wallet.getCardDigits());
        entity.setType(wallet.getType());
        entity.setColor(wallet.getColor());
        entity.setBank(bank);

        var walletUpdated = repository.save(entity);
        syncCreditCardInvoice(walletUpdated, wallet);
        return mapper.toResponse(walletUpdated);
    }

    public WalletResponseDTO incrementAmount(UUID id, BigDecimal amount) {
        log.info("Increment amount by Goal Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        entity.setBalance(entity.getBalance().add(amount));

        var walletUpdated = repository.save(entity);
        return mapper.toResponse(walletUpdated);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting Wallet");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }

    private void syncCreditCardInvoice(Wallet wallet, WalletRequestDTO request) {
        if (wallet.getType() != WalletType.CREDIT_CARD) {
            return;
        }

        boolean hasAnyInvoiceData = request.getCreditLimit() != null
            || request.getClosingDate() != null
            || request.getExpirationDate() != null;

        if (!hasAnyInvoiceData) {
            return;
        }

        if (request.getCreditLimit() == null
            || request.getClosingDate() == null
            || request.getExpirationDate() == null) {
            throw new IllegalArgumentException(
                "Credit cards require a limit, closing date and expiration date"
            );
        }

        if (request.getCreditLimit().signum() <= 0) {
            throw new IllegalArgumentException("The credit card limit must be greater than zero");
        }

        if (!request.getExpirationDate().isAfter(request.getClosingDate())) {
            throw new IllegalArgumentException("The expiration date must be after the closing date");
        }

        Invoice invoice = request.getInvoiceId() == null
            ? new Invoice()
            : invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Not found Invoice Id: " + request.getInvoiceId()));

        if (invoice.getWallet() != null && !invoice.getWallet().getId().equals(wallet.getId())) {
            throw new IllegalArgumentException("The invoice does not belong to this credit card");
        }

        invoice.setWallet(wallet);
        invoice.setLimit(request.getCreditLimit());
        invoice.setClosingDate(request.getClosingDate());
        invoice.setExpirationDate(request.getExpirationDate());
        invoice.setDaysUntilExpiration((int) ChronoUnit.DAYS.between(
            request.getClosingDate(),
            request.getExpirationDate()
        ));

        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.PAYING);
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        if (!wallet.getInvoices().contains(savedInvoice)) {
            wallet.getInvoices().add(savedInvoice);
        }
    }
}
