package br.com.financepro.financePro.financialProfile.dto;

import br.com.financepro.financePro.account.dto.request.firstSteps.AnswersOfQuizFinancialProfile;

public record FinancialProfileRequest(
    String username,
    AnswersOfQuizFinancialProfile answers
) {}