package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class FinancialTrendFlag {

    public static final String GUARDIAN_CAUTIOUS = "Guardião cauteloso";
    public static final String GUARDIAN_CAUTIOUS_DESC = "Mantém forte controle sobre o dinheiro e prioriza a segurança futura. Pode, entretanto, restringir demais o presente, mesmo quando teria condições de aproveitar com tranquilidade.";

    public static final String PLANNER_BALANCED = "Planejador equilibrado";
    public static final String PLANNER_BALANCED_DESC = "Organiza seus compromissos, controla decisões impulsivas e consegue equilibrar necessidades atuais, lazer e objetivos futuros.";

    public static final String FLEXIBLE_CONSCIOUSLY = "Flexível consciente";
    public static final String FLEXIBLE_CONSCIOUSLY_DESC = "Valoriza experiências e escolhas do presente, mas normalmente respeita seus limites financeiros e preserva compromissos importantes.";

    public static final String SPONTANEOUS = "Espontâneo";
    public static final String SPONTANEOUS_DESC = "Toma decisões com maior influência do momento e planeja com menos regularidade. Apesar disso, seus gastos espontâneos ainda não comprometem frequentemente necessidades ou obrigações importantes.";

    public static final String PLANNER_UNDER_CONSTRUCTION = "Planejador em construção";
    public static final String PLANNER_UNDER_CONSTRUCTION_DESC = "Demonstra intenção de se organizar e preocupação com o futuro, mas ainda encontra dificuldade para transformar essa intenção em acompanhamento, reserva e hábitos consistentes.";

    public static final String IMPULSE_AT_THE_CONTROLS = "Impulso no comando";
    public static final String IMPULSE_AT_THE_CONTROLS_DESC = "Decisões imediatas, emocionais ou sociais interferem com frequência no planejamento, nos objetivos ou nos compromissos financeiros.";

    public static final Map<String, String> financialTrends = Map.of(
        GUARDIAN_CAUTIOUS, GUARDIAN_CAUTIOUS_DESC,
        PLANNER_BALANCED, PLANNER_BALANCED_DESC,
        FLEXIBLE_CONSCIOUSLY, FLEXIBLE_CONSCIOUSLY_DESC,
        SPONTANEOUS, SPONTANEOUS_DESC,
        PLANNER_UNDER_CONSTRUCTION, PLANNER_UNDER_CONSTRUCTION_DESC,
        IMPULSE_AT_THE_CONTROLS, IMPULSE_AT_THE_CONTROLS_DESC
    );
}
