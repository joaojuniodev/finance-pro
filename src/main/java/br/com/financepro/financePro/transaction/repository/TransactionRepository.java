package br.com.financepro.financePro.transaction.repository;

import br.com.financepro.financePro.transaction.repository.projection.TopCategoryProjection;
import br.com.financepro.financePro.transaction.repository.projection.WeeklyOverviewProjection;
import br.com.financepro.financePro.transaction.model.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findAll(Specification<Transaction> spec);

    @Query(value = """
        SELECT
            EXTRACT(MONTH FROM t.registered_at) AS month,
            CASE
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 1  AND 7  THEN 1
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 8  AND 14 THEN 2
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 15 AND 21 THEN 3
                ELSE 4
            END AS week,
            COALESCE(SUM(
                CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END
            ), 0) AS income,
            COALESCE(SUM(
                CASE WHEN t.type = 'DEBIT' THEN t.amount ELSE 0 END
            ), 0) AS expenses
        FROM transactions t
        WHERE
            t.account_id = :accountId
            AND EXTRACT(YEAR FROM t.registered_at) = :year
        GROUP BY
            EXTRACT(MONTH FROM t.registered_at),
            CASE
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 1  AND 7  THEN 1
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 8  AND 14 THEN 2
                WHEN EXTRACT(DAY FROM t.registered_at) BETWEEN 15 AND 21 THEN 3
                ELSE 4
            END
        ORDER BY
            month, week
    """, nativeQuery = true)
    List<WeeklyOverviewProjection> findWeeklyOverview(
        UUID accountId,
        Integer year
    );

    @Query(value = """
        SELECT
            c.id           AS categoryId,
            c.name         AS categoryName,
            c.type         AS categoryType,
            c.system       AS categorySystem,
            SUM(t.amount)  AS total
        FROM transactions t
        INNER JOIN categories c ON c.id = t.category_id
        WHERE
            t.account_id = :accountId
            AND t.type = 'DEBIT'
            AND EXTRACT(MONTH FROM t.registered_at) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR  FROM t.registered_at) = EXTRACT(YEAR  FROM CURRENT_DATE)
        GROUP BY c.id, c.name, c.type, c.system
        ORDER BY total DESC
        LIMIT 1
    """, nativeQuery = true)
    TopCategoryProjection findTopCategoryCurrentMonth(UUID accountId);
}