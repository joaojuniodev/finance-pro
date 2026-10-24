package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.dto.request.firstSteps.AnswersOfQuizFinancialProfile;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.financepro.financePro.financialProfile.flags.ConfidenceInTheResultFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.CurrentAreaOfFocusFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.FinancialTrendFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.InterpretiveSynthesisFlag.INCONCLUSIVE_SYNTHESIS;
import static br.com.financepro.financePro.financialProfile.flags.KeyOpportunityFlag.BUILD_FINANCIAL_RESILIENCE;
import static br.com.financepro.financePro.financialProfile.flags.KeyOpportunityFlag.IMPROVE_FINANCIAL_SECURITY;
import static br.com.financepro.financePro.financialProfile.flags.MainStrengthFlag.FINANCIAL_SECURITY;
import static br.com.financepro.financePro.financialProfile.flags.MainStrengthFlag.ORGANIZATION_AND_CONTROL;
import static org.assertj.core.api.Assertions.assertThat;

class BuildFinancialProfileServiceTests {

    private static RootFinancialData root;

    private final BuildFinancialProfileService service = new BuildFinancialProfileService();

    @BeforeAll
    static void loadQuestions() throws IOException {
        try (InputStream input = BuildFinancialProfileServiceTests.class.getResourceAsStream(
            "/questions/questions-financial-profile.json"
        )) {
            root = new ObjectMapper().readValue(input, RootFinancialData.class);
        }
    }

    @Test
    void shouldDetectAllCriticalAnswersAndSelectTheLowestScoreAsMainAttention() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(question("fp-fc-02", "Precisei deixar alguma conta sem pagar")),
            List.of(question("fp-fr-05", "Algumas semanas")),
            List.of(question("fp-foc-09", "Quase sempre")),
            List.of(),
            List.of(
                question("fp-fbf-13", "Quase sempre"),
                question("fp-fbf-16", "Frequentemente"),
                question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")
            ),
            List.of()
        );

        var result = service.execute(dimensions(50, 50, 50, 50, 50, 50), request, root);

        assertThat(result.areaOfFocus())
            .containsKeys(
                UNPAID_IMPORTANT_OBLIGATION,
                LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS,
                DIFFICULTY_KEEPING_UP,
                LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS,
                ESSENTIALS_NEEDS_SACRIFICED
            )
            .doesNotContainKey(IMPULSES_AFFECTING_COMMITMENTS);
        assertThat(result.mainAttention()).containsOnlyKeys(UNPAID_IMPORTANT_OBLIGATION);
    }

    @Test
    void shouldOnlyFlagQuestionSeventeenWhenCommitmentsAreAffected() {
        AnswersOfQuizFinancialProfile balancedRequest = request(
            List.of(), List.of(), List.of(), List.of(),
            List.of(question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")),
            List.of()
        );

        var balancedResult = service.execute(dimensions(70, 70, 70, 70, 70, 70), balancedRequest, root);

        assertThat(balancedResult.areaOfFocus()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);
        assertThat(balancedResult.mainAttention()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);

        AnswersOfQuizFinancialProfile criticalRequest = request(
            List.of(), List.of(), List.of(), List.of(),
            List.of(question("fp-fbf-17", "Minhas decisões de gasto estão prejudicando compromissos importantes")),
            List.of()
        );

        var criticalResult = service.execute(dimensions(70, 70, 70, 70, 20, 70), criticalRequest, root);

        assertThat(criticalResult.areaOfFocus()).containsOnlyKeys(IMPULSES_AFFECTING_COMMITMENTS);
        assertThat(criticalResult.mainAttention()).containsOnlyKeys(IMPULSES_AFFECTING_COMMITMENTS);
    }

    @Test
    void shouldSelectOpportunityFromTheLowestDimensionAmongTwoPointAnswers() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(question("fp-fc-01", "Ficaram praticamente iguais")),
            List.of(question("fp-fr-05", "De um a três meses")),
            List.of(), List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(80, 35, 70, 70, 70, 70), request, root);

        assertThat(result.keyOpportunity()).containsOnlyKeys(BUILD_FINANCIAL_RESILIENCE);
    }

    @Test
    void shouldTreatTwoPointLateBillsAsOpportunityInsteadOfCriticalAttention() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(question("fp-fc-02", "Algumas atrasaram")),
            List.of(), List.of(), List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(40, 70, 70, 70, 70, 70), request,  root);

        assertThat(result.areaOfFocus()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);
        assertThat(result.keyOpportunity()).containsOnlyKeys(IMPROVE_FINANCIAL_SECURITY);
    }

    @Test
    void shouldSelectTheDimensionWithTheHighestScoreAsMainStrength() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(), List.of(),
            List.of(
                question("fp-foc-06", "Quase sempre"),
                question("fp-foc-07", "Frequentemente"),
                question("fp-foc-08", "Às vezes"),
                question("fp-foc-09", "Raramente")
            ),
            List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(60, 55, 80, 70, 65, 75), request, root);

        assertThat(result.mainStrength()).containsOnlyKeys(ORGANIZATION_AND_CONTROL);
    }

    @Test
    void shouldIgnoreTheHighestDimensionWhenItContainsACriticalAnswer() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(question("fp-fc-02", "Precisei deixar alguma conta sem pagar")),
            List.of(),
            List.of(
                question("fp-foc-06", "Quase sempre"),
                question("fp-foc-07", "Frequentemente")
            ),
            List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(85, 70, 75, 70, 70, 70), request, root);

        assertThat(result.mainStrength()).containsOnlyKeys(ORGANIZATION_AND_CONTROL);
    }

    @Test
    void shouldUseFourPointAnswersToBreakATieBetweenDimensions() {
        AnswersOfQuizFinancialProfile request = request(
            List.of(
                question("fp-fc-01", "Gastaram bem menos do que entrou"),
                question("fp-fc-02", "Algumas atrasaram")
            ),
            List.of(
                question("fp-fr-04", "Conseguiria pagar reorganizando o mês"),
                question("fp-fr-05", "De três a seis meses")
            ),
            List.of(), List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(70, 70, 60, 60, 60, 60), request, root);

        assertThat(result.mainStrength()).containsOnlyKeys(FINANCIAL_SECURITY);
    }

    @Test
    void shouldCalculateEveryFinancialTrendFromBehavioralDimensionsAndCriticalAnswers() {
        assertTrend(
            GUARDIAN_CAUTIOUS,
            dimensions(70, 70, 75, 70, 70, 80),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-17", "Cuido tanto do futuro que às vezes não aproveito o presente")), List.of())
        );
        assertTrend(
            PLANNER_BALANCED,
            dimensions(70, 70, 75, 75, 75, 60),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")), List.of())
        );
        assertTrend(
            FLEXIBLE_CONSCIOUSLY,
            dimensions(70, 70, 50, 55, 75, 50),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")), List.of())
        );
        assertTrend(
            PLANNER_UNDER_CONSTRUCTION,
            dimensions(70, 70, 40, 70, 70, 75),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")), List.of())
        );
        assertTrend(
            IMPULSE_AT_THE_CONTROLS,
            dimensions(70, 70, 50, 70, 30, 50),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-13", "Frequentemente")), List.of())
        );
        assertTrend(
            SPONTANEOUS,
            dimensions(70, 70, 45, 40, 50, 40),
            request(List.of(), List.of(), List.of(), List.of(),
                List.of(question("fp-fbf-17", "Frequentemente gasto mais do que pretendia")), List.of())
        );
    }

    @Test
    void shouldReduceConfidenceForEachContradictionAndDetectAnInconclusiveResult() {
        var highConfidence = service.execute(
            dimensions(75, 75, 75, 75, 75, 75),
            completeRequest(Map.of()),
            root
        );
        assertThat(highConfidence.confidenceScore()).isEqualTo(100);
        assertThat(highConfidence.confidenceInTheResult()).containsOnlyKeys(HIGH);

        var moderateConfidence = service.execute(
            dimensions(75, 75, 75, 75, 75, 75),
            completeRequest(Map.of("fp-foc-09", "Quase sempre")),
            root
        );
        assertThat(moderateConfidence.confidenceScore()).isEqualTo(85);
        assertThat(moderateConfidence.confidenceInTheResult()).containsOnlyKeys(MODERATE);

        var lowConfidence = service.execute(
            dimensions(75, 75, 50, 35, 75, 75),
            completeRequest(Map.of(
                "fp-foc-09", "Quase sempre",
                "fp-fir-11", "Frequentemente",
                "fp-fir-12", "Frequentemente"
            )),
            root
        );
        assertThat(lowConfidence.confidenceScore()).isEqualTo(55);
        assertThat(lowConfidence.confidenceInTheResult()).containsOnlyKeys(LOW);

        AnswersOfQuizFinancialProfile complete = completeRequest(Map.of());
        AnswersOfQuizFinancialProfile incomplete = request(
            complete.questionsFinancialSecurity(),
            List.of(question("fp-fr-05", "Não sei dizer")),
            complete.questionsFinancialOrganizationAndControl(),
            complete.questionsFinancialImpulseRegulation(),
            complete.questionsFinancialBalanceAndFreedom(),
            complete.questionsFinancialOrientationTheFuture()
        );
        var inconclusive = service.execute(
            dimensions(75, 0, 75, 75, 75, 75),
            incomplete,
            root
        );
        assertThat(inconclusive.confidenceScore()).isEqualTo(49);
        assertThat(inconclusive.confidenceInTheResult()).containsOnlyKeys(INCONCLUSIVE);
    }

    @Test
    void shouldApplyOneConfidencePenaltyForEachSupportedContradiction() {
        List<Map<String, String>> contradictions = List.of(
            Map.of("fp-fc-01", "Gastaram bem mais"),
            Map.of("fp-fr-04", "Eu não saberia o que fazer"),
            Map.of("fp-foc-09", "Quase sempre"),
            Map.of("fp-fof-15", "Priorizo o presente e resolvo o restante depois"),
            Map.of("fp-fir-11", "Frequentemente"),
            Map.of("fp-fir-12", "Frequentemente"),
            Map.of(
                "fp-foc-08", "Nunca",
                "fp-fof-14", "Guardo ou uso para reduzir dívidas",
                "fp-fof-15", "Priorizo o presente e resolvo o restante depois"
            ),
            Map.of("fp-fbf-13", "Frequentemente"),
            Map.of("fp-fbf-17", "Cuido tanto do futuro que às vezes não aproveito o presente")
        );

        for (Map<String, String> answers : contradictions) {
            var result = service.execute(
                dimensions(75, 75, 75, 75, 75, 75),
                completeRequest(answers),
                root
            );

            assertThat(result.confidenceScore()).isEqualTo(85);
            assertThat(result.confidenceInTheResult()).containsOnlyKeys(MODERATE);
        }
    }

    @Test
    void shouldBuildInterpretiveSynthesisFromTheCalculatedProfile() {
        var result = service.execute(
            dimensions(75, 75, 75, 75, 75, 75),
            completeRequest(Map.of()),
            root
        );

        assertThat(result.interpretiveSynthesis())
            .contains("retrato consistente")
            .contains("planejar suas decisões")
            .contains("boa capacidade para enfrentar imprevistos")
            .contains("Sua principal força")
            .contains("Nenhum sinal crítico relevante");
    }

    @Test
    void shouldIncludeMainAttentionAndOpportunityInTheSynthesis() {
        var result = service.execute(
            dimensions(50, 60, 65, 50, 30, 60),
            completeRequest(Map.of(
                "fp-fc-01", "Ficaram praticamente iguais",
                "fp-fbf-16", "Quase sempre"
            )),
            root
        );

        assertThat(result.interpretiveSynthesis())
            .contains("preservar necessidades essenciais")
            .contains("equilibrar melhor renda, despesas e compromissos");
    }

    @Test
    void shouldNotBuildADefinitiveSynthesisWhenTheResultIsInconclusive() {
        AnswersOfQuizFinancialProfile complete = completeRequest(Map.of());
        AnswersOfQuizFinancialProfile incomplete = request(
            complete.questionsFinancialSecurity(),
            List.of(question("fp-fr-05", "Não sei dizer")),
            complete.questionsFinancialOrganizationAndControl(),
            complete.questionsFinancialImpulseRegulation(),
            complete.questionsFinancialBalanceAndFreedom(),
            complete.questionsFinancialOrientationTheFuture()
        );

        var result = service.execute(
            dimensions(75, 0, 75, 75, 75, 75),
            incomplete,
            root
        );

        assertThat(result.interpretiveSynthesis()).isEqualTo(INCONCLUSIVE_SYNTHESIS);
    }

    private void assertTrend(String expectedTrend, FinancialDimensionsDTO dimensions, AnswersOfQuizFinancialProfile request) {
        var result = service.execute(dimensions, request, root);
        assertThat(result.financialTrend()).containsOnlyKeys(expectedTrend);
    }

    private static AnswersOfQuizFinancialProfile completeRequest(Map<String, String> overrides) {
        Map<String, String> responses = new HashMap<>(Map.ofEntries(
            Map.entry("fp-fc-01", "Gastaram um pouco menos"),
            Map.entry("fp-fc-02", "Todas foram pagas antes ou no prazo"),
            Map.entry("fp-fc-03", "Sobra um pouco"),
            Map.entry("fp-fr-04", "Eu pagaria usando uma reserva"),
            Map.entry("fp-fr-05", "Mais de seis meses"),
            Map.entry("fp-foc-06", "Quase sempre"),
            Map.entry("fp-foc-07", "Quase sempre"),
            Map.entry("fp-foc-08", "Quase sempre"),
            Map.entry("fp-foc-09", "Nunca"),
            Map.entry("fp-fir-10", "Espero um pouco e penso se ainda quero comprar"),
            Map.entry("fp-fir-11", "Nunca"),
            Map.entry("fp-fir-12", "Nunca"),
            Map.entry("fp-fbf-13", "Nunca"),
            Map.entry("fp-fbf-16", "Nunca"),
            Map.entry("fp-fbf-17", "Aproveito o presente sem comprometer meus planos"),
            Map.entry("fp-fof-14", "Divido entre objetivos futuros e algo para aproveitar agora"),
            Map.entry("fp-fof-15", "Equilibro o que quero hoje com meus planos futuros")
        ));
        responses.putAll(overrides);

        return request(
            List.of(
                question("fp-fc-01", responses.get("fp-fc-01")),
                question("fp-fc-02", responses.get("fp-fc-02")),
                question("fp-fc-03", responses.get("fp-fc-03"))
            ),
            List.of(
                question("fp-fr-04", responses.get("fp-fr-04")),
                question("fp-fr-05", responses.get("fp-fr-05"))
            ),
            List.of(
                question("fp-foc-06", responses.get("fp-foc-06")),
                question("fp-foc-07", responses.get("fp-foc-07")),
                question("fp-foc-08", responses.get("fp-foc-08")),
                question("fp-foc-09", responses.get("fp-foc-09"))
            ),
            List.of(
                question("fp-fir-10", responses.get("fp-fir-10")),
                question("fp-fir-11", responses.get("fp-fir-11")),
                question("fp-fir-12", responses.get("fp-fir-12"))
            ),
            List.of(
                question("fp-fbf-13", responses.get("fp-fbf-13")),
                question("fp-fbf-16", responses.get("fp-fbf-16")),
                question("fp-fbf-17", responses.get("fp-fbf-17"))
            ),
            List.of(
                question("fp-fof-14", responses.get("fp-fof-14")),
                question("fp-fof-15", responses.get("fp-fof-15"))
            )
        );
    }

    private static AnswersOfQuizFinancialProfile request(
        List<Question> security,
        List<Question> resilience,
        List<Question> organization,
        List<Question> impulse,
        List<Question> balance,
        List<Question> future
    ) {
        return new AnswersOfQuizFinancialProfile(
            "user@test.com",
            security,
            resilience,
            organization,
            impulse,
            balance,
            future
        );
    }

    private static Question question(String id, String response) {
        return new Question(id, "", response);
    }

    private static FinancialDimensionsDTO dimensions(
        double security,
        double resilience,
        double organization,
        double impulse,
        double balance,
        double future
    ) {
        return new FinancialDimensionsDTO(security, resilience, organization, impulse, balance, future);
    }
}
