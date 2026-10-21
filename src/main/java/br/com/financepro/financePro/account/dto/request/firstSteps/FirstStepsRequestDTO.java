package br.com.financepro.financePro.account.dto.request.firstSteps;

import java.util.List;

// evitar utilizar o ID do usuário por questões de segurança
public record FirstStepsRequestDTO(
    String username,
    List<Question> questionsFinancialSecurity,
    List<Question> questionsFinancialResilience,
    List<Question> questionsFinancialOrganizationAndControl,
    List<Question> questionsFinancialImpulseRegulation,
    List<Question> questionsFinancialBalanceAndFreedom,
    List<Question> questionsFinancialOrientationTheFuture
) {}