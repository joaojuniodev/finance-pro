package br.com.financepro.financePro.account.service.firstSteps;

import br.com.financepro.financePro.account.dto.request.firstSteps.FirstStepsRequestDTO;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.account.dto.response.firstSteps.ResponseTest;
import br.com.financepro.financePro.security.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

record RootFinancialData(
    @JsonProperty("financial-data")
    FinancialCategory financialData
){}

record FinancialCategory(
    @JsonProperty("financial-security")
    List<QuestionJson> financialSecurity,

    @JsonProperty("financial-resilience")
    List<QuestionJson> financialResilience,

    @JsonProperty("financial-organization-and-control")
    List<QuestionJson> financialOrganizationAndControl,

    @JsonProperty("financial-impulse-regulation")
    List<QuestionJson> financialImpulseRegulation,

    @JsonProperty("financial-balance-and-freedom")
    List<QuestionJson> financialBalanceAndFreedom,

    @JsonProperty("financial-orientation-the-future")
    List<QuestionJson> financialOrientationTheFuture
){}

record QuestionJson(
    String id,

    @JsonProperty("title")
    String title,

    Map<String, Integer> options
){}

enum IdentifyFinancial {
    FINANCIAL_SECURITY,
    FINANCIAL_RESILIENCE,
    FINANCIAL_ORGANIZATION_AND_CONTROL,
    FINANCIAL_IMPULSE_REGULATION,
    FINANCIAL_BALANCE_AND_FREEDOM,
    FINANCIAL_ORIENTATION_THE_FUTURE
}

@Service
public class FirstStepsService {
    private static final Logger log = LoggerFactory.getLogger(FirstStepsService.class.getName());

    private static final String path = "classpath:questions/questions-financial-profile.json";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private final int MAX = 4;

    private double pointsFinancialSecurity = 0;
    private double pointsFinancialResilience = 0;
    private double pointsFinancialOrganizationAndControl = 0;
    private double pointsFinancialImpulseRegulation = 0;
    private double pointsFinancialBalanceAndFreedom = 0;

    double numberQuestionsFinancialSecurity = 0;
    double numberQuestionsFinancialResilience = 0;
    double numberQuestionsFinancialOrganizationAndControl = 0;
    double numberQuestionsFinancialImpulseRegulation = 0;
    double numberQuestionsFinancialBalanceAndFreedom = 0;

    public ResponseTest firstSteps(FirstStepsRequestDTO firstSteps) {
        RootFinancialData root = getLoadJsonFile();

        assert root != null;

        double maxPointsFinancialSecurity = 0;
        double maxPointsFinancialResilience = 0;
        double maxPointsFinancialOrganizationAndControl = 0;
        double maxPointsFinancialImpulseRegulation = 0;
        double maxPointsFinancialBalanceAndFreedom = 0;

        numberQuestionsFinancialSecurity = root.financialData().financialSecurity().size();
        numberQuestionsFinancialResilience = root.financialData().financialResilience().size();
        numberQuestionsFinancialOrganizationAndControl = root.financialData().financialOrganizationAndControl().size();
        numberQuestionsFinancialImpulseRegulation = root.financialData().financialImpulseRegulation().size();
        numberQuestionsFinancialBalanceAndFreedom = root.financialData().financialBalanceAndFreedom().size();

        if (numberQuestionsFinancialSecurity > 0 && numberQuestionsFinancialResilience > 0 &&
            numberQuestionsFinancialOrganizationAndControl > 0 && numberQuestionsFinancialImpulseRegulation > 0) {
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
                firstSteps.questionsBalanceAndFreedom(),
                root.financialData().financialBalanceAndFreedom(),
                IdentifyFinancial.FINANCIAL_BALANCE_AND_FREEDOM
            );

            maxPointsFinancialSecurity = numberQuestionsFinancialSecurity * MAX;
            maxPointsFinancialResilience = numberQuestionsFinancialResilience * MAX;
            maxPointsFinancialOrganizationAndControl = numberQuestionsFinancialOrganizationAndControl * MAX;
            maxPointsFinancialImpulseRegulation = numberQuestionsFinancialImpulseRegulation * MAX;
            maxPointsFinancialBalanceAndFreedom = numberQuestionsFinancialBalanceAndFreedom * MAX;
        }

        pointsFinancialSecurity = calculatePoints(pointsFinancialSecurity, maxPointsFinancialSecurity);
        pointsFinancialResilience = calculatePoints(pointsFinancialResilience, maxPointsFinancialResilience);
        pointsFinancialOrganizationAndControl = calculatePoints(pointsFinancialOrganizationAndControl, maxPointsFinancialOrganizationAndControl);
        pointsFinancialImpulseRegulation = calculatePoints(pointsFinancialImpulseRegulation, maxPointsFinancialImpulseRegulation);
        pointsFinancialBalanceAndFreedom = calculatePoints(pointsFinancialBalanceAndFreedom, maxPointsFinancialBalanceAndFreedom);

        return new ResponseTest(pointsFinancialSecurity, pointsFinancialResilience, pointsFinancialOrganizationAndControl, pointsFinancialImpulseRegulation);
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
        }
    }

    private double calculatePoints(double points, double maxPoints) {
        return (points / maxPoints) * 100;
    }

    private RootFinancialData getLoadJsonFile() {
        Resource resource = resourceLoader.getResource(path);

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, RootFinancialData.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}