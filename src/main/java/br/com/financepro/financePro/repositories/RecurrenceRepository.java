package br.com.financepro.financePro.repositories;

import br.com.financepro.financePro.models.Recurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecurrenceRepository extends JpaRepository<Recurrence, UUID> {
}
