package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class ConfidenceInTheResultFlag {

    public final String HIGH = "Alta";
    public final String HIGH_DESC = "O usuário respondeu adequadamente às perguntas, todas as dimensões possuem informações suficientes e não existem contradições relevantes.";

    public final String MODERATE = "Moderada";
    public final String MODERATE_DESC = "Existem algumas respostas ausentes ou pequenas divergências, mas ainda há informações suficientes para formar um retrato coerente.";

    public final String LOWER = "Baixa";
    public final String LOWER_DESC = "O resultado pode ser apresentado, mas existem poucas informações em algumas dimensões ou contradições que limitam sua precisão.";

    public final String INCLUSIVE = "Inclusiva";
    public final String INCLUSIVE_DESC = "Não existem informações suficientes para definir o perfil. Isso ocorre quando mais de 25% das respostas estão ausentes ou alguma dimensão não possui respostas válidas suficientes.";

    public final Map<String, String> confidenceInTheResults = Map.of(
        HIGH, HIGH_DESC,
        MODERATE, MODERATE_DESC,
        LOWER, LOWER_DESC,
        INCLUSIVE, INCLUSIVE_DESC
    );
}