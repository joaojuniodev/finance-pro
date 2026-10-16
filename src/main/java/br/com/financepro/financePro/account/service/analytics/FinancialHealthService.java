package br.com.financepro.financePro.account.service.analytics;

import br.com.financepro.financePro.account.dto.response.financialHealth.FeedbackFinancialHealth;
import br.com.financepro.financePro.account.dto.response.financialHealth.FinancialHealthResponse;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.common.enums.*;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class FinancialHealthService {
    // Score de Saúde Financeira

    // Regra básica: 50-30-20 = 50% para necessidades, 30% para desejos, e 20% para poupança
    // ->> se o usuário consegue seguir essa regra, já ganha um percentual a mais. (O sistema deixa essa regra a mostra com explicações de como funciona)

    // Saúde financeira também inclui saber para onde o dinheiro está indo (despesas por categoria).

    // 35 1. Equilíbrio entre receitas e despesas: gasta menos do que ganha
    // 25 2. Controle de endividamento: até 18 pontos por gastos essenciais e 7 por assinaturas.
    // 20 3. Lazer e compras: 20, 15 ou 10 pontos abaixo de 10%, 15% ou 20% da renda.
    // 15 4. Reserva de emergência: 15 pontos se possui, 3 se não possui.
    // 5 5. Cofrinhos e metas: 3 pontos se possui um, 5 se possui mais de um.

    // 6. Está seguindo a educação financeira proposta pelo app (Ativada automáticamente quando o usuário se inicia na educação financeira).

    private static final Logger log = LoggerFactory.getLogger(FinancialHealthService.class.getName());

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RecurrenceRepository recurrenceRepository;

    final String rentCategoryName = "Moradia";
    final String waterCategoryName = "Água";
    final String lightCategoryName = "Energia";
    final String healthCategoryName = "Saúde";
    final String transportCategoryName = "Transporte";
    final String signatureCategoryName = "Assinaturas";
    final String leisureCategoryName = "Lazer";
    final String shoppingCategoryName = "Compras";

    final String positive = "positive";
    final String neutral = "neutral";
    final String alert = "alert";

    public FinancialHealthService(
        AccountRepository accountRepository,
        TransactionRepository transactionRepository,
        RecurrenceRepository recurrenceRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.recurrenceRepository = recurrenceRepository;
    }

    @Transactional(readOnly = true)
    public FinancialHealthResponse buildFinancialHealth(UUID accountId) {
        log.info("Building Financial Life");

        var account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("Not found Account by id: " + accountId));

        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.plusMonths(1).atDay(1);

        var transactions = transactionRepository.findBetweenStartAndEndOfPeriod(
            accountId,
            firstDay.atStartOfDay(),
            endDate.atStartOfDay()
        );
        var recurrences = recurrenceRepository.findOccurrencesWithPeriod(accountId, endDate).stream()
            .filter(rec -> rec.getType() == RecurrenceType.DEBIT)
            .filter(rec -> rec.getNextExecutionDate() != null)
            .filter(rec -> !rec.getNextExecutionDate().isBefore(firstDay))
            .filter(rec -> !isRecurrenceAlreadyRegistered(rec, transactions))
            .toList();

        var income = transactions.stream()
            .filter(tx -> tx.getType() == TransactionType.CREDIT && tx.getStatus() == TransactionStatus.COMPLETED)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        var expense = transactions.stream()
            .filter(tx -> tx.getType() == TransactionType.DEBIT)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 50%:
        BigDecimal essentialExpenses = sumExpensesByCategories(
            transactions, recurrences,
            rentCategoryName, waterCategoryName, lightCategoryName,
            healthCategoryName, transportCategoryName
        );

        // 30%:
        BigDecimal signaturesExpenses = sumExpensesByCategories(
            transactions, recurrences, signatureCategoryName
        );

        // 20%: mercado e supermercado não fazem parte de lazer e compras.
        BigDecimal leisureAndShoppingExpenses = sumExpensesByCategories(
            transactions, recurrences,
            leisureCategoryName, shoppingCategoryName
        );

        int pointsEquilibriumIncomeAndExpenses = 0;
        int pointsControlIndebtedness = 0;
        int pointsLeisureAndShopping = 0;

        if (income.compareTo(BigDecimal.ZERO) > 0) {
            pointsEquilibriumIncomeAndExpenses = calculateTheEquilibrium(income, expense);
            pointsControlIndebtedness = calculateDebtControl(income, essentialExpenses, signaturesExpenses);
            pointsLeisureAndShopping = calculateLeisureAndShopping(income, leisureAndShoppingExpenses);
        }

        int pointsEmergencyReserve = calculateEmergencyReserve(account.getWallets());
        int pointsSavingsGoals = calculateSavingsGoals(account.getWallets());
        int totalPoints = pointsEquilibriumIncomeAndExpenses + pointsControlIndebtedness
            + pointsLeisureAndShopping + pointsEmergencyReserve + pointsSavingsGoals;

        List<FeedbackFinancialHealth> identifiedStrengths = new ArrayList<>();
        List<FeedbackFinancialHealth> optimizationPointsIdentified = new ArrayList<>();

        createsStrengthsAndImprovements(
            identifiedStrengths, optimizationPointsIdentified,
            pointsEquilibriumIncomeAndExpenses,
            pointsControlIndebtedness,
            pointsLeisureAndShopping,
            pointsEmergencyReserve,
            pointsSavingsGoals
        );

        return new FinancialHealthResponse(
            totalPoints,
            getStatus(totalPoints),
            identifiedStrengths,
            optimizationPointsIdentified
        );
    }

    private FinancialHealthStatus getStatus(int totalPoints) {
        if (totalPoints > 90) return FinancialHealthStatus.EXCELLENT;
        else if (totalPoints > 70) return FinancialHealthStatus.VERY_GOOD;
        else if (totalPoints > 50) return FinancialHealthStatus.GOOD;
        else if (totalPoints > 40) return FinancialHealthStatus.FAIR;
        else return FinancialHealthStatus.CRITICAL;
    }

    private void createsStrengthsAndImprovements(
        List<FeedbackFinancialHealth> identifiedStrengths,
        List<FeedbackFinancialHealth> optimizationPointsIdentified,
        int pointsEquilibriumIncomeAndExpenses,
        int pointsControlIndebtedness,
        int pointsLeisureAndShopping,
        int pointsEmergencyReserve,
        int pointsSavingsGoals
    ) {
        if (pointsEquilibriumIncomeAndExpenses == 35) {
            identifiedStrengths.add(new FeedbackFinancialHealth("Receitas e despesas equilibradas.", positive));
        } else {
            optimizationPointsIdentified.add(new FeedbackFinancialHealth("Melhorar o equilíbrio entre receitas e despesas.", "alert"));
        }

        if (pointsControlIndebtedness == 25) {
            identifiedStrengths.add(new FeedbackFinancialHealth("Gastos essenciais e assinaturas sob controle.", positive));
        } else {
            optimizationPointsIdentified.add(new FeedbackFinancialHealth("Revisar os gastos essenciais e as assinaturas em relação à renda.", alert));
        }

        if (pointsLeisureAndShopping == 20) {
            identifiedStrengths.add(new FeedbackFinancialHealth("Gastos com lazer e compras abaixo de 10% da renda.", positive));
        } else {
            optimizationPointsIdentified.add(new FeedbackFinancialHealth("Revisar os gastos com lazer e compras em relação à renda.", alert));
        }

        if (pointsEmergencyReserve == 15) {
            identifiedStrengths.add(new FeedbackFinancialHealth("Possui uma reserva de emergência.", positive));
        } else {
            optimizationPointsIdentified.add(new FeedbackFinancialHealth("Criar uma reserva de emergência.", alert));
        }

        if (pointsSavingsGoals > 0) {
            identifiedStrengths.add(new FeedbackFinancialHealth("Possui cofrinhos ou metas.", positive));
        } else {
            optimizationPointsIdentified.add(new FeedbackFinancialHealth("Criar um cofrinho ou uma meta.", alert));
        }
    }

    private int calculateTheEquilibrium(BigDecimal income, BigDecimal expense) {
        log.info("Calculating the balance between revenues and expenses");

        var dif = income.subtract(expense).intValue();

        if (dif > 800) return 35;
        else if (dif > 500) return 30;
        else if (dif > 300) return 25;
        else if (dif > 0) return 15;
        else return 0;
    }

    private int calculateDebtControl(
        BigDecimal income,
        BigDecimal essentialExpenses,
        BigDecimal signaturesExpenses
    ) {
        int points = 0;

        if (isBelowPercentage(essentialExpenses, income, 30)) points += 18;
        else if (isBelowPercentage(essentialExpenses, income, 50)) points += 10;

        if (isBelowPercentage(signaturesExpenses, income, 20)) points += 7;
        else if (isBelowPercentage(signaturesExpenses, income, 30)) points += 5;

        return points;
    }

    private int calculateLeisureAndShopping(BigDecimal income, BigDecimal expenses) {
        if (isBelowPercentage(expenses, income, 10)) return 20;
        else if (isBelowPercentage(expenses, income, 15)) return 15;
        else if (isBelowPercentage(expenses, income, 20)) return 10;
        else return 0;
    }

    private boolean isBelowPercentage(BigDecimal expenses, BigDecimal income, int percentage) {
        // Compara os percentuais sem arredondar valores próximos aos limites.
        return expenses.multiply(BigDecimal.valueOf(100))
            .compareTo(income.multiply(BigDecimal.valueOf(percentage))) < 0;
    }

    private int calculateEmergencyReserve(Collection<Wallet> wallets) {
        boolean hasReserve = wallets.stream()
            .anyMatch(wallet -> wallet.getType() == WalletType.RESERVE);

        return hasReserve ? 15 : 3;
    }

    private int calculateSavingsGoals(Collection<Wallet> wallets) {
        long savingsGoals = wallets.stream()
            .filter(wallet -> wallet.getType() == WalletType.SAVING || wallet.getGoal() != null)
            .count();

        if (savingsGoals > 1) return 5;
        else if (savingsGoals == 1) return 3;
        else return 0;
    }

    private BigDecimal sumExpensesByCategories(
        List<Transaction> transactions,
        List<Recurrence> recurrences,
        String... categoryNames
    ) {
        BigDecimal transactionExpenses = transactions.stream()
            .filter(tx -> tx.getType() == TransactionType.DEBIT)
            .filter(tx -> matchesCategory(tx.getCategory(), categoryNames))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal recurrenceExpenses = recurrences.stream()
            .filter(rec -> matchesCategory(rec.getCategory(), categoryNames))
            .map(Recurrence::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return transactionExpenses.add(recurrenceExpenses);
    }

    private boolean matchesCategory(Category category, String... categoryNames) {
        return category != null && Arrays.asList(categoryNames).contains(category.getName());
    }

    private boolean isRecurrenceAlreadyRegistered(Recurrence recurrence, List<Transaction> transactions) {
        return transactions.stream().anyMatch(tx -> tx.getType() == TransactionType.DEBIT
            && tx.getRecurrence() != null
            && recurrence.getId().equals(tx.getRecurrence().getId())
            && tx.getRegisteredAt() != null
            && tx.getRegisteredAt().toLocalDate().equals(recurrence.getNextExecutionDate()));
    }
}
