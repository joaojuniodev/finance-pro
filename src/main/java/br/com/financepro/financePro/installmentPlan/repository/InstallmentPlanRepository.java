package br.com.financepro.financePro.installmentPlan.repository;

import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, UUID> {

    @EntityGraph(attributePaths = {"invoice", "installments"})
    List<InstallmentPlan> findAllByInvoice_Wallet_IdOrderByStartDateDesc(UUID walletId);
}
