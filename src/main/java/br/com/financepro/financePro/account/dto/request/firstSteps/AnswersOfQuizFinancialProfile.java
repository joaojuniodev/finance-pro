package br.com.financepro.financePro.account.dto.request.firstSteps;

import java.util.List;

public record AnswersOfQuizFinancialProfile(
    String username,
    List<Question> questionsFinancialSecurity,
    List<Question> questionsFinancialResilience,
    List<Question> questionsFinancialOrganizationAndControl,
    List<Question> questionsFinancialImpulseRegulation,
    List<Question> questionsFinancialBalanceAndFreedom,
    List<Question> questionsFinancialOrientationTheFuture
){}