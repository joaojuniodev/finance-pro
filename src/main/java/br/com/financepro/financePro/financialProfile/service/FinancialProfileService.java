package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileRequest;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(FinancialProfileService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public void firstStepsByUser(FinancialProfileRequest firstSteps) {
        log.info("Registering first steps for User");

        
    }
}