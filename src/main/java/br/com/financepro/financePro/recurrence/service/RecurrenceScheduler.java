package br.com.financepro.financePro.recurrence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RecurrenceScheduler {

    @Autowired
    private RecurrenceExecutionService recurrenceExecutionService;

    @Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
    public void executeDailyRecurrences() {
        recurrenceExecutionService.executeDueRecurrences(LocalDate.now());
    }
}