package br.com.financepro.financePro.wallet.repository;

import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID>, JpaSpecificationExecutor<Wallet> {

    @EntityGraph(attributePaths = {"invoices"})
    List<Wallet> findAll(Specification<Wallet> spec);

    @EntityGraph(attributePaths = {"invoices"})
    Optional<Wallet> findById(UUID id);
}