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
    List<FinancialCategory> financialData
){}

record FinancialCategory(
    @JsonProperty("financial-security")
    List<QuestionJson> financialSecurity
){}

record QuestionJson(
    String id,

    @JsonProperty("title")
    String title,

    Map<String, Integer> options
){}

@Service
public class FirstStepsService {
    private static final Logger log = LoggerFactory.getLogger(FirstStepsService.class.getName());

    private final String path = "classpath:questions/questions-financial-profile.json";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    private final int MAX = 4;

    private int pointsFinancialSecurity = 0;
    private int totalPointsFinancialSecurity = 0;
    private int numberQuestionsFinancialSecurity = 0;

    public ResponseTest firstSteps(FirstStepsRequestDTO firstSteps) {
        RootFinancialData root = getLoadJsonFile();

        root.financialData().forEach(category -> {
            numberQuestionsFinancialSecurity = category.financialSecurity().size();
        });

        for (Question question : firstSteps.questionFinancialSecurity().questions()) {
            root.financialData().forEach(category -> {
                category.financialSecurity().forEach(questionData -> {
                    if (question.title().equals(questionData.title())) {
                        var point = questionData.options().get(question.response());
                        if (point != null) pointsFinancialSecurity += point;
                        else numberQuestionsFinancialSecurity -= 1;
                    }
                });
            });
        }

        numberQuestionsFinancialSecurity *= MAX;

        return new ResponseTest(pointsFinancialSecurity, totalPointsFinancialSecurity, numberQuestionsFinancialSecurity);
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