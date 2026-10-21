package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class FinancialMomentsFlag {

    public static final String UNDER_PRESSURE = "Sob pressão";
    public static final String UNDER_PRESSURE_DESC = "O usuário enfrenta dificuldades atuais para manter contas, necessidades ou compromissos. Pode estar recorrendo a crédito, adiando pagamentos ou terminando o mês com insuficiência financeira.";

    public static final String UNDER_REORGANIZATION = "Em reorganização";
    public static final String UNDER_REORGANIZATION_DESC = "Existem fragilidades importantes, mas a situação permite ajustes. O usuário precisa reorganizar compromissos, melhorar o acompanhamento ou começar a construir proteção para imprevistos.";

    public static final String STABLE = "Estável";
    public static final String STABLE_DESC = "O usuário consegue manter os compromissos atuais, mas ainda pode possuir pouca margem financeira ou proteção limitada contra situações inesperadas.";

    public static final String COMFORTABLE = "Confortável";
    public static final String COMFORTABLE_DESC = "As despesas e obrigações estão sob controle, existe alguma margem financeira e não foram encontrados sinais críticos relevantes.";

    public static final String RESILIENT = "Resiliente";
    public static final String RESILIENT_DESC = "Além de manter os compromissos atuais, o usuário possui boa capacidade de enfrentar despesas inesperadas ou períodos de redução de renda.";

    public Map<String, String> financialMoments = Map.of(
        UNDER_PRESSURE, UNDER_PRESSURE_DESC,
        UNDER_REORGANIZATION, UNDER_REORGANIZATION_DESC,
        STABLE, STABLE_DESC,
        COMFORTABLE, COMFORTABLE_DESC,
        RESILIENT, RESILIENT_DESC
    );
}