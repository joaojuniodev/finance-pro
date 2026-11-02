package br.com.financepro.financePro.goal.repository;

import br.com.financepro.financePro.goal.model.Goal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    @EntityGraph(attributePaths = "category")
    List<Goal> findByAccountIdOrderByNameAsc(UUID accountId);

    @EntityGraph(attributePaths = "category")
    Optional<Goal> findByIdAndAccountId(UUID id, UUID accountId);

    @Query(value = "SELECT * FROM goals WHERE id = :id AND account_id = :accountId FOR UPDATE", nativeQuery = true)
    Optional<Goal> findForUpdate(@Param("id") UUID id, @Param("accountId") UUID accountId);
}
