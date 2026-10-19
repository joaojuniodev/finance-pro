package br.com.financepro.financePro.account.dto.request.firstSteps;

// evitar utilizar o ID do usuário por questões de segurança
public record FirstStepsRequestDTO(
    String username,
    QuestionFinancialSecurity questionFinancialSecurity
) {}