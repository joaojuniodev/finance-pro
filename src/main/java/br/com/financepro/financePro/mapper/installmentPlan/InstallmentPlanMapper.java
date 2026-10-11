package br.com.financepro.financePro.mapper.installmentPlan;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import br.com.financepro.financePro.mapper.invoice.InvoiceMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstallmentPlanMapper implements ObjectMapper<InstallmentPlan, InstallmentPlanResponseDTO, InstallmentPlanRequestDTO> {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InstallmentMapper installmentMapper;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Override
    public InstallmentPlan toEntity(InstallmentPlanRequestDTO request) {
        var invoice = invoiceRepository.findById(request.invoiceId())
            .orElseThrow(() -> new NotFoundException("Not found this Invoice Id: " + request.invoiceId()));
        InstallmentPlan installmentPlan = new InstallmentPlan();
        installmentPlan.setId(request.id());
        installmentPlan.setDescription(request.description());
        installmentPlan.setValue(request.value());
        installmentPlan.setTimes(request.times());
        installmentPlan.setFees(request.fees());
        installmentPlan.setStatus(request.status());
        installmentPlan.setInvoice(invoice);
        return installmentPlan;
    }

    @Override
    public InstallmentPlanResponseDTO toResponse(InstallmentPlan entity) {
        return new InstallmentPlanResponseDTO(
            entity.getId(),
            entity.getDescription(),
            entity.getValue(),
            entity.getTimes(),
            entity.getFees(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getStatus(),
            invoiceMapper.toResponse(entity.getInvoice()),
            entity.getInstallments().stream().map(installmentMapper::toResponse).toList()
        );
    }
}