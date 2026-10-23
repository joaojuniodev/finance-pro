package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.dto.request.firstSteps.FirstStepsRequestDTO;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.account.dto.response.firstSteps.ResponseTest;
import br.com.financepro.financePro.common.json.QuestionJson;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.flags.FinancialMomentsFlag;
import br.com.financepro.financePro.financialProfile.repository.FinancialProfileRepository;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.com.financepro.financePro.financialProfile.flags.CurrentAreaOfFocusFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.KeyOpportunityFlag.*;

@Service
public class BuildFinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(BuildFinancialProfileService.class.getName());

    // Momento financeiro:
    // Segurança financeira - Resiliência financeira - respostas críticas relacinadas a contas, crédito e imprevistos

    // Identificar as atenções críticas

    // Tendência financeira:
    // Organização e controle - Regulação de impulsos - Orientação para o futuro - Equilíbrio e liberdade - respostas críticas comportamentais

    // Descobrir a força principal

    // Descobrir a principal oportunidade

    // Calcular a confiança (confiança do resultado do perfil financeiro)

    // Construir a síntese interpretativa

    @Autowired
    private FinancialProfileRepository repository;

    @Autowired
    private UserRepository userRepository;

    private final String[] responsesCriticalOfFinancialMoment = new String[]
        {"fp-fc-01", "fp-fc-02", "fp-fc-03", "fp-fr-04", "fp-fr-05", "fp-foc-09", "fp-fbf-13", "fp-fbf-16", "fp-fbf-17"};

    public ResponseTest execute(FinancialDimensionsDTO dimensions, FirstStepsRequestDTO firstSteps, User user, RootFinancialData root) {
        log.info("Building Financial Profile this User");

        var moment = analyzeFinancialMoment(dimensions.financialSecurity(), dimensions.financialResilience(), firstSteps);
        var attentionAnalysis = analyzeCurrentAreaOfFocus(firstSteps, root);
        var keyOpportunity = analyzeKeyOpportunity(dimensions, firstSteps, root);

        System.out.println(moment);

        return new ResponseTest(moment, attentionAnalysis.all(), attentionAnalysis.main(), keyOpportunity);
    }

    private AttentionAnalysis analyzeCurrentAreaOfFocus(FirstStepsRequestDTO firstSteps, RootFinancialData root) {
        Map<String, AttentionCandidate> candidates = new LinkedHashMap<>();

        for (Question question : firstSteps.questionsFinancialSecurity()) {
            Integer score = findScore(question, root.financialData().financialSecurity());

            if ("fp-fc-02".equals(question.questionId())) {
                if (Integer.valueOf(1).equals(score)) {
                    addAttention(candidates, OVERDUE_BILLS, OVERDUE_BILLS_DESC, score, 5);
                } else if (Integer.valueOf(0).equals(score)) {
                    addAttention(candidates, UNPAID_IMPORTANT_OBLIGATION, UNPAID_IMPORTANT_OBLIGATION_DESC, score, 2);
                }
            } else if ("fp-fc-03".equals(question.questionId()) && Integer.valueOf(0).equals(score)) {
                addAttention(candidates, RECURRING_RELIANCE_ON_CREDIT, RECURRING_RELIANCE_ON_CREDIT_DESC, score, 3);
            }
        }

        for (Question question : firstSteps.questionsFinancialResilience()) {
            Integer score = findScore(question, root.financialData().financialResilience());

            if ("fp-fr-04".equals(question.questionId()) && isCriticalScore(score)) {
                addAttention(candidates, LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS, LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS_DESC, score, 8);
                if (Integer.valueOf(1).equals(score)) {
                    addAttention(candidates, POSTPONING_BILL_TO_UNFORESEEN_EVENTS, POSTPONING_BILL_TO_UNFORESEEN_EVENTS_DESC, score, 6);
                }
            } else if ("fp-fr-05".equals(question.questionId()) && isCriticalScore(score)) {
                addAttention(candidates, LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS, LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS_DESC, score, 8);
            }
        }

        for (Question question : firstSteps.questionsFinancialOrganizationAndControl()) {
            Integer score = findScore(question, root.financialData().financialOrganizationAndControl());
            if ("fp-foc-09".equals(question.questionId()) && isCriticalScore(score)) {
                addAttention(candidates, DIFFICULTY_KEEPING_UP, DIFFICULTY_KEEPING_UP_DESC, score, 9);
            }
        }

        for (Question question : firstSteps.questionsFinancialBalanceAndFreedom()) {
            Integer score = findScore(question, root.financialData().financialBalanceAndFreedom());

            if ("fp-fbf-13".equals(question.questionId()) && isCriticalScore(score)) {
                addAttention(candidates, LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS, LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS_DESC, score, 7);
            } else if ("fp-fbf-16".equals(question.questionId()) && isCriticalScore(score)) {
                addAttention(candidates, ESSENTIALS_NEEDS_SACRIFICED, ESSENTIALS_NEEDS_SACRIFICED_DESC, score, 1);
            } else if ("fp-fbf-17".equals(question.questionId()) && Integer.valueOf(0).equals(score)) {
                addAttention(candidates, IMPULSES_AFFECTING_COMMITMENTS, IMPULSES_AFFECTING_COMMITMENTS_DESC, score, 4);
            }
        }

        if (candidates.isEmpty()) {
            Map<String, String> noAttention = singleResult(NO_CURRENT_CRITICAL_ATTENTION, NO_CURRENT_CRITICAL_ATTENTION_DESC);
            return new AttentionAnalysis(noAttention, noAttention);
        }

        Map<String, String> all = new LinkedHashMap<>();
        candidates.values().forEach(candidate -> all.put(candidate.title(), candidate.description()));

        AttentionCandidate main = candidates.values().stream()
            .min(Comparator.comparingInt(AttentionCandidate::score)
                .thenComparingInt(AttentionCandidate::priority))
            .orElseThrow();

        return new AttentionAnalysis(all, singleResult(main.title(), main.description()));
    }

    private Map<String, String> analyzeKeyOpportunity(
        FinancialDimensionsDTO dimensions,
        FirstStepsRequestDTO firstSteps,
        RootFinancialData root
    ) {
        List<OpportunityCandidate> candidates = new ArrayList<>();

        addOpportunityCandidates(candidates, firstSteps.questionsFinancialSecurity(), root.financialData().financialSecurity(),
            dimensions.financialSecurity(), IMPROVE_FINANCIAL_SECURITY, IMPROVE_FINANCIAL_SECURITY_DESC);
        addOpportunityCandidates(candidates, firstSteps.questionsFinancialResilience(), root.financialData().financialResilience(),
            dimensions.financialResilience(), BUILD_FINANCIAL_RESILIENCE, BUILD_FINANCIAL_RESILIENCE_DESC);
        addOpportunityCandidates(candidates, firstSteps.questionsFinancialOrganizationAndControl(), root.financialData().financialOrganizationAndControl(),
            dimensions.financialOrganizationAndControl(), IMPROVE_ORGANIZATION_AND_CONTROL, IMPROVE_ORGANIZATION_AND_CONTROL_DESC);
        addOpportunityCandidates(candidates, firstSteps.questionsFinancialImpulseRegulation(), root.financialData().financialImpulseRegulation(),
            dimensions.financialImpulseRegulation(), STRENGTHEN_IMPULSE_REGULATION, STRENGTHEN_IMPULSE_REGULATION_DESC);
        addOpportunityCandidates(candidates, firstSteps.questionsFinancialOrientationTheFuture(), root.financialData().financialOrientationTheFuture(),
            dimensions.financialOrientationTheFuture(), STRENGTHEN_ORIENTATION_THE_FUTURE, STRENGTHEN_ORIENTATION_THE_FUTURE_DESC);
        addOpportunityCandidates(candidates, firstSteps.questionsFinancialBalanceAndFreedom(), root.financialData().financialBalanceAndFreedom(),
            dimensions.financialBalanceAndFreedom(), IMPROVE_BALANCE_AND_FREEDOM, IMPROVE_BALANCE_AND_FREEDOM_DESC);

        return candidates.stream()
            .min(Comparator.comparingDouble(OpportunityCandidate::dimensionScore)
                .thenComparingInt(OpportunityCandidate::questionOrder))
            .map(candidate -> singleResult(candidate.title(), candidate.description()))
            .orElseGet(LinkedHashMap::new);
    }

    private void addOpportunityCandidates(
        List<OpportunityCandidate> candidates,
        List<Question> answers,
        List<QuestionJson> questions,
        double dimensionScore,
        String title,
        String description
    ) {
        for (Question answer : answers) {
            Integer score = findScore(answer, questions);
            if (Integer.valueOf(2).equals(score)) {
                candidates.add(new OpportunityCandidate(title, description, dimensionScore, questionOrder(answer.questionId())));
            }
        }
    }

    private Integer findScore(Question answer, List<QuestionJson> questions) {
        if (answer == null || answer.questionId() == null || answer.response() == null) return null;

        return questions.stream()
            .filter(question -> answer.questionId().equals(question.id()))
            .findFirst()
            .map(question -> question.options().get(answer.response()))
            .orElse(null);
    }

    private boolean isCriticalScore(Integer score) {
        return score != null && score <= 1;
    }

    private void addAttention(
        Map<String, AttentionCandidate> candidates,
        String title,
        String description,
        int score,
        int priority
    ) {
        AttentionCandidate candidate = new AttentionCandidate(title, description, score, priority);
        candidates.merge(title, candidate, (current, incoming) ->
            incoming.score() < current.score() ? incoming : current);
    }

    private Map<String, String> singleResult(String title, String description) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(title, description);
        return result;
    }

    private int questionOrder(String questionId) {
        if (questionId == null) return Integer.MAX_VALUE;

        try {
            return Integer.parseInt(questionId.substring(questionId.lastIndexOf('-') + 1));
        } catch (NumberFormatException exception) {
            return Integer.MAX_VALUE;
        }
    }

    private record AttentionCandidate(String title, String description, int score, int priority) {}

    private record AttentionAnalysis(Map<String, String> all, Map<String, String> main) {}

    private record OpportunityCandidate(
        String title,
        String description,
        double dimensionScore,
        int questionOrder
    ) {}

    private String analyzeFinancialMoment(double security, double resilience, FirstStepsRequestDTO firstSteps) {

        String moment = "";

        if (security >= 0 && security <= 24) moment = FinancialMomentsFlag.UNDER_PRESSURE;
        else if ((security >= 25 && security <= 44) && resilience <= 24) moment = FinancialMomentsFlag.UNDER_PRESSURE;
        else if ((security >= 25 && security <= 44) && resilience >= 25) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        else if ((security >= 45 && security <= 59) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        else if ((security >= 45 && security <= 59) && resilience >= 45) moment = FinancialMomentsFlag.STABLE;
        else if ((security >= 60 && security <= 74) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        else if ((security >= 60 && security <= 74) && (resilience >= 45 && resilience <= 59)) moment = FinancialMomentsFlag.STABLE;
        else if ((security >= 60 && security <= 74) && resilience >= 60) moment = FinancialMomentsFlag.COMFORTABLE;
        else if ((security >= 75 && security <= 100) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        else if ((security >= 75 && security <= 100) && (resilience >= 45 && resilience <= 59)) moment = FinancialMomentsFlag.STABLE;
        else if ((security >= 75 && security <= 100) && (resilience >= 60 && resilience <= 74)) moment = FinancialMomentsFlag.COMFORTABLE;
        else if ((security >= 75 && security <= 100) && (resilience >= 75 && resilience <= 100)) moment = FinancialMomentsFlag.RESILIENT;

        int under_pressure = 0;
        int under_reorganization = 0;
        int stable = 0;
        int comfortable = 0;

        final String spent_more = "Gastaram bem mais";
        final String several_were_delayed = "Várias atrasaram";
        final String not_paid_bill = "Precisei deixar alguma conta sem pagar";
        final String practically_zero = "Fico praticamente no zero";
        final String almost_there = "Falta um pouco";
        final String i_need_credit = "Preciso recorrer a crédito, empréstimo ou atraso de contas";

        for (Question question : firstSteps.questionsFinancialSecurity()) {
            if (question.questionId().equals("fp-fc-01")) {
                if (question.response().equals(spent_more)) under_reorganization++;
            }
            if (question.questionId().equals("fp-fc-02")) {
                if (question.response().equals(several_were_delayed)) under_reorganization++;
                if (question.response().equals(not_paid_bill)) under_pressure++;
            }
            if (question.questionId().equals("fp-fc-03")) {
                if (question.response().equals(practically_zero)) stable++;
                if (question.response().equals(almost_there)) under_reorganization++;
                if (question.response().equals(i_need_credit)) under_pressure++;
            }
        }

        final String pay_but_reorganize_the_month = "Conseguiria pagar reorganizando o mês";
        final String processes_installments_and_maintains_accounts = "Parcelaria, mas manteria as outras contas";
        final String defer_bill_or_loan_repayment = "Precisaria adiar uma conta ou pedir dinheiro emprestado";
        final String not_what_do = "Eu não saberia o que fazer";
        final String from_one_to_three_months = "De um a três meses";
        final String a_few_weeks = "Algumas semanas";
        final String less_than_a_week = "Menos de uma semana";

        for (Question question : firstSteps.questionsFinancialResilience()) {
            if (question.questionId().equals("fp-fr-04")) {
                if (question.response().equals(pay_but_reorganize_the_month)) comfortable++;
                if (question.response().equals(processes_installments_and_maintains_accounts)) stable++;
                if (question.response().equals(defer_bill_or_loan_repayment)) under_reorganization++;
                if (question.response().equals(not_what_do)) under_reorganization++;
            }
            if (question.questionId().equals("fp-fr-05")) {
                if (question.response().equals(from_one_to_three_months)) comfortable++;
                if (question.response().equals(a_few_weeks)) stable++;
                if (question.response().equals(less_than_a_week)) under_reorganization++;
            }
        }

        // Do pior para o melhor:
        // 1. Sob pressão
        // 2. No máximo em reorganização
        // 3. No máximo estável
        // 4. No máximo confortável
        // 5. Resiliente

        if (under_pressure > 0) {
            if (moment.equals(FinancialMomentsFlag.UNDER_REORGANIZATION) || moment.equals(FinancialMomentsFlag.STABLE) ||
                moment.equals(FinancialMomentsFlag.COMFORTABLE) || moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.UNDER_PRESSURE;
            }
        }
        else if (under_reorganization > 0) {
            if (moment.equals(FinancialMomentsFlag.STABLE) || moment.equals(FinancialMomentsFlag.COMFORTABLE) ||
                moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
            }
        }
        else if (stable > 0) {
            if (moment.equals(FinancialMomentsFlag.COMFORTABLE) || moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.STABLE;
            }

        }
        else if (comfortable > 0) {
            if (moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.COMFORTABLE;
            }
        }

        return moment;
    }
}
