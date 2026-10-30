package br.com.financepro.financePro.movement.repository;

import br.com.financepro.financePro.movement.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {
    List<Movement> findAllByFromWalletAccountUserUsernameOrderByRegisteredAtDesc(String username);
    Optional<Movement> findByIdAndFromWalletAccountUserUsername(UUID id, String username);
    Optional<Movement> findByRequestId(UUID requestId);
}
