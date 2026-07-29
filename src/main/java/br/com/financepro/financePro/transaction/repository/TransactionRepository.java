package br.com.financepro.financePro.transaction.repository;

import br.com.financepro.financePro.transaction.dto.projection.CategoryExpenseProjection;
import br.com.financepro.financePro.transaction.dto.projection.TopCategoryProjection;
import br.com.financepro.financePro.transaction.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findAll(Specification<Transaction> spec);

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.account.id = :accountId
        ORDER BY t.registeredAt DESC
    """)
    List<Transaction> findRecentAccount(UUID accountId, Pageable pageable);

    @Query("""
        SELECT new br.com.financepro.financePro.transaction.dto.projection.CategoryExpenseProjection(
            c,
            SUM(t.amount)
        )
        FROM Transaction t
        JOIN t.category c
        WHERE t.account.id = :accountId
            AND t.type = br.com.financepro.financePro.common.enums.TransactionType.DEBIT
            AND t.registeredAt BETWEEN :startDate AND :endDate
        GROUP BY c
        ORDER BY SUM(t.amount) DESC
    """)
    List<CategoryExpenseProjection> findExpensesByCategory(
        UUID accountId,
        LocalDateTime startDate,
        LocalDateTime endDate
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