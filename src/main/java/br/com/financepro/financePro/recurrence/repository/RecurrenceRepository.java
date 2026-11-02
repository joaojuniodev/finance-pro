package br.com.financepro.financePro.recurrence.repository;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecurrenceRepository extends JpaRepository<Recurrence, UUID>, JpaSpecificationExecutor<Recurrence> {

    Optional<Recurrence> findByIdAndAccountUserUsername(UUID id, String username);

    @Query("""
        select r from Recurrence r where r.wallet.id = :walletId
          and r.status = br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus.ACTIVE
          and r.type = br.com.financepro.financePro.common.enums.RecurrenceType.DEBIT
          and r.amount > 0 and r.dayOne between 1 and 31
          and (r.frequencyType <> br.com.financepro.financePro.common.enums.FrequencyType.BIWEEKLY
               or (r.dayTwo > r.dayOne and r.dayTwo <= 31))
          and (r.frequencyType <> br.com.financepro.financePro.common.enums.FrequencyType.YEARLY
               or r.monthOfTheYear between 1 and 12)
        """)
    List<Recurrence> findActiveCardExpenses(@Param("walletId") UUID walletId);

    @Modifying(flushAutomatically = true)
    @Query("""
        update Recurrence r set r.lastExecutionDate = :paidDate, r.nextExecutionDate = :nextDate
        where r.id = :id and r.wallet.id = :walletId and r.nextExecutionDate = :expectedDate
          and r.frequencyType = :frequency and r.dayOne = :dayOne
          and (r.dayTwo = :dayTwo or (r.dayTwo is null and :dayTwo is null))
          and (r.monthOfTheYear = :month or (r.monthOfTheYear is null and :month is null))
        """)
    int advanceCardSchedule(@Param("id") UUID id, @Param("walletId") UUID walletId,
        @Param("expectedDate") LocalDate expectedDate, @Param("frequency") FrequencyType frequency,
        @Param("dayOne") Integer dayOne, @Param("dayTwo") Integer dayTwo, @Param("month") Integer month,
        @Param("paidDate") LocalDate paidDate, @Param("nextDate") LocalDate nextDate);

    @EntityGraph(attributePaths = {"wallet.invoices"})
    List<Recurrence> findAll(Specification<Recurrence> spec);

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.status = ACTIVE
          AND r.executionType = :executionType
          AND (r.wallet.type IS NULL OR r.wallet.type <> br.com.financepro.financePro.common.enums.WalletType.CREDIT_CARD)
          AND r.nextExecutionDate <= :date
    """)
    List<Recurrence> findPendingAutomaticRecurrences(
        @Param("executionType") ExecutionType executionType,
        @Param("date") LocalDate date
    );

    @Query("""
        SELECT r
        FROM Recurrence r
        WHERE r.account.id = :accountId
          AND r.status = ACTIVE
          AND r.nextExecutionDate < :date
    """)
    List<Recurrence> findOccurrencesWithPeriod(
        @Param("accountId") UUID accountId,
        @Param("date") LocalDate date
    );

}
