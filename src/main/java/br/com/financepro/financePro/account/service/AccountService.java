package br.com.financepro.financePro.account.service;

import br.com.financepro.financePro.account.dto.AccountRequestDTO;
import br.com.financepro.financePro.account.dto.AccountResponseDTO;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.account.AccountMapper;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(AccountService.class.getName());

    @Autowired
    private AccountRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountMapper mapper;

    public List<AccountResponseDTO> getAll() {
        log.info("Getting All Accounts");

        return repository.findAll()
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public AccountResponseDTO getByUsername(String username) {
        log.info("Getting Account by Username");

        var entity = repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Not found Account by username: " + username));

        return mapper.toResponse(entity);
    }

    public AccountResponseDTO getById(UUID id) {
        log.info("Getting Account by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional
    public AccountResponseDTO startAccount(String username) {
        log.info("Start User Account");

        User user = userRepository.findByUserName(username);

        Account account = new Account(user);
        account.setCurrentBalance(BigDecimal.ZERO);
        account.setIncome(BigDecimal.ZERO);
        account.setExpenses(BigDecimal.ZERO);
        account.setNetIncome(BigDecimal.ZERO);

        var accountCreated = repository.save(account);
        return mapper.toResponse(accountCreated);
    }

    @Transactional
    public void resetMonthlySummary(Account account) {
        account.setIncome(BigDecimal.ZERO);
        account.setExpenses(BigDecimal.ZERO);
        account.setNetIncome(BigDecimal.ZERO);
        account.setBiggestExpenseCategory(null);
        account.setBiggestExpenseValue(null);

        repository.save(account);

        log.info("Resumo mensal zerado para a conta {}", account.getId());
    }

    @Transactional
    public void resetMonthlySummaryForAllAccounts() {
        List<Account> accounts = repository.findAll();
        accounts.forEach(this::resetMonthlySummary);
        log.info("Resumo mensal zerado para {} contas", accounts.size());
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