package br.com.financepro.financePro.mapper.installmentPlan;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstallmentPlanMapper implements ObjectMapper<InstallmentPlan, InstallmentPlanResponseDTO, InstallmentPlanRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private InstallmentMapper installmentMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public InstallmentPlan toEntity(InstallmentPlanRequestDTO request) {
        var wallet = walletRepository.findById(request.walletId())
            .orElseThrow(() -> new NotFoundException("Not found this Wallet Id: " + request.walletId()));
        InstallmentPlan installmentPlan = new InstallmentPlan();
        installmentPlan.setId(request.id());
        installmentPlan.setDescription(request.description());
        installmentPlan.setValue(request.value());
        installmentPlan.setTimes(request.times());
        installmentPlan.setFees(request.fees());
        installmentPlan.setStatus(request.status());
        return installmentPlan;
    }

    @Override
    public InstallmentPlanResponseDTO toResponse(InstallmentPlan entity) {
        System.out.println("6");
        return new InstallmentPlanResponseDTO(
            entity.getId(),
            entity.getDescription(),
            entity.getValue(),
            entity.getTimes(),
            entity.getFees(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getStatus(),
            walletMapper.toResponse(entity.getWallet()),
            entity.getInstallments().stream().map(installmentMapper::toResponse).toList()
        );
    }
}