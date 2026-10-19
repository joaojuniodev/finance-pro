package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class KeyOpportunityFlag {

    public final String IMPROVE_FINANCIAL_SECURITY = "Melhorar a segurança financeira";
    public final String IMPROVE_FINANCIAL_SECURITY_DESC = "Criar maior equilíbrio entre renda, despesas e compromissos pode trazer mais tranquilidade para os próximos meses.";

    public final String BUILD_FINANCIAL_RESILIENCE = "Construir resiliência financeira";
    public final String BUILD_FINANCIAL_RESILIENCE_DESC = "Criar uma reserva ou uma estratégia para imprevistos pode diminuir a dependência de crédito e proteger os compromissos atuais.";

    public final String IMPROVE_ORGANIZATION_AND_CONTROL = "Melhorar a organização e o controle";
    public final String IMPROVE_ORGANIZATION_AND_CONTROL_DESC = "Acompanhar contas, faturas, limites e metas com maior frequência pode aumentar a sensação de controle.";

    public final String STRENGTHEN_IMPULSE_REGULATION = "Fortalecer a regulação de impulsos";
    public final String STRENGTHEN_IMPULSE_REGULATION_DESC = "Criar uma pausa antes de compras não planejadas pode ajudar a proteger necessidades, compromissos e objetivos.";

    public final String STRENGTHEN_ORIENTATION_THE_FUTURE = "Fortalecer a orientação para o futuro";
    public final String STRENGTHEN_ORIENTATION_THE_FUTURE_DESC = "Transformar objetivos futuros em pequenas ações recorrentes pode tornar os planos mais concretos.";

    public final String IMPROVE_BALANCE__AND_FREEDOM = "Melhorar o equilíbrio e a liberdade";
    public final String IMPROVE_BALANCE__AND_FREEDOM_DESC = "Buscar um equilíbrio mais saudável entre necessidades, lazer, segurança e objetivos pode melhorar a relação do usuário com o dinheiro.";

    public Map<String, String> keyOpportunities = Map.of(
        IMPROVE_FINANCIAL_SECURITY, IMPROVE_FINANCIAL_SECURITY_DESC,
        BUILD_FINANCIAL_RESILIENCE, BUILD_FINANCIAL_RESILIENCE_DESC,
        IMPROVE_ORGANIZATION_AND_CONTROL, IMPROVE_ORGANIZATION_AND_CONTROL_DESC,
        STRENGTHEN_IMPULSE_REGULATION, STRENGTHEN_IMPULSE_REGULATION_DESC,
        STRENGTHEN_ORIENTATION_THE_FUTURE, STRENGTHEN_ORIENTATION_THE_FUTURE_DESC,
        IMPROVE_BALANCE__AND_FREEDOM, IMPROVE_BALANCE__AND_FREEDOM_DESC
    );
}