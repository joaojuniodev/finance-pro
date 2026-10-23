package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class ConfidenceInTheResultFlag {

    public static final String HIGH = "Alta";
    public static final String HIGH_DESC = "As respostas fornecem informações suficientes em todas as dimensões e não apresentam contradições relevantes.";

    public static final String MODERATE = "Moderada";
    public static final String MODERATE_DESC = "Existem algumas respostas ausentes ou pequenas divergências, mas ainda há informações suficientes para formar um retrato coerente.";

    public static final String LOW = "Baixa";
    public static final String LOW_DESC = "O resultado pode ser apresentado, mas existem poucas informações em algumas dimensões ou contradições que limitam sua precisão.";

    public static final String INCONCLUSIVE = "Inconclusiva";
    public static final String INCONCLUSIVE_DESC = "Não existem informações suficientes para definir o perfil. Isso ocorre quando mais de 25% das respostas estão ausentes ou alguma dimensão não possui respostas válidas suficientes.";

    public static final Map<String, String> confidenceInTheResults = Map.of(
        HIGH, HIGH_DESC,
        MODERATE, MODERATE_DESC,
        LOW, LOW_DESC,
        INCONCLUSIVE, INCONCLUSIVE_DESC
    );
}
