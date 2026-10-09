package br.com.financepro.financePro.mapper.installment;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.dto.InstallmentRequestDTO;
import br.com.financepro.financePro.installment.dto.InstallmentResponseDTO;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.installmentPlan.repository.InstallmentPlanRepository;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.installmentPlan.InstallmentPlanMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class InstallmentMapper implements ObjectMapper<Installment, InstallmentResponseDTO, InstallmentRequestDTO> {

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private InstallmentPlanRepository installmentPlanRepository;

    @Override
    public Installment toEntity(InstallmentRequestDTO request) {
        var installmentPlan = installmentPlanRepository.findById(request.installmentPlanId())
            .orElseThrow(() -> new NotFoundException("Not found this Installment Plan Id: " + request.installmentPlanId()));

        Installment installment = new Installment();
        installment.setId(request.id());
        installment.setValue(request.value());
        installment.setPaid(request.isPaid());
        installment.setMonth(request.month());
        installment.setYear(request.year());
        installment.setInstallmentPlan(installmentPlan);
        return installment;
    }

    @Override
    public InstallmentResponseDTO toResponse(Installment entity) {
        return new InstallmentResponseDTO(
            entity.getId(),
            entity.getValue(),
            entity.getPaid(),
            entity.getMonth(),
            entity.getYear()
        );
    }
}