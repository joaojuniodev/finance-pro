package br.com.financepro.financePro.account.service.firstSteps;

import br.com.financepro.financePro.account.controller.AccountController;
import br.com.financepro.financePro.account.dto.request.firstSteps.AnswersOfQuizFinancialProfile;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.json.FinancialJsonFileReader;
import br.com.financepro.financePro.common.json.QuestionJson;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.controller.FinancialProfileController;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileResponse;
import br.com.financepro.financePro.financialProfile.service.BuildFinancialProfileService;
import br.com.financepro.financePro.financialProfile.service.FinancialProfileService;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FirstStepsIntegrationTests {
    private RootFinancialData root;
    private FirstStepsService service;
    private final List<Double> savedIndexes = Collections.synchronizedList(new ArrayList<>());

    @BeforeEach
    void setUp() throws Exception {
        try (var input = getClass().getResourceAsStream("/questions/questions-financial-profile.json")) {
            root = new ObjectMapper().readValue(input, RootFinancialData.class);
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        UserRepository users = (UserRepository) Proxy.newProxyInstance(UserRepository.class.getClassLoader(),
            new Class<?>[]{UserRepository.class}, (proxy, method, arguments) -> user);
        var reader = new FinancialJsonFileReader() {
            @Override public RootFinancialData read() { return root; }
        };
        var persistence = new FinancialProfileService(users, null) {
            @Override public FinancialProfileResponse create(User owner, double index, FinancialDimensionsDTO dimensions, FinancialProfileResponse profile) {
                savedIndexes.add(index);
                return profile;
            }
        };
        service = new FirstStepsService(users, new BuildFinancialProfileService(), persistence, reader);
    }

    @Test
    void repeatedSubmissionsMustNotAccumulateScores() {
        var answers = answers(Set.of());
        service.firstSteps("test", answers);
        service.firstSteps("test", answers);
        assertThat(savedIndexes).containsExactly(100d, 100d);
    }

    @Test
    void missingOptionsAreExcludedFromTheDenominator() {
        service.firstSteps("test", answers(Set.of("fp-fc-01", "fp-fr-05", "fp-fbf-13", "fp-fof-14")));
        assertThat(savedIndexes).containsExactly(100d);
    }

    @Test
    void concurrentRequestsHaveIndependentScores() throws Exception {
        var executor = Executors.newFixedThreadPool(4);
        try {
            var calls = new ArrayList<java.util.concurrent.Callable<FinancialProfileResponse>>();
            for (int i = 0; i < 12; i++) calls.add(() -> service.firstSteps("test", answers(Set.of())));
            for (var future : executor.invokeAll(calls)) future.get();
            assertThat(savedIndexes).hasSize(12).allMatch(score -> score == 100d);
        } finally { executor.shutdownNow(); }
    }

    @Test
    void invalidDuplicateAndIncompleteAnswersAreRejectedBeforePersistence() {
        var good = answers(Set.of());
        var duplicates = List.of(good.questionsFinancialSecurity().get(0), good.questionsFinancialSecurity().get(0), good.questionsFinancialSecurity().get(2));
        var invalid = new Question("fp-fc-01", good.questionsFinancialSecurity().get(0).title(), "forged-answer");
        for (var group : List.of(duplicates, List.of(invalid), List.of(invalid, good.questionsFinancialSecurity().get(1), good.questionsFinancialSecurity().get(2)))) {
            var bad = new AnswersOfQuizFinancialProfile("test", group, good.questionsFinancialResilience(), good.questionsFinancialOrganizationAndControl(), good.questionsFinancialImpulseRegulation(), good.questionsFinancialBalanceAndFreedom(), good.questionsFinancialOrientationTheFuture());
            assertThatThrownBy(() -> service.firstSteps("test", bad)).isInstanceOf(InvalidFinancialAnswersException.class);
        }
        assertThatThrownBy(() -> service.firstSteps("other-user", good)).isInstanceOf(InvalidFinancialAnswersException.class);
        assertThatThrownBy(() -> service.firstSteps("test", null)).isInstanceOf(InvalidFinancialAnswersException.class);
        assertThat(savedIndexes).isEmpty();
    }

    @Test
    void endpointsRequireAuthenticationAndRejectAnotherUsersPath() {
        var controller = new AccountController();
        assertThat(controller.firstSteps("test", null, null).getStatusCode().value()).isEqualTo(401);
        assertThat(controller.firstSteps("another-user", null, () -> "test").getStatusCode().value()).isEqualTo(403);
        assertThat(new FinancialProfileController(null).getMine(null).getStatusCode().value()).isEqualTo(401);
    }

    private AnswersOfQuizFinancialProfile answers(Set<String> missing) {
        var data = root.financialData();
        return new AnswersOfQuizFinancialProfile("test", group(data.financialSecurity(), missing), group(data.financialResilience(), missing),
            group(data.financialOrganizationAndControl(), missing), group(data.financialImpulseRegulation(), missing),
            group(data.financialBalanceAndFreedom(), missing), group(data.financialOrientationTheFuture(), missing));
    }

    private List<Question> group(List<QuestionJson> questions, Set<String> missing) {
        return questions.stream().map(question -> {
            String response = missing.contains(question.id())
                ? question.options().entrySet().stream().filter(entry -> entry.getValue() == null).findFirst().orElseThrow().getKey()
                : question.options().entrySet().stream().filter(entry -> entry.getValue() != null).max(Comparator.comparingInt(Map.Entry::getValue)).orElseThrow().getKey();
            return new Question(question.id(), question.title(), response);
        }).toList();
    }
}
