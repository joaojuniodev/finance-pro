package br.com.financepro.financePro.account.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyResetScheduler {

    private final Logger log = LoggerFactory.getLogger(MonthlyResetScheduler.class.getName());

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyAccountSummaries() {
        log.info("Iniciando reset mensal de income/expenses");
        accountService.resetMonthlySummaryForAllAccounts();
        log.info("Reset mensal concluído");
    }
}