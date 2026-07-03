package br.com.financepro.financePro.movement.service;

import br.com.financepro.financePro.movement.dto.MovementRequestDTO;
import br.com.financepro.financePro.movement.dto.MovementResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.movement.MovementMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.movement.repository.MovementRepository;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class MovementService {

    private final Logger log = LoggerFactory.getLogger(MovementService.class.getName());

    @Autowired
    private MovementRepository repository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MovementMapper mapper;

    @Autowired
    private WalletBalanceService walletBalanceService;

    public List<MovementResponseDTO> getAll() {
        log.info("Getting All Movements");

        return repository.findAll()
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public MovementResponseDTO getById(UUID id) {
        log.info("Getting Movement by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional
    public MovementResponseDTO create(MovementRequestDTO movement) {
        log.info("Creating Movement");

        movementBetweenWallets(movement);

        var movementCreated = repository.save(mapper.toEntity(movement));
        return mapper.toResponse(movementCreated);
    }

    public void delete(UUID id) {
        log.info("Deleting Movement");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }

    @Transactional
    private void movementBetweenWallets(MovementRequestDTO movement) {
        final BigDecimal amount = movement.getAmount();

        Wallet fromWallet = walletRepository.findById(movement.getFromWalletId())
            .orElseThrow(() -> new NotFoundException("Not found From Wallet this Id: " + movement.getFromWalletId()));
        Wallet toWallet = walletRepository.findById(movement.getToWalletId())
            .orElseThrow(() -> new NotFoundException("Not found To Wallet this Id: " + movement.getToWalletId()));

        walletBalanceService.transfer(fromWallet, toWallet, amount);
    }
}