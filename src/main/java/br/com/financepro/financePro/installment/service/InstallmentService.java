package br.com.financepro.financePro.installment.service;

import br.com.financepro.financePro.installment.dto.InstallmentRequestDTO;
import br.com.financepro.financePro.installment.dto.InstallmentResponseDTO;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InstallmentService {

    private final Logger log = LoggerFactory.getLogger(InstallmentService.class.getName());

    @Autowired
    private InstallmentRepository repository;

    @Autowired
    private InstallmentMapper mapper;

    public List<InstallmentResponseDTO> getAll(UUID accountId) {
        log.info("Getting All Installments");

        return repository.findAll()
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    public InstallmentResponseDTO create(InstallmentRequestDTO installment) {
        log.info("Creating new Installment");

        var entity = mapper.toEntity(installment);
        return mapper.toResponse(repository.save(entity));
    }
}