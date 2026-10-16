package br.com.financepro.financePro.installment.repository;

import br.com.financepro.financePro.installment.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    List<Installment> findAllByInstallmentPlanInvoiceIdAndMonthAndYear(
        UUID invoiceId,
        Integer month,
        Integer year
    );

    @Query("""
        SELECT i
        FROM Installment i
        WHERE i.account.id = :accountId
          AND i.month = :month
          AND i.year = :year
    """)
    List<Installment> findForPeriod(
        @Param("accountId") UUID accountId,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}