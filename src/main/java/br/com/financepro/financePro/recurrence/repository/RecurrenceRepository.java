package br.com.financepro.financePro.recurrence.repository;

import br.com.financepro.financePro.recurrence.model.Recurrence;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecurrenceRepository extends JpaRepository<Recurrence, UUID>, JpaSpecificationExecutor<Recurrence> {

    List<Recurrence> findAll(Specification<Recurrence> spec);

    List<Recurrence> findByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}