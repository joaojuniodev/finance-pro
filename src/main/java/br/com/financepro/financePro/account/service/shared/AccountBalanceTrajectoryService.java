package br.com.financepro.financePro.account.service.shared;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.transaction.dto.projection.DailyNetProjection;
import br.com.financepro.financePro.transaction.dto.response.BalanceTrajectoryPointDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountBalanceTrajectoryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<BalanceTrajectoryPointDTO> getMonthlyBalanceTrajectory(UUID accountId, YearMonth month) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        BigDecimal currentBalance = account.getCurrentBalance();

        LocalDate startOfMonth = month.atDay(1);
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = month.equals(YearMonth.from(today)) ? today : month.atEndOfMonth();

        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = startOfMonth.plusMonths(1).atStartOfDay();

        List<DailyNetProjection> dailyNets = transactionRepository.findDailyNet(
            accountId, startDateTime, endDateTime
        );

        Map<LocalDate, BigDecimal> netByDate = dailyNets.stream()
            .collect(Collectors.toMap(DailyNetProjection::getDate, DailyNetProjection::getNetAmount));

        BigDecimal netFromEndOfMonthToToday = BigDecimal.ZERO;

        BigDecimal totalNetOfMonth = dailyNets.stream()
            .map(DailyNetProjection::getNetAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal runningBalance = currentBalance.subtract(totalNetOfMonth);

        List<BalanceTrajectoryPointDTO> trajectory = new ArrayList<>();

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            BigDecimal net = netByDate.getOrDefault(date, BigDecimal.ZERO);
            runningBalance = runningBalance.add(net);
            trajectory.add(new BalanceTrajectoryPointDTO(date, runningBalance));
        }

        return trajectory;
    }
}