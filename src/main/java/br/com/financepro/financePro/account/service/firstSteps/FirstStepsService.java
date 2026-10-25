package br.com.financepro.financePro.account.service.firstSteps;

import br.com.financepro.financePro.account.dto.request.firstSteps.AnswersOfQuizFinancialProfile;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.common.json.FinancialJsonFileReader;
import br.com.financepro.financePro.common.json.QuestionJson;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.dto.FinancialProfileResponse;
import br.com.financepro.financePro.financialProfile.service.BuildFinancialProfileService;
import br.com.financepro.financePro.financialProfile.service.FinancialProfileService;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FirstStepsService {
    private final UserRepository userRepository;
    private final BuildFinancialProfileService buildFinancialProfile;
    private final FinancialProfileService financialProfileService;
    private final FinancialJsonFileReader jsonFileReader;

    public FirstStepsService(UserRepository userRepository, BuildFinancialProfileService buildFinancialProfile,
                             FinancialProfileService financialProfileService, FinancialJsonFileReader jsonFileReader) {
        this.userRepository = userRepository;
        this.buildFinancialProfile = buildFinancialProfile;
        this.financialProfileService = financialProfileService;
        this.jsonFileReader = jsonFileReader;
    }

    public FinancialProfileResponse firstSteps(String username, AnswersOfQuizFinancialProfile answers) {
        var user = userRepository.findByUserName(username);
        if (user == null) throw new NotFoundException("Usuário não encontrado.");
        if (answers == null || !username.equals(answers.username())) {
            throw new InvalidFinancialAnswersException("O questionário deve pertencer ao usuário autenticado.");
        }
        var root = jsonFileReader.read();
        if (root == null) throw new IllegalStateException("Questionário indisponível.");
        var data = root.financialData();
        // Scores belong to this request. Missing answers are excluded from the denominator.
        var dimensions = new FinancialDimensionsDTO(
            score(answers.questionsFinancialSecurity(), data.financialSecurity()),
            score(answers.questionsFinancialResilience(), data.financialResilience()),
            score(answers.questionsFinancialOrganizationAndControl(), data.financialOrganizationAndControl()),
            score(answers.questionsFinancialImpulseRegulation(), data.financialImpulseRegulation()),
            score(answers.questionsFinancialBalanceAndFreedom(), data.financialBalanceAndFreedom()),
            score(answers.questionsFinancialOrientationTheFuture(), data.financialOrientationTheFuture())
        );
        double index = (dimensions.financialSecurity() + dimensions.financialResilience()
            + dimensions.financialOrganizationAndControl() + dimensions.financialImpulseRegulation()
            + dimensions.financialBalanceAndFreedom() + dimensions.financialOrientationTheFuture()) / 6;
        var profile = buildFinancialProfile.execute(dimensions, answers, root);
        return financialProfileService.create(user, index, dimensions, profile);
    }

    private double score(List<Question> answers, List<QuestionJson> questions) {
        if (answers == null || answers.size() != questions.size()) {
            throw new InvalidFinancialAnswersException("Revise as respostas de todas as perguntas.");
        }
        Set<String> answeredIds = new HashSet<>();
        int points = 0;
        int validAnswers = 0;
        for (Question answer : answers) {
            if (answer == null || !answeredIds.add(answer.questionId())) {
                throw new InvalidFinancialAnswersException("O questionário contém uma pergunta inválida ou repetida.");
            }
            var question = questions.stream().filter(item -> item.id().equals(answer.questionId()))
                .findFirst().orElseThrow(() -> new InvalidFinancialAnswersException("Pergunta inválida."));
            if (!question.title().equals(answer.title()) || !question.options().containsKey(answer.response())) {
                throw new InvalidFinancialAnswersException("Uma resposta não corresponde ao questionário atual. Revise suas respostas.");
            }
            Integer value = question.options().get(answer.response());
            if (value != null) {
                points += value;
                validAnswers++;
            }
        }
        return validAnswers == 0 ? 0 : points * 100.0 / (validAnswers * 4);
    }
}
