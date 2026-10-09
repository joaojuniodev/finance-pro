package br.com.financepro.financePro.installmentPlan.dto;

import br.com.financepro.financePro.common.enums.InstallmentPlanStatus;
import br.com.financepro.financePro.installment.dto.InstallmentResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InstallmentPlanResponseDTO(
    UUID id,
    String description,
    BigDecimal value,
    BigDecimal times,
    BigDecimal fees,
    LocalDate startDate,
    LocalDate endDate,
    InstallmentPlanStatus status,
    WalletResponseDTO wallet,
    List<InstallmentResponseDTO> installments
) {
}