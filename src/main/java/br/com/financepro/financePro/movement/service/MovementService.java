package br.com.financepro.financePro.movement.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.movement.MovementMapper;
import br.com.financepro.financePro.movement.dto.MovementRequestDTO;
import br.com.financepro.financePro.movement.dto.MovementResponseDTO;
import br.com.financepro.financePro.movement.exception.InvalidTransferException;
import br.com.financepro.financePro.movement.model.Movement;
import br.com.financepro.financePro.movement.repository.MovementRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MovementService {
    private final MovementRepository repository;
    private final WalletRepository walletRepository;
    private final MovementMapper mapper;
    private final WalletBalanceService walletBalanceService;

    public MovementService(MovementRepository repository, WalletRepository walletRepository,
                           MovementMapper mapper, WalletBalanceService walletBalanceService) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.mapper = mapper;
        this.walletBalanceService = walletBalanceService;
    }

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> getAll(String username) {
        return repository.findAllByFromWalletAccountUserUsernameOrderByRegisteredAtDesc(username)
            .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MovementResponseDTO getById(UUID id, String username) {
        return mapper.toResponse(repository.findByIdAndFromWalletAccountUserUsername(id, username)
            .orElseThrow(() -> new NotFoundException("Transferência não encontrada.")));
    }

    @Transactional
    public MovementResponseDTO create(MovementRequestDTO request, String username) {
        validate(request);
        UUID sourceId = request.getFromWalletId();
        UUID destinationId = request.getToWalletId();
        // Acquire locks in a stable order, including transfers in opposite directions.
        UUID firstId = sourceId.compareTo(destinationId) < 0 ? sourceId : destinationId;
        UUID secondId = firstId.equals(sourceId) ? destinationId : sourceId;
        Wallet first = ownedWallet(firstId, username);
        Wallet second = ownedWallet(secondId, username);
        Wallet source = firstId.equals(sourceId) ? first : second;
        Wallet destination = firstId.equals(sourceId) ? second : first;

        var existing = repository.findByRequestId(request.getRequestId());
        if (existing.isPresent()) {
            Movement previous = existing.get();
            if (!previous.getFromWallet().getId().equals(sourceId)
                || !previous.getToWallet().getId().equals(destinationId)
                || previous.getAmount().compareTo(request.getAmount()) != 0) {
                throw new InvalidTransferException("A solicitação já foi usada para outra transferência.");
            }
            return mapper.toResponse(previous);
        }

        walletBalanceService.transfer(source, destination, request.getAmount());
        Movement movement = new Movement(null, request.getAmount(), source, destination, LocalDateTime.now());
        movement.setRequestId(request.getRequestId());
        return mapper.toResponse(repository.save(movement));
    }

    private Wallet ownedWallet(UUID id, String username) {
        return walletRepository.findOwnedByIdForUpdate(id, username)
            .orElseThrow(() -> new NotFoundException("Carteira não encontrada para esta conta."));
    }

    private void validate(MovementRequestDTO request) {
        if (request == null || request.getRequestId() == null || request.getId() != null
            || request.getFromWalletId() == null || request.getToWalletId() == null
            || request.getFromWalletId().equals(request.getToWalletId())) {
            throw new InvalidTransferException("Escolha carteiras diferentes e informe uma solicitação válida.");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 2
            || amount.precision() - amount.scale() > 36) {
            throw new InvalidTransferException("Informe um valor positivo com até duas casas decimais.");
        }
    }
}
