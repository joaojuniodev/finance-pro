package br.com.financepro.financePro.services;

import br.com.financepro.financePro.data.dtos.request.account.AccountRequestDTO;
import br.com.financepro.financePro.data.dtos.response.account.AccountResponseDTO;
import br.com.financepro.financePro.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.account.AccountMapper;
import br.com.financepro.financePro.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class.getName());

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountMapper mapper;

    public List<AccountResponseDTO> getAll() {
        log.info("Getting All Accounts");

        return repository.findAll()
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public AccountResponseDTO getById(UUID id) {
        log.info("Getting Account by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public AccountResponseDTO create(AccountRequestDTO account) {
        log.info("Creating Account");

        var accountCreated = repository.save(mapper.toEntity(account));
        return mapper.toResponse(accountCreated);
    }

    public AccountResponseDTO update(AccountRequestDTO account) {
        log.info("Updating Account");

        var entity = repository.findById(account.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + account.getId()));
        entity.setIncome(account.getIncome());
        entity.setExpenses(account.getExpenses());
        entity.setNetIncome(account.getNetIncome());

        var accountUpdated = repository.save(entity);
        return mapper.toResponse(accountUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Account");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }
}