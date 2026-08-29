package br.com.financepro.financePro.transaction.repository;

import br.com.financepro.financePro.transaction.dto.projection.CategoryAmountProjection;
import br.com.financepro.financePro.transaction.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
        SELECT c.id AS id, c.name AS name, c.color AS color, c.icon AS icon, SUM(t.amount) as totalAmount
        FROM Transaction t
        JOIN t.category c
        WHERE t.account.id = :accountId AND t.type = DEBIT
        GROUP BY c.id, c.name, c.color, c.icon
        ORDER BY totalAmount DESC
    """)
    List<CategoryAmountProjection> findSpendingByCategory(@Param("accountId") UUID accountId);

    @Query(value = """
        SELECT id, name, color, icon, total_amount, percentage
        FROM (
            SELECT
                c.id AS id,
                c.name AS name,
                c.color AS color,
                c.icon AS icon,
                SUM(t.amount) AS total_amount,
                (SUM(t.amount) * 100.0 / SUM(SUM(t.amount)) OVER ()) AS percentage
            FROM transactions t
            JOIN categories c ON c.id = t.category_id
            WHERE t.account_id = :accountId AND t.type = DEBIT
            GROUP BY c.name, c.color
        ) sub
        ORDER BY total_amount DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Object[]> findTopSpendingCategoryNative(@Param("accountId") UUID accountId);
}