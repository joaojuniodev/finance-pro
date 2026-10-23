package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.dto.request.firstSteps.FirstStepsRequestDTO;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static br.com.financepro.financePro.financialProfile.flags.CurrentAreaOfFocusFlag.*;
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
        FirstStepsRequestDTO request = request(
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

        var result = service.execute(dimensions(50, 50, 50, 50, 50, 50), request, null, root);

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
        FirstStepsRequestDTO balancedRequest = request(
            List.of(), List.of(), List.of(), List.of(),
            List.of(question("fp-fbf-17", "Aproveito o presente sem comprometer meus planos")),
            List.of()
        );

        var balancedResult = service.execute(dimensions(70, 70, 70, 70, 70, 70), balancedRequest, null, root);

        assertThat(balancedResult.areaOfFocus()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);
        assertThat(balancedResult.mainAttention()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);

        FirstStepsRequestDTO criticalRequest = request(
            List.of(), List.of(), List.of(), List.of(),
            List.of(question("fp-fbf-17", "Minhas decisões de gasto estão prejudicando compromissos importantes")),
            List.of()
        );

        var criticalResult = service.execute(dimensions(70, 70, 70, 70, 20, 70), criticalRequest, null, root);

        assertThat(criticalResult.areaOfFocus()).containsOnlyKeys(IMPULSES_AFFECTING_COMMITMENTS);
        assertThat(criticalResult.mainAttention()).containsOnlyKeys(IMPULSES_AFFECTING_COMMITMENTS);
    }

    @Test
    void shouldSelectOpportunityFromTheLowestDimensionAmongTwoPointAnswers() {
        FirstStepsRequestDTO request = request(
            List.of(question("fp-fc-01", "Ficaram praticamente iguais")),
            List.of(question("fp-fr-05", "De um a três meses")),
            List.of(), List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(80, 35, 70, 70, 70, 70), request, null, root);

        assertThat(result.keyOpportunity()).containsOnlyKeys(BUILD_FINANCIAL_RESILIENCE);
    }

    @Test
    void shouldTreatTwoPointLateBillsAsOpportunityInsteadOfCriticalAttention() {
        FirstStepsRequestDTO request = request(
            List.of(question("fp-fc-02", "Algumas atrasaram")),
            List.of(), List.of(), List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(40, 70, 70, 70, 70, 70), request, null, root);

        assertThat(result.areaOfFocus()).containsOnlyKeys(NO_CURRENT_CRITICAL_ATTENTION);
        assertThat(result.keyOpportunity()).containsOnlyKeys(IMPROVE_FINANCIAL_SECURITY);
    }

    @Test
    void shouldSelectTheDimensionWithTheHighestScoreAsMainStrength() {
        FirstStepsRequestDTO request = request(
            List.of(), List.of(),
            List.of(
                question("fp-foc-06", "Quase sempre"),
                question("fp-foc-07", "Frequentemente"),
                question("fp-foc-08", "Às vezes"),
                question("fp-foc-09", "Raramente")
            ),
            List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(60, 55, 80, 70, 65, 75), request, null, root);

        assertThat(result.mainStrength()).containsOnlyKeys(ORGANIZATION_AND_CONTROL);
    }

    @Test
    void shouldIgnoreTheHighestDimensionWhenItContainsACriticalAnswer() {
        FirstStepsRequestDTO request = request(
            List.of(question("fp-fc-02", "Precisei deixar alguma conta sem pagar")),
            List.of(),
            List.of(
                question("fp-foc-06", "Quase sempre"),
                question("fp-foc-07", "Frequentemente")
            ),
            List.of(), List.of(), List.of()
        );

        var result = service.execute(dimensions(85, 70, 75, 70, 70, 70), request, null, root);

        assertThat(result.mainStrength()).containsOnlyKeys(ORGANIZATION_AND_CONTROL);
    }

    @Test
    void shouldUseFourPointAnswersToBreakATieBetweenDimensions() {
        FirstStepsRequestDTO request = request(
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

        var result = service.execute(dimensions(70, 70, 60, 60, 60, 60), request, null, root);

        assertThat(result.mainStrength()).containsOnlyKeys(FINANCIAL_SECURITY);
    }

    private static FirstStepsRequestDTO request(
        List<Question> security,
        List<Question> resilience,
        List<Question> organization,
        List<Question> impulse,
        List<Question> balance,
        List<Question> future
    ) {
        return new FirstStepsRequestDTO(
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
