package br.com.financepro.financePro.account.dto.request.firstSteps;

import java.util.List;

public record QuestionFinancialSecurity(
    List<Question> questions
) {}