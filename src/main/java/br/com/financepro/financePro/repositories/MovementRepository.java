package br.com.financepro.financePro.repositories;

import br.com.financepro.financePro.models.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {
}
