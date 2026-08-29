package br.com.financepro.financePro.wallet.service;

import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.bank.model.Bank;
import br.com.financepro.financePro.bank.repository.BankRepository;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.transaction.dto.response.TransactionSummaryDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletSummaryDTO;
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

        var walletCreated = repository.save(mapper.toEntity(wallet));
        accountBalanceService.updateBalance(
            walletCreated.getAccount(),
            walletCreated.getBalance(),
            TransactionType.CREDIT,
            true,
            false
        );
        return mapper.toResponse(walletCreated);
    }

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
}