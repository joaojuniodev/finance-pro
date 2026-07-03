package br.com.financepro.financePro.wallet.repository;

import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID>, JpaSpecificationExecutor<Wallet> {

    List<Wallet> findAll(Specification<Wallet> spec);

    @Query("SELECT w FROM Wallet w WHERE LOWER(w.name) = LOWER(:name) AND w.account.id = :accountId")
    Optional<Wallet> findByName(@Param("name") String name, @Param("accountId") UUID accountId);
}