package br.com.financepro.financePro.recurrence.repository;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecurrenceRepository extends JpaRepository<Recurrence, UUID>, JpaSpecificationExecutor<Recurrence> {

    List<Recurrence> findAll(Specification<Recurrence> spec);

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.active = true
          AND r.nextExecutionDate <= :date
    """)
    List<Recurrence> findPendingAutomaticRecurrences(
        @Param("executionType") ExecutionType executionType,
        @Param("date") LocalDate date
    );

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.active = true
          AND r.executionType = :executionType
          AND r.nextExecutionDate = :date
    """)
    List<Recurrence> findPendingThisTodayManualRecurrences(
        @Param("executionType") ExecutionType executionType,
        @Param("date") LocalDate date
    );

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.active = true
          AND r.nextExecutionDate < :date
    """)
    List<Recurrence> findDelayedRecurrences(@Param("date") LocalDate date);

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.active = true
          AND r.nextExecutionDate BETWEEN :startDate AND :endDate
    """)
    List<Recurrence> findUpcomingRecurrences(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}