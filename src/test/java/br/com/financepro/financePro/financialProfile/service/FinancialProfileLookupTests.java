package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.financialProfile.controller.FinancialProfileController;
import br.com.financepro.financePro.financialProfile.model.FinancialProfile;
import br.com.financepro.financePro.financialProfile.repository.FinancialProfileRepository;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinancialProfileLookupTests {
    @Test
    void lookupUsesTheAuthenticatedIdentityAndReturnsOnlyProfileDataWithoutCaching() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword("private");
        FinancialProfile profile = new FinancialProfile();
        profile.setUser(user);
        profile.setGeneralFinancialIndex(67);
        profile.setFinancialSecurity(70);
        profile.setFinancialResilience(40);
        profile.setOrganizationAndControl(85);
        profile.setImpulseRegulation(75);
        profile.setBalanceAndFreedom(65);
        profile.setOrientationTheFuture(67);
        profile.setFinancialTrend(Map.of("Tendência", "Descrição"));
        profile.setRegisteredAt(LocalDateTime.of(2026, 10, 24, 12, 0));

        var service = service(user, profile);
        var response = new FinancialProfileController(service).getMine(() -> "authenticated-user");
        assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().generalFinancialIndex()).isEqualTo(67);
        assertThat(response.getBody().dimensions().financialSecurity()).isEqualTo(70d);
        assertThat(response.getBody().dimensions().financialOrientationTheFuture()).isEqualTo(67d);
        assertThat(response.getBody().profile().financialTrend()).containsEntry("Tendência", "Descrição");
        var json = new ObjectMapper().findAndRegisterModules().valueToTree(response.getBody());
        assertThat(json.has("user")).isFalse();
        assertThat(json.toString()).doesNotContain("private", "password", "accessToken");
    }

    @Test
    void aMissingProfileDoesNotProduceMockedOrZeroResults() {
        User user = new User();
        user.setId(UUID.randomUUID());
        assertThatThrownBy(() -> service(user, null).getByUsername("authenticated-user")).isInstanceOf(NotFoundException.class);
    }

    private FinancialProfileService service(User user, FinancialProfile profile) {
        UserRepository users = (UserRepository) Proxy.newProxyInstance(UserRepository.class.getClassLoader(), new Class<?>[]{UserRepository.class}, (proxy, method, arguments) -> {
            assertThat(method.getName()).isEqualTo("findByUserName");
            assertThat(arguments[0]).isEqualTo("authenticated-user");
            return user;
        });
        FinancialProfileRepository profiles = (FinancialProfileRepository) Proxy.newProxyInstance(FinancialProfileRepository.class.getClassLoader(), new Class<?>[]{FinancialProfileRepository.class}, (proxy, method, arguments) -> {
            assertThat(method.getName()).isEqualTo("findByUserId");
            assertThat(arguments[0]).isEqualTo(user.getId());
            return Optional.ofNullable(profile);
        });
        return new FinancialProfileService(users, profiles);
    }
}
