package br.com.financepro.financePro.movement.repository;

import br.com.financepro.financePro.movement.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {
}
