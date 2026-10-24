package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileResponse;
import br.com.financepro.financePro.financialProfile.model.FinancialProfile;
import br.com.financepro.financePro.financialProfile.repository.FinancialProfileRepository;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialProfileServiceTests {

    private FinancialProfile existingProfile;
    private FinancialProfile savedProfile;
    private User savedUser;
    private FinancialProfileService service;

    @BeforeEach
    void setUp() {
        FinancialProfileRepository repository = proxy(FinancialProfileRepository.class, (method, arguments) -> {
            if ("findByUserId".equals(method)) return Optional.ofNullable(existingProfile);
            if ("save".equals(method)) {
                savedProfile = (FinancialProfile) arguments[0];
                return savedProfile;
            }
            throw new UnsupportedOperationException(method);
        });

        UserRepository userRepository = proxy(UserRepository.class, (method, arguments) -> {
            if ("save".equals(method)) {
                savedUser = (User) arguments[0];
                return savedUser;
            }
            throw new UnsupportedOperationException(method);
        });

        service = new FinancialProfileService(userRepository, repository);
    }

    @Test
    void shouldPersistTheCompleteFinancialProfileAndRelateItToTheUser() {
        User user = user();
        FinancialDimensionsDTO dimensions = new FinancialDimensionsDTO(61.4, 72.5, 83.2, 54.7, 65.1, 76.8);
        FinancialProfileResponse response = response();

        FinancialProfileResponse result = service.create(user, 68.9, dimensions, response);

        assertThat(result).isSameAs(response);
        assertThat(savedProfile.getUser()).isSameAs(user);
        assertThat(savedProfile.getFinancialTrend()).isEqualTo(response.financialTrend());
        assertThat(savedProfile.getFinancialMoment()).isEqualTo(response.financialMoment());
        assertThat(savedProfile.getCurrentAreaOfFocus()).isEqualTo(response.areaOfFocus());
        assertThat(savedProfile.getMainAttention()).isEqualTo(response.mainAttention());
        assertThat(savedProfile.getMainStrength()).isEqualTo(response.mainStrength());
        assertThat(savedProfile.getKeyOpportunity()).isEqualTo(response.keyOpportunity());
        assertThat(savedProfile.getConfidenceScore()).isEqualTo(response.confidenceScore());
        assertThat(savedProfile.getConfidenceInTheResult()).isEqualTo(response.confidenceInTheResult());
        assertThat(savedProfile.getInterpretiveSynthesis()).isEqualTo(response.interpretiveSynthesis());
        assertThat(savedProfile.getGeneralFinancialIndex()).isEqualTo(69);
        assertThat(savedProfile.getFinancialSecurity()).isEqualTo(61);
        assertThat(savedProfile.getFinancialResilience()).isEqualTo(73);
        assertThat(savedProfile.getOrganizationAndControl()).isEqualTo(83);
        assertThat(savedProfile.getImpulseRegulation()).isEqualTo(55);
        assertThat(savedProfile.getBalanceAndFreedom()).isEqualTo(65);
        assertThat(savedProfile.getOrientationTheFuture()).isEqualTo(77);
        assertThat(savedProfile.getRegisteredAt()).isNotNull();
        assertThat(user.getCompletedQuestionnaireFinancialLife()).isTrue();
        assertThat(savedUser).isSameAs(user);
    }

    @Test
    void shouldUpdateTheExistingProfileInsteadOfCreatingAnotherOne() {
        User user = user();
        existingProfile = new FinancialProfile();
        existingProfile.setId(UUID.randomUUID());

        service.create(
            user,
            70,
            new FinancialDimensionsDTO(70d, 70d, 70d, 70d, 70d, 70d),
            response()
        );

        assertThat(savedProfile).isSameAs(existingProfile);
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCompletedQuestionnaireFinancialLife(false);
        return user;
    }

    private FinancialProfileResponse response() {
        return new FinancialProfileResponse(
            Map.of("Planejador equilibrado", "Descrição da tendência"),
            Map.of("Estável", "Descrição do momento"),
            Map.of("Nenhuma atenção crítica atual", "Descrição das atenções"),
            Map.of("Nenhuma atenção crítica atual", "Descrição da atenção principal"),
            Map.of("Organização e controle", "Descrição da força"),
            Map.of("Construir resiliência financeira", "Descrição da oportunidade"),
            85,
            Map.of("Moderada", "Descrição da confiança"),
            "Síntese interpretativa"
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, RepositoryCall call) {
        return (T) Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[]{type},
            (proxy, method, arguments) -> call.execute(method.getName(), arguments)
        );
    }

    private interface RepositoryCall {
        Object execute(String method, Object[] arguments);
    }
}
