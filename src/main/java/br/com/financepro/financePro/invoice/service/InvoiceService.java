package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.common.contract.ICrud;
import br.com.financepro.financePro.invoice.dto.InvoiceRequestDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceResponseDTO;
import br.com.financepro.financePro.invoice.filter.InvoiceFilter;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService implements ICrud<Invoice, InvoiceResponseDTO, InvoiceRequestDTO, InvoiceFilter> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class.getName());

    @Autowired
    private InvoiceRepository repository;

    @Override
    public List<InvoiceResponseDTO> getAll() {
        return List.of();
    }

    @Override
    public List<InvoiceResponseDTO> filter(InvoiceFilter invoiceFilter) {
        return List.of();
    }

    @Override
    public InvoiceResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public InvoiceResponseDTO create(InvoiceRequestDTO request) {
        return null;
    }

    @Override
    public InvoiceResponseDTO update(InvoiceRequestDTO request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}