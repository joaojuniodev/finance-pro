package br.com.financepro.financePro.account.dto.request.firstSteps;

public record Question(
    String questionId,
    String title,
    String response
) {}