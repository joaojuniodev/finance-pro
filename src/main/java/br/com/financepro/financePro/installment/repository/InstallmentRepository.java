package br.com.financepro.financePro.installment.repository;

import br.com.financepro.financePro.installment.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    List<Installment> findAllByInstallmentPlanWalletIdAndMonthAndYear(
        UUID walletId,
        Integer month,
        Integer year
    );
}
