package br.com.financepro.financePro.account.service.firstSteps;

import br.com.financepro.financePro.account.dto.request.firstSteps.FirstStepsRequestDTO;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.account.dto.response.firstSteps.ResponseTest;
import br.com.financepro.financePro.common.enums.IdentifyFinancial;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.common.json.FinancialJsonFileReader;
import br.com.financepro.financePro.common.json.QuestionJson;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.service.BuildFinancialProfileService;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirstStepsService {

    private static final Logger log = LoggerFactory.getLogger(FirstStepsService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildFinancialProfileService buildFinancialProfile;

    @Autowired
    private FinancialJsonFileReader jsonFileReader;

    private static final int DIMENSIONS = 6;
    private static final int MAX = 4;

    private double financialIndex = 0;

    private double pointsFinancialSecurity = 0;
    private double pointsFinancialResilience = 0;
    private double pointsFinancialOrganizationAndControl = 0;
    private double pointsFinancialImpulseRegulation = 0;
    private double pointsFinancialBalanceAndFreedom = 0;
    private double pointsFinancialOrientationTheFuture = 0;

    double numberQuestionsFinancialSecurity = 0;
    double numberQuestionsFinancialResilience = 0;
    double numberQuestionsFinancialOrganizationAndControl = 0;
    double numberQuestionsFinancialImpulseRegulation = 0;
    double numberQuestionsFinancialBalanceAndFreedom = 0;
    double numberQuestionsFinancialOrientationTheFuture = 0;

    public ResponseTest firstSteps(FirstStepsRequestDTO firstSteps) {

        var user = userRepository.findByUserName(firstSteps.username());

        if (user == null) {
            throw new NotFoundException("Not found this User by username: " + firstSteps.username());
        }

        RootFinancialData root = jsonFileReader.read();

        assert root != null;

        double maxPointsFinancialSecurity = 0;
        double maxPointsFinancialResilience = 0;
        double maxPointsFinancialOrganizationAndControl = 0;
        double maxPointsFinancialImpulseRegulation = 0;
        double maxPointsFinancialBalanceAndFreedom = 0;
        double maxPointsFinancialOrientationTheFuture = 0;

        numberQuestionsFinancialSecurity = root.financialData().financialSecurity().size();
        numberQuestionsFinancialResilience = root.financialData().financialResilience().size();
        numberQuestionsFinancialOrganizationAndControl = root.financialData().financialOrganizationAndControl().size();
        numberQuestionsFinancialImpulseRegulation = root.financialData().financialImpulseRegulation().size();
        numberQuestionsFinancialBalanceAndFreedom = root.financialData().financialBalanceAndFreedom().size();
        numberQuestionsFinancialOrientationTheFuture = root.financialData().financialOrientationTheFuture().size();

        if (validateNumberQuestions()) {
            buildQuestions(
                firstSteps.questionsFinancialSecurity(),
                root.financialData().financialSecurity(),
                IdentifyFinancial.FINANCIAL_SECURITY
            );
            buildQuestions(
                firstSteps.questionsFinancialResilience(),
                root.financialData().financialResilience(),
                IdentifyFinancial.FINANCIAL_RESILIENCE
            );
            buildQuestions(
                firstSteps.questionsFinancialOrganizationAndControl(),
                root.financialData().financialOrganizationAndControl(),
                IdentifyFinancial.FINANCIAL_ORGANIZATION_AND_CONTROL
            );
            buildQuestions(
                firstSteps.questionsFinancialImpulseRegulation(),
                root.financialData().financialImpulseRegulation(),
                IdentifyFinancial.FINANCIAL_IMPULSE_REGULATION
            );
            buildQuestions(
                firstSteps.questionsFinancialBalanceAndFreedom(),
                root.financialData().financialBalanceAndFreedom(),
                IdentifyFinancial.FINANCIAL_BALANCE_AND_FREEDOM
            );
            buildQuestions(
                firstSteps.questionsFinancialOrientationTheFuture(),
                root.financialData().financialOrientationTheFuture(),
                IdentifyFinancial.FINANCIAL_ORIENTATION_THE_FUTURE
            );

            maxPointsFinancialSecurity = numberQuestionsFinancialSecurity * MAX;
            maxPointsFinancialResilience = numberQuestionsFinancialResilience * MAX;
            maxPointsFinancialOrganizationAndControl = numberQuestionsFinancialOrganizationAndControl * MAX;
            maxPointsFinancialImpulseRegulation = numberQuestionsFinancialImpulseRegulation * MAX;
            maxPointsFinancialBalanceAndFreedom = numberQuestionsFinancialBalanceAndFreedom * MAX;
            maxPointsFinancialOrientationTheFuture = numberQuestionsFinancialOrientationTheFuture * MAX;
        }

        pointsFinancialSecurity = calculatePoints(pointsFinancialSecurity, maxPointsFinancialSecurity);
        pointsFinancialResilience = calculatePoints(pointsFinancialResilience, maxPointsFinancialResilience);
        pointsFinancialOrganizationAndControl = calculatePoints(pointsFinancialOrganizationAndControl, maxPointsFinancialOrganizationAndControl);
        pointsFinancialImpulseRegulation = calculatePoints(pointsFinancialImpulseRegulation, maxPointsFinancialImpulseRegulation);
        pointsFinancialBalanceAndFreedom = calculatePoints(pointsFinancialBalanceAndFreedom, maxPointsFinancialBalanceAndFreedom);
        pointsFinancialOrientationTheFuture = calculatePoints(pointsFinancialOrientationTheFuture, maxPointsFinancialOrientationTheFuture);

        financialIndex =
            (pointsFinancialSecurity + pointsFinancialResilience
            + pointsFinancialOrganizationAndControl + pointsFinancialImpulseRegulation
            + pointsFinancialBalanceAndFreedom + pointsFinancialOrientationTheFuture)
            / DIMENSIONS;

        buildFinancialProfile.build(new FinancialDimensionsDTO(
            pointsFinancialSecurity,
            pointsFinancialResilience,
            pointsFinancialOrganizationAndControl,
            pointsFinancialImpulseRegulation,
            pointsFinancialBalanceAndFreedom,
            pointsFinancialOrientationTheFuture
        ), root, firstSteps, user);

        return new ResponseTest(financialIndex, pointsFinancialSecurity, pointsFinancialResilience, pointsFinancialOrganizationAndControl, pointsFinancialImpulseRegulation, pointsFinancialBalanceAndFreedom, pointsFinancialOrientationTheFuture);
    }

    private boolean validateNumberQuestions() {
        return numberQuestionsFinancialSecurity > 0 && numberQuestionsFinancialResilience > 0 &&
            numberQuestionsFinancialOrganizationAndControl > 0 && numberQuestionsFinancialImpulseRegulation > 0 &&
            numberQuestionsFinancialBalanceAndFreedom > 0 && numberQuestionsFinancialOrientationTheFuture > 0;
    }

    private void buildQuestions(List<Question> questions, List<QuestionJson> financialData, IdentifyFinancial identify) {
        for (Question question : questions) {
            financialData.forEach(questionData -> {
                if (question.title().equals(questionData.title())) {
                    var point = questionData.options().get(question.response());
                    if (point != null) sumPoints(identify, point);
                    else updateNumberQuestions(identify);
                }
            });
        }
    }

    private void sumPoints(IdentifyFinancial name, double point) {
        if (name.equals(IdentifyFinancial.FINANCIAL_SECURITY)) {
            pointsFinancialSecurity += point;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_RESILIENCE)) {
            pointsFinancialResilience += point;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_ORGANIZATION_AND_CONTROL)) {
            pointsFinancialOrganizationAndControl += point;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_IMPULSE_REGULATION)) {
            pointsFinancialImpulseRegulation += point;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_BALANCE_AND_FREEDOM)) {
            pointsFinancialBalanceAndFreedom += point;
        } else {
            pointsFinancialOrientationTheFuture += point;
        }
    }

    private void updateNumberQuestions(IdentifyFinancial name) {
        if (name.equals(IdentifyFinancial.FINANCIAL_SECURITY)) {
            numberQuestionsFinancialSecurity -= 1;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_RESILIENCE)) {
            numberQuestionsFinancialResilience -= 1;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_ORGANIZATION_AND_CONTROL)) {
            numberQuestionsFinancialOrganizationAndControl -= 1;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_IMPULSE_REGULATION)) {
            numberQuestionsFinancialImpulseRegulation -= 1;
        } else if (name.equals(IdentifyFinancial.FINANCIAL_BALANCE_AND_FREEDOM)) {
            numberQuestionsFinancialBalanceAndFreedom -= 1;
        } else {
            numberQuestionsFinancialOrientationTheFuture -= 1;
        }
    }

    private double calculatePoints(double points, double maxPoints) {
        return (points / maxPoints) * 100;
    }
}