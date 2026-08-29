package br.com.financepro.financePro.account.service.shared;

import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.transaction.dto.projection.DailyFlowProjection;
import br.com.financepro.financePro.transaction.dto.response.EvolutionDataPointDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class AccountEvolutionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<EvolutionDataPointDTO> getWeeklyEvolution(UUID accountId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // últimos 7 dias, incluindo hoje

        List<DailyFlowProjection> rows = transactionRepository.findDailyFlow(
            accountId,
            startDate.atStartOfDay()
        );

        // Agrupa por data, separando income/expense
        Map<LocalDate, BigDecimal> incomeByDate = new HashMap<>();
        Map<LocalDate, BigDecimal> expenseByDate = new HashMap<>();

        for (DailyFlowProjection row : rows) {
            if (row.getType() == TransactionType.CREDIT) {
                incomeByDate.put(row.getDate(), row.getTotal());
            } else if (row.getType() == TransactionType.DEBIT) {
                expenseByDate.put(row.getDate(), row.getTotal());
            }
        }

        // Preenche todos os 7 dias, mesmo os sem transação (zero)
        List<EvolutionDataPointDTO> evolution = new ArrayList<>();
        BigDecimal runningBalance = BigDecimal.ZERO;

        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            BigDecimal income = incomeByDate.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal expense = expenseByDate.getOrDefault(date, BigDecimal.ZERO);

            runningBalance = runningBalance.add(income).subtract(expense);

            evolution.add(new EvolutionDataPointDTO(date, income, expense, runningBalance));
        }

        return evolution;
    }
}