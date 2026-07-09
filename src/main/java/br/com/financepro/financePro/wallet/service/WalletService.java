package br.com.financepro.financePro.wallet.service;


import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.service.AccountBalanceService;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.transaction.service.TransactionService;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.repository.spec.WalletSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final Logger log = LoggerFactory.getLogger(WalletService.class.getName());

    @Autowired
    private WalletRepository repository;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private WalletMapper mapper;

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

    @Transactional
    public WalletResponseDTO create(WalletRequestDTO wallet) {
        log.info("Creating Wallet");

        var walletCreated = repository.save(mapper.toEntity(wallet));
        accountBalanceService.updateBalance(
            walletCreated.getAccount(),
            walletCreated.getBalance(),
            TransactionType.CREDIT,
            false
        );
        return mapper.toResponse(walletCreated);
    }

    public WalletResponseDTO update(WalletRequestDTO wallet) {
        log.info("Updating Wallet");

        var entity = repository.findById(wallet.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + wallet.getId()));
        entity.setName(wallet.getName());
        entity.setDescription(wallet.getDescription());
        entity.setBalance(wallet.getBalance());
        entity.setCardDigits(wallet.getCardDigits());

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