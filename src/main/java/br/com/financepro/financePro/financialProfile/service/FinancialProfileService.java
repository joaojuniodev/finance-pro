package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileResponse;
import br.com.financepro.financePro.financialProfile.model.FinancialProfile;
import br.com.financepro.financePro.financialProfile.repository.FinancialProfileRepository;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(FinancialProfileService.class.getName());

    private final UserRepository userRepository;

    private final FinancialProfileRepository repository;

    public FinancialProfileService(UserRepository userRepository, FinancialProfileRepository repository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Transactional
    public FinancialProfileResponse create(
        User user,
        double generalFinancialIndex,
        FinancialDimensionsDTO dimensions,
        FinancialProfileResponse financialProfile
    ) {
        log.info("Registering Financial Profile");

        FinancialProfile entity = repository.findByUserId(user.getId()).orElseGet(FinancialProfile::new);

        entity.setFinancialTrend(financialProfile.financialTrend());
        entity.setFinancialMoment(financialProfile.financialMoment());
        entity.setCurrentAreaOfFocus(financialProfile.areaOfFocus());
        entity.setMainAttention(financialProfile.mainAttention());
        entity.setMainStrength(financialProfile.mainStrength());
        entity.setKeyOpportunity(financialProfile.keyOpportunity());
        entity.setConfidenceScore(financialProfile.confidenceScore());
        entity.setConfidenceInTheResult(financialProfile.confidenceInTheResult());
        entity.setInterpretiveSynthesis(financialProfile.interpretiveSynthesis());
        entity.setGeneralFinancialIndex(roundScore(generalFinancialIndex));
        entity.setFinancialSecurity(roundScore(dimensions.financialSecurity()));
        entity.setFinancialResilience(roundScore(dimensions.financialResilience()));
        entity.setOrganizationAndControl(roundScore(dimensions.financialOrganizationAndControl()));
        entity.setImpulseRegulation(roundScore(dimensions.financialImpulseRegulation()));
        entity.setBalanceAndFreedom(roundScore(dimensions.financialBalanceAndFreedom()));
        entity.setOrientationTheFuture(roundScore(dimensions.financialOrientationTheFuture()));
        entity.setRegisteredAt(LocalDateTime.now());
        entity.setUser(user);

        repository.save(entity);

        user.setCompletedQuestionnaireFinancialLife(true);
        userRepository.save(user);

        return financialProfile;
    }

    private int roundScore(double score) {
        return (int) Math.round(score);
    }
}
