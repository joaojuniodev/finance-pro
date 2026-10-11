package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.contract.ICrud;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.invoice.dto.InvoiceDetailsResponseDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceRequestDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceResponseDTO;
import br.com.financepro.financePro.invoice.filter.InvoiceFilter;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.invoice.InvoiceMapper;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService implements ICrud<Invoice, InvoiceResponseDTO, InvoiceRequestDTO, InvoiceFilter> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class.getName());

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private InvoiceMapper mapper;

    @Override
    public List<InvoiceResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public List<InvoiceResponseDTO> filter(InvoiceFilter filter) {
        return List.of();
    }

    @Override
    public InvoiceResponseDTO getById(UUID id) {
        log.info("Getting Invoice by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));

        return mapper.toResponse(entity);
    }

    @Override
    public InvoiceResponseDTO create(InvoiceRequestDTO invoice) {
        log.info("Creating new Invoice");

        var entity = mapper.toEntity(invoice);

        entity.setExpirationDate(calculateNextExpirationDate(
            invoice.closingDate(),
            invoice.daysUntilExpiration()
        ));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public InvoiceResponseDTO update(InvoiceRequestDTO invoice) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    private LocalDate calculateNextExpirationDate(LocalDate date, Integer daysUntilExpiration) {
        return date.plusDays(daysUntilExpiration);
    }
}