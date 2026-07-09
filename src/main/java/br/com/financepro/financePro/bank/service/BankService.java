package br.com.financepro.financePro.bank.service;

import br.com.financepro.financePro.bank.dto.BankRequestDTO;
import br.com.financepro.financePro.bank.dto.BankResponseDTO;
import br.com.financepro.financePro.bank.repository.BankRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.bank.BankMapper;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.repository.spec.WalletSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BankService {

    private final Logger log = LoggerFactory.getLogger(BankService.class.getName());

    @Autowired
    private BankRepository repository;

    @Autowired
    private BankMapper mapper;

    public List<BankResponseDTO> getAll() {
        log.info("Getting All Banks");

        return repository
            .findAll()
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public BankResponseDTO getById(UUID id) {
        log.info("Getting Bank by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public BankResponseDTO create(BankRequestDTO bank) {
        log.info("Creating Bank");

        var bankCreated = repository.save(mapper.toEntity(bank));
        return mapper.toResponse(bankCreated);
    }

    public BankResponseDTO update(BankRequestDTO bank) {
        log.info("Updating Bank");

        var entity = repository.findById(bank.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + bank.getId()));
        entity.setName(bank.getName());
        entity.setColor(bank.getColor());
        entity.setName(bank.getName());
        entity.setGradient(bank.getGradient());
        entity.setShadow(bank.getShadow());
        entity.setIcon(bank.getIcon());

        var bankUpdated = repository.save(entity);
        return mapper.toResponse(bankUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Bank");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }
}