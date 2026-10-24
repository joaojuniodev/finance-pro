package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.dto.request.firstSteps.AnswersOfQuizFinancialProfile;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.json.QuestionJson;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileResponse;
import br.com.financepro.financePro.financialProfile.flags.FinancialMomentsFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.com.financepro.financePro.financialProfile.flags.ConfidenceInTheResultFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.CurrentAreaOfFocusFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.FinancialTrendFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.InterpretiveSynthesisFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.KeyOpportunityFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.MainStrengthFlag.*;

@Service
public class BuildFinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(BuildFinancialProfileService.class.getName());

    public FinancialProfileResponse execute(FinancialDimensionsDTO dimensions, AnswersOfQuizFinancialProfile firstSteps, RootFinancialData root) {
        log.info("Building Financial Profile this User");

        var trend = analyzeFinancialTrend(dimensions, firstSteps, root);
        var moment = analyzeFinancialMoment(dimensions.financialSecurity(), dimensions.financialResilience(), firstSteps);
        var attentionAnalysis = analyzeCurrentAreaOfFocus(firstSteps, root);
        var mainStrength = analyzeMainStrength(dimensions, firstSteps, root);
        var keyOpportunity = analyzeKeyOpportunity(dimensions, firstSteps, root);
        var confidence = analyzeConfidence(firstSteps, root);
        var interpretiveSynthesis = buildInterpretiveSynthesis(
            trend,
            moment,
            mainStrength,
            keyOpportunity,
            attentionAnalysis.main(),
            confidence.result()
        );

        return new FinancialProfileResponse(
            trend,
            moment,
            attentionAnalysis.all(),
            attentionAnalysis.main(),
            mainStrength,
            keyOpportunity,
            confidence.score(),
            confidence.result(),
            interpretiveSynthesis
        );
    }

    private String buildInterpretiveSynthesis(
        Map<String, String> trend,
        Map<String, String> moment,
        Map<String, String> mainStrength,
        Map<String, String> keyOpportunity,
        Map<String, String> mainAttention,
        Map<String, String> confidence
    ) {
        String confidenceLevel = firstKey(confidence);
        if (INCONCLUSIVE.equals(confidenceLevel)) return INCONCLUSIVE_SYNTHESIS;

        List<String> parts = new ArrayList<>();
        addSynthesisPart(parts, CONFIDENCE_INTRODUCTIONS, confidenceLevel);
        addSynthesisPart(parts, TREND_SYNTHESIS, firstKey(trend));
        addSynthesisPart(parts, MOMENT_SYNTHESIS, firstKey(moment));
        addSynthesisPart(parts, STRENGTH_SYNTHESIS, firstKey(mainStrength));

        String attention = firstKey(mainAttention);
        if (NO_CURRENT_CRITICAL_ATTENTION.equals(attention)) {
            parts.add(NO_CRITICAL_ATTENTION_SYNTHESIS);
        } else {
            addSynthesisPart(parts, ATTENTION_SYNTHESIS, attention);
        }

        addSynthesisPart(parts, OPPORTUNITY_SYNTHESIS, firstKey(keyOpportunity));
        return String.join(" ", parts);
    }

    private void addSynthesisPart(List<String> parts, Map<String, String> predefinedTexts, String key) {
        if (key == null) return;

        String text = predefinedTexts.get(key);
        if (text != null) parts.add(text);
    }

    private String firstKey(Map<String, String> result) {
        return result.keySet().stream().findFirst().orElse(null);
    }

    private ConfidenceAnalysis analyzeConfidence(AnswersOfQuizFinancialProfile firstSteps, RootFinancialData root) {
        Map<String, Integer> scores = new LinkedHashMap<>();

        addScores(scores, firstSteps.questionsFinancialSecurity(), root.financialData().financialSecurity());
        addScores(scores, firstSteps.questionsFinancialResilience(), root.financialData().financialResilience());
        addScores(scores, firstSteps.questionsFinancialOrganizationAndControl(), root.financialData().financialOrganizationAndControl());
        addScores(scores, firstSteps.questionsFinancialImpulseRegulation(), root.financialData().financialImpulseRegulation());
        addScores(scores, firstSteps.questionsFinancialBalanceAndFreedom(), root.financialData().financialBalanceAndFreedom());
        addScores(scores, firstSteps.questionsFinancialOrientationTheFuture(), root.financialData().financialOrientationTheFuture());

        int unansweredQuestions = (int) scores.values().stream().filter(score -> score == null).count();
        int contradictions = countContradictions(scores, firstSteps);
        int confidenceScore = Math.max(0, 100 - (unansweredQuestions * 5) - (contradictions * 15));

        boolean hasDimensionWithoutValidAnswers =
            hasNoValidAnswers(scores, "fp-fc-01", "fp-fc-02", "fp-fc-03")
                || hasNoValidAnswers(scores, "fp-fr-04", "fp-fr-05")
                || hasNoValidAnswers(scores, "fp-foc-06", "fp-foc-07", "fp-foc-08", "fp-foc-09")
                || hasNoValidAnswers(scores, "fp-fir-10", "fp-fir-11", "fp-fir-12")
                || hasNoValidAnswers(scores, "fp-fof-14", "fp-fof-15")
                || hasNoValidAnswers(scores, "fp-fbf-13", "fp-fbf-16", "fp-fbf-17");

        if (unansweredQuestions > 4 || hasDimensionWithoutValidAnswers) {
            return new ConfidenceAnalysis(
                Math.min(confidenceScore, 49),
                singleResult(INCONCLUSIVE, INCONCLUSIVE_DESC)
            );
        }
        if (confidenceScore >= 90) {
            return new ConfidenceAnalysis(confidenceScore, singleResult(HIGH, HIGH_DESC));
        }
        if (confidenceScore >= 70) {
            return new ConfidenceAnalysis(confidenceScore, singleResult(MODERATE, MODERATE_DESC));
        }
        return new ConfidenceAnalysis(confidenceScore, singleResult(LOW, LOW_DESC));
    }

    private void addScores(Map<String, Integer> scores, List<Question> answers, List<QuestionJson> questions) {
        for (QuestionJson question : questions) {
            scores.put(question.id(), findScore(question.id(), answers, questions));
        }
    }

    private int countContradictions(Map<String, Integer> scores, AnswersOfQuizFinancialProfile firstSteps) {
        int contradictions = 0;

        if ((isProtectiveScore(scores.get("fp-fc-01")) && isCriticalScore(scores.get("fp-fc-03")))
            || (isCriticalScore(scores.get("fp-fc-01")) && isProtectiveScore(scores.get("fp-fc-03")))) {
            contradictions++;
        }
        if (isCriticalScore(scores.get("fp-fr-04")) && isProtectiveScore(scores.get("fp-fr-05"))) {
            contradictions++;
        }
        if ((isProtectiveScore(scores.get("fp-foc-06")) || isProtectiveScore(scores.get("fp-foc-07")))
            && isCriticalScore(scores.get("fp-foc-09"))) {
            contradictions++;
        }
        if (isProtectiveScore(scores.get("fp-foc-08")) && Integer.valueOf(0).equals(scores.get("fp-fof-15"))) {
            contradictions++;
        }
        if (Integer.valueOf(4).equals(scores.get("fp-fir-10")) && isCriticalScore(scores.get("fp-fir-11"))) {
            contradictions++;
        }
        if (Integer.valueOf(4).equals(scores.get("fp-fir-10")) && isCriticalScore(scores.get("fp-fir-12"))) {
            contradictions++;
        }

        String futurePriorityResponse = findResponse("fp-fof-15", firstSteps.questionsFinancialOrientationTheFuture());
        boolean prioritizesFuture = "Abro mão de algumas coisas agora para ter mais segurança no futuro".equals(futurePriorityResponse);
        boolean enjoysPresentWithCoveredBills = "Aproveito o presente, desde que minhas contas estejam cobertas".equals(futurePriorityResponse);

        if ((Integer.valueOf(4).equals(scores.get("fp-fof-14")) && Integer.valueOf(0).equals(scores.get("fp-fof-15")))
            || (Integer.valueOf(1).equals(scores.get("fp-fof-14")) && prioritizesFuture)) {
            contradictions++;
        }

        boolean declaresBalance = Integer.valueOf(4).equals(scores.get("fp-fof-15"))
            || enjoysPresentWithCoveredBills
            || Integer.valueOf(4).equals(scores.get("fp-fbf-17"));
        boolean hasCriticalBalanceImpact = isCriticalScore(scores.get("fp-fbf-13"))
            || isCriticalScore(scores.get("fp-fbf-16"));
        if (declaresBalance && hasCriticalBalanceImpact) {
            contradictions++;
        }

        if (Integer.valueOf(2).equals(scores.get("fp-fbf-17"))
            && (Integer.valueOf(4).equals(scores.get("fp-fof-15"))
                || Integer.valueOf(0).equals(scores.get("fp-fof-15")))) {
            contradictions++;
        }

        return contradictions;
    }

    private boolean isProtectiveScore(Integer score) {
        return score != null && score >= 3;
    }

    private boolean hasNoValidAnswers(Map<String, Integer> scores, String... questionIds) {
        for (String questionId : questionIds) {
            if (scores.get(questionId) != null) return false;
        }
        return true;
    }

    private String findResponse(String questionId, List<Question> answers) {
        return answers.stream()
            .filter(answer -> questionId.equals(answer.questionId()))
            .map(Question::response)
            .findFirst()
            .orElse(null);
    }

    private Map<String, String> analyzeFinancialTrend(
        FinancialDimensionsDTO dimensions,
        AnswersOfQuizFinancialProfile firstSteps,
        RootFinancialData root
    ) {
        double organization = dimensions.financialOrganizationAndControl();
        double impulseRegulation = dimensions.financialImpulseRegulation();
        double futureOrientation = dimensions.financialOrientationTheFuture();
        double balance = dimensions.financialBalanceAndFreedom();

        Integer leisureWithPendingBills = findScore(
            "fp-fbf-13",
            firstSteps.questionsFinancialBalanceAndFreedom(),
            root.financialData().financialBalanceAndFreedom()
        );
        Integer essentialsSacrificed = findScore(
            "fp-fbf-16",
            firstSteps.questionsFinancialBalanceAndFreedom(),
            root.financialData().financialBalanceAndFreedom()
        );
        Integer currentLifeSituation = findScore(
            "fp-fbf-17",
            firstSteps.questionsFinancialBalanceAndFreedom(),
            root.financialData().financialBalanceAndFreedom()
        );

        boolean hasCriticalBehavioralImpact = isCriticalScore(leisureWithPendingBills)
            || isCriticalScore(essentialsSacrificed)
            || Integer.valueOf(0).equals(currentLifeSituation);
        boolean hasExcessiveRestriction = Integer.valueOf(2).equals(currentLifeSituation);

        if (hasCriticalBehavioralImpact) {
            return singleResult(IMPULSE_AT_THE_CONTROLS, IMPULSE_AT_THE_CONTROLS_DESC);
        }
        if (organization >= 60 && futureOrientation >= 60 && hasExcessiveRestriction) {
            return singleResult(GUARDIAN_CAUTIOUS, GUARDIAN_CAUTIOUS_DESC);
        }
        if (organization >= 60 && impulseRegulation >= 60 && balance >= 60) {
            return singleResult(PLANNER_BALANCED, PLANNER_BALANCED_DESC);
        }
        if (futureOrientation >= 60 && organization < 60) {
            return singleResult(PLANNER_UNDER_CONSTRUCTION, PLANNER_UNDER_CONSTRUCTION_DESC);
        }
        if (balance >= 60 && impulseRegulation >= 45) {
            return singleResult(FLEXIBLE_CONSCIOUSLY, FLEXIBLE_CONSCIOUSLY_DESC);
        }

        return singleResult(SPONTANEOUS, SPONTANEOUS_DESC);
    }

    private Map<String, String> analyzeMainStrength(
        FinancialDimensionsDTO dimensions,
        AnswersOfQuizFinancialProfile firstSteps,
        RootFinancialData root
    ) {
        List<StrengthCandidate> candidates = new ArrayList<>();

        addStrengthCandidate(candidates, firstSteps.questionsFinancialSecurity(), root.financialData().financialSecurity(),
            dimensions.financialSecurity(), FINANCIAL_SECURITY, FINANCIAL_SECURITY_DESC, 1);
        addStrengthCandidate(candidates, firstSteps.questionsFinancialResilience(), root.financialData().financialResilience(),
            dimensions.financialResilience(), FINANCIAL_RESILIENCE, FINANCIAL_RESILIENCE_DESC, 2);
        addStrengthCandidate(candidates, firstSteps.questionsFinancialOrganizationAndControl(), root.financialData().financialOrganizationAndControl(),
            dimensions.financialOrganizationAndControl(), ORGANIZATION_AND_CONTROL, ORGANIZATION_AND_CONTROL_DESC, 3);
        addStrengthCandidate(candidates, firstSteps.questionsFinancialImpulseRegulation(), root.financialData().financialImpulseRegulation(),
            dimensions.financialImpulseRegulation(), IMPULSE_REGULATION, IMPULSE_REGULATION_DESC, 4);
        addStrengthCandidate(candidates, firstSteps.questionsFinancialOrientationTheFuture(), root.financialData().financialOrientationTheFuture(),
            dimensions.financialOrientationTheFuture(), ORIENTATION_FOR_THE_FUTURE, ORIENTATION_FOR_THE_FUTURE_DESC, 5);
        addStrengthCandidate(candidates, firstSteps.questionsFinancialBalanceAndFreedom(), root.financialData().financialBalanceAndFreedom(),
            dimensions.financialBalanceAndFreedom(), BALANCE_AND_FREEDOM, BALANCE_AND_FREEDOM_DESC, 6);

        List<StrengthCandidate> validCandidates = candidates.stream()
            .filter(candidate -> !candidate.hasCriticalAnswer())
            .toList();
        List<StrengthCandidate> selectionPool = validCandidates.isEmpty() ? candidates : validCandidates;

        StrengthCandidate strength = selectionPool.stream()
            .min(Comparator.comparingDouble(StrengthCandidate::dimensionScore).reversed()
                .thenComparing(Comparator.comparingInt(StrengthCandidate::answersWithFourPoints).reversed())
                .thenComparing(Comparator.comparingInt(StrengthCandidate::answersWithThreePoints).reversed())
                .thenComparingInt(StrengthCandidate::order))
            .orElseThrow();

        boolean isRelativeStrength = strength.hasCriticalAnswer() || strength.dimensionScore() < 60;
        return singleResult(strength.title(), isRelativeStrength ? RELATIVE_STRENGTH_DESC : strength.description());
    }

    private void addStrengthCandidate(
        List<StrengthCandidate> candidates,
        List<Question> answers,
        List<QuestionJson> questions,
        double dimensionScore,
        String title,
        String description,
        int order
    ) {
        int answersWithFourPoints = 0;
        int answersWithThreePoints = 0;
        boolean hasCriticalAnswer = false;

        for (Question answer : answers) {
            Integer score = findScore(answer, questions);
            if (Integer.valueOf(4).equals(score)) answersWithFourPoints++;
            else if (Integer.valueOf(3).equals(score)) answersWithThreePoints++;
            else if (isCriticalScore(score)) hasCriticalAnswer = true;
        }

        candidates.add(new StrengthCandidate(
            title,
            description,
            dimensionScore,
            answersWithFourPoints,
            answersWithThreePoints,
            hasCriticalAnswer,
            order
        ));
    }

    private AttentionAnalysis analyzeCurrentAreaOfFocus(AnswersOfQuizFinancialProfile firstSteps, RootFinancialData root) {
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
        AnswersOfQuizFinancialProfile firstSteps,
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

    private Integer findScore(String questionId, List<Question> answers, List<QuestionJson> questions) {
        return answers.stream()
            .filter(answer -> questionId.equals(answer.questionId()))
            .findFirst()
            .map(answer -> findScore(answer, questions))
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

    private record StrengthCandidate(
        String title,
        String description,
        double dimensionScore,
        int answersWithFourPoints,
        int answersWithThreePoints,
        boolean hasCriticalAnswer,
        int order
    ) {}

    private record ConfidenceAnalysis(int score, Map<String, String> result) {}

    private Map<String, String> analyzeFinancialMoment(double security, double resilience, AnswersOfQuizFinancialProfile firstSteps) {
        Map<String, String> financialMoment = addFinancialMoment(security, resilience);

        defineLimit(financialMoment, firstSteps);

        return financialMoment;
    }

    private Map<String, String> addFinancialMoment(double security, double resilience) {
        Map<String, String> financialMoment = new HashMap<>();

        if (security >= 0 && security <= 24) financialMoment.put(
            FinancialMomentsFlag.UNDER_PRESSURE,
            FinancialMomentsFlag.UNDER_PRESSURE_DESC
        );
        else if ((security >= 25 && security <= 44) && resilience <= 24) financialMoment.put(
            FinancialMomentsFlag.UNDER_PRESSURE,
            FinancialMomentsFlag.UNDER_PRESSURE_DESC
        );
        else if ((security >= 25 && security <= 44) && resilience >= 25) financialMoment.put(
            FinancialMomentsFlag.UNDER_REORGANIZATION,
            FinancialMomentsFlag.UNDER_REORGANIZATION_DESC
        );
        else if ((security >= 45 && security <= 59) && resilience <= 44) financialMoment.put(
            FinancialMomentsFlag.UNDER_REORGANIZATION,
            FinancialMomentsFlag.UNDER_REORGANIZATION_DESC
        );
        else if ((security >= 45 && security <= 59) && resilience >= 45) financialMoment.put(
            FinancialMomentsFlag.STABLE,
            FinancialMomentsFlag.STABLE_DESC
        );
        else if ((security >= 60 && security <= 74) && resilience <= 44) financialMoment.put(
            FinancialMomentsFlag.UNDER_REORGANIZATION,
            FinancialMomentsFlag.UNDER_REORGANIZATION_DESC
        );
        else if ((security >= 60 && security <= 74) && (resilience >= 45 && resilience <= 59)) financialMoment.put(
            FinancialMomentsFlag.STABLE,
            FinancialMomentsFlag.STABLE_DESC
        );
        else if ((security >= 60 && security <= 74) && resilience >= 60) financialMoment.put(
            FinancialMomentsFlag.COMFORTABLE,
            FinancialMomentsFlag.COMFORTABLE_DESC
        );
        else if ((security >= 75 && security <= 100) && resilience <= 44) financialMoment.put(
            FinancialMomentsFlag.UNDER_REORGANIZATION,
            FinancialMomentsFlag.UNDER_REORGANIZATION_DESC
        );
        else if ((security >= 75 && security <= 100) && (resilience >= 45 && resilience <= 59)) financialMoment.put(
            FinancialMomentsFlag.STABLE,
            FinancialMomentsFlag.STABLE_DESC
        );
        else if ((security >= 75 && security <= 100) && (resilience >= 60 && resilience <= 74)) financialMoment.put(
            FinancialMomentsFlag.COMFORTABLE,
            FinancialMomentsFlag.COMFORTABLE_DESC
        );
        else if ((security >= 75 && security <= 100) && (resilience >= 75 && resilience <= 100)) financialMoment.put(
            FinancialMomentsFlag.RESILIENT,
            FinancialMomentsFlag.RESILIENT_DESC
        );

        return financialMoment;
    }

    private void defineLimit(Map<String, String> moment, AnswersOfQuizFinancialProfile firstSteps) {
        String currentMoment = firstKey(moment);
        String limit = analyzeCriticalAnswers(firstSteps);

        boolean shouldLimit =
            FinancialMomentsFlag.UNDER_PRESSURE.equals(limit)
                && !FinancialMomentsFlag.UNDER_PRESSURE.equals(currentMoment)
            || FinancialMomentsFlag.UNDER_REORGANIZATION.equals(limit)
                && (FinancialMomentsFlag.STABLE.equals(currentMoment)
                    || FinancialMomentsFlag.COMFORTABLE.equals(currentMoment)
                    || FinancialMomentsFlag.RESILIENT.equals(currentMoment))
            || FinancialMomentsFlag.STABLE.equals(limit)
                && (FinancialMomentsFlag.COMFORTABLE.equals(currentMoment)
                    || FinancialMomentsFlag.RESILIENT.equals(currentMoment))
            || FinancialMomentsFlag.COMFORTABLE.equals(limit)
                && FinancialMomentsFlag.RESILIENT.equals(currentMoment);

        if (shouldLimit) {
            moment.clear();
            moment.put(limit, FinancialMomentsFlag.financialMoments.get(limit));
        }
    }

    private String analyzeCriticalAnswers(AnswersOfQuizFinancialProfile firstSteps) {
        int underPressure = 0;
        int underReorganization = 0;
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
                if (question.response().equals(spent_more)) underReorganization++;
            }
            if (question.questionId().equals("fp-fc-02")) {
                if (question.response().equals(several_were_delayed)) underReorganization++;
                if (question.response().equals(not_paid_bill)) underPressure++;
            }
            if (question.questionId().equals("fp-fc-03")) {
                if (question.response().equals(practically_zero)) stable++;
                if (question.response().equals(almost_there)) underReorganization++;
                if (question.response().equals(i_need_credit)) underPressure++;
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
                if (question.response().equals(defer_bill_or_loan_repayment)) underReorganization++;
                if (question.response().equals(not_what_do)) underReorganization++;
            }
            if (question.questionId().equals("fp-fr-05")) {
                if (question.response().equals(from_one_to_three_months)) comfortable++;
                if (question.response().equals(a_few_weeks)) stable++;
                if (question.response().equals(less_than_a_week)) underReorganization++;
            }
        }

        if (underPressure > 0) return FinancialMomentsFlag.UNDER_PRESSURE;
        if (underReorganization > 0) return FinancialMomentsFlag.UNDER_REORGANIZATION;
        if (stable > 0) return FinancialMomentsFlag.STABLE;
        if (comfortable > 0) return FinancialMomentsFlag.COMFORTABLE;
        return null;
    }
}
