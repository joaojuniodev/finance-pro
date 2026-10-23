package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class MainStrengthFlag {

    public static final String FINANCIAL_SECURITY = "Segurança financeira";
    public static final String FINANCIAL_SECURITY_DESC = "O usuário demonstra boa capacidade para manter seus gastos e compromissos dentro dos recursos disponíveis.";

    public static final String FINANCIAL_RESILIENCE = "Resiliência financeira";
    public static final String FINANCIAL_RESILIENCE_DESC = "O usuário demonstra capacidade de enfrentar imprevistos e períodos de instabilidade sem comprometer imediatamente suas necessidades essenciais.";

    public static final String ORGANIZATION_AND_CONTROL = "Organização e controle";
    public static final String ORGANIZATION_AND_CONTROL_DESC = "O usuário acompanha contas, vencimentos, limites e metas, mantendo boa visibilidade sobre sua vida financeira.";

    public static final String IMPULSE_REGULATION = "Regulação de impulsos";
    public static final String IMPULSE_REGULATION_DESC = "O usuário costuma refletir antes de comprar e consegue evitar que decisões imediatas prejudiquem seus compromissos.";

    public static final String ORIENTATION_FOR_THE_FUTURE = "Orientação para o futuro";
    public static final String ORIENTATION_FOR_THE_FUTURE_DESC = "O usuário considera objetivos e consequências futuras ao tomar decisões financeiras no presente.";

    public static final String BALANCE_AND_FREEDOM = "Equilíbrio e liberdade";
    public static final String BALANCE_AND_FREEDOM_DESC = "O usuário consegue aproveitar o presente sem abandonar necessidades, compromissos ou planos importantes.";

    public static final String RELATIVE_STRENGTH_DESC = "Entre as áreas avaliadas, esta é a dimensão relativamente mais preservada e pode apoiar a evolução da vida financeira do usuário.";

    public static final Map<String, String> mainStrengths = Map.of(
        FINANCIAL_SECURITY, FINANCIAL_SECURITY_DESC,
        FINANCIAL_RESILIENCE, FINANCIAL_RESILIENCE_DESC,
        ORGANIZATION_AND_CONTROL, ORGANIZATION_AND_CONTROL_DESC,
        IMPULSE_REGULATION, IMPULSE_REGULATION_DESC,
        ORIENTATION_FOR_THE_FUTURE, ORIENTATION_FOR_THE_FUTURE_DESC,
        BALANCE_AND_FREEDOM, BALANCE_AND_FREEDOM_DESC
    );
}
