package br.com.financepro.financePro.mapper.invoice;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.invoice.dto.InvoiceDetailsResponseDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceInstallmentResponseDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceRequestDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceResponseDTO;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InvoiceMapper implements ObjectMapper<Invoice, InvoiceResponseDTO, InvoiceRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Invoice toEntity(InvoiceRequestDTO request) {
        var wallet = walletRepository.findById(request.walletId())
            .orElseThrow(() -> new NotFoundException("Not found this Wallet Id: " + request.walletId()));

        Invoice invoice = new Invoice();
        invoice.setId(request.id());
        invoice.setLimit(request.limit());
        invoice.setStatus(request.status());
        invoice.setClosingDate(request.closingDate());
        invoice.setDaysUntilExpiration(request.daysUntilExpiration());
        invoice.setWallet(wallet);
        return invoice;
    }

    @Override
    public InvoiceResponseDTO toResponse(Invoice entity) {
        return new InvoiceResponseDTO(
            entity.getId(),
            entity.getLimit(),
            entity.getStatus(),
            entity.getClosingDate(),
            entity.getExpirationDate(),
            entity.getDaysUntilExpiration()
        );
    }

    public InvoiceDetailsResponseDTO toDetails(Invoice invoice, List<Installment> installments) {
        BigDecimal totalAmount = installments.stream()
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidAmount = installments.stream()
            .filter(installment -> Boolean.TRUE.equals(installment.getPaid()))
            .map(Installment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingAmount = totalAmount.subtract(paidAmount);

        return new InvoiceDetailsResponseDTO(
            invoice.getId(),
            invoice.getClosingDate(),
            invoice.getExpirationDate(),
            invoice.getStatus(),
            totalAmount,
            paidAmount,
            pendingAmount,
            installments.stream().map(installment -> new InvoiceInstallmentResponseDTO(
                installment.getId(),
                installment.getInstallmentPlan().getId(),
                installment.getInstallmentPlan().getDescription(),
                installment.getValue(),
                installment.getPaid(),
                installment.getMonth(),
                installment.getYear()
            )).toList()
        );
    }
}