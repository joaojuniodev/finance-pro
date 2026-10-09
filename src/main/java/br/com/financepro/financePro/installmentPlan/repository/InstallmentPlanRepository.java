package br.com.financepro.financePro.installmentPlan.repository;

import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, UUID> {
}