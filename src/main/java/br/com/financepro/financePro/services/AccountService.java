package br.com.financepro.financePro.services;

import br.com.financepro.financePro.mapper.account.AccountMapper;
import br.com.financepro.financePro.models.Account;
import br.com.financepro.financePro.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountMapper mapper;

    public List<Account> getAll() {
        return null;
    }
}