package br.com.financepro.financePro.mapper.movement;

import br.com.financepro.financePro.movement.dto.MovementRequestDTO;
import br.com.financepro.financePro.movement.dto.MovementResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.movement.model.Movement;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper implements ObjectMapper<Movement, MovementResponseDTO, MovementRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Movement toEntity(MovementRequestDTO request) {
        Wallet fromWallet = walletRepository.findById(request.getFromWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this From Wallet Id: " + request.getFromWalletId()));
        Wallet toWallet = walletRepository.findById(request.getToWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this To Wallet Id: " + request.getToWalletId()));
        return new Movement(
            request.getId(),
            request.getAmount(),
            fromWallet,
            toWallet,
            request.getRegisteredAt()
        );
    }

    @Override
    public MovementResponseDTO toResponse(Movement entity) {
        return new MovementResponseDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getFromWallet().getId(),
            entity.getToWallet().getId(),
            entity.getRegisteredAt()
        );
    }
}
