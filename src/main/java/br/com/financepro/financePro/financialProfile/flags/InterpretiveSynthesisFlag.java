package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

import static br.com.financepro.financePro.financialProfile.flags.ConfidenceInTheResultFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.CurrentAreaOfFocusFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.FinancialMomentsFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.FinancialTrendFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.KeyOpportunityFlag.*;
import static br.com.financepro.financePro.financialProfile.flags.MainStrengthFlag.*;

public class InterpretiveSynthesisFlag {

    public static final String INCONCLUSIVE_SYNTHESIS = "Ainda não há informações suficientes para construir uma síntese confiável do seu perfil financeiro. Você pode revisar ou completar suas respostas para receber um retrato mais preciso.";
    public static final String NO_CRITICAL_ATTENTION_SYNTHESIS = "Nenhum sinal crítico relevante apareceu nas suas respostas atuais.";

    public static final Map<String, String> CONFIDENCE_INTRODUCTIONS = Map.of(
        HIGH, "Suas respostas formam um retrato consistente da sua vida financeira atual.",
        MODERATE, "Suas respostas formam um retrato coerente, embora existam algumas ausências ou divergências.",
        LOW, "Este retrato deve ser lido com cautela, pois algumas respostas limitam sua precisão."
    );

    public static final Map<String, String> TREND_SYNTHESIS = Map.of(
        GUARDIAN_CAUTIOUS, "Você tende a priorizar a segurança futura e a manter um controle cuidadoso sobre o dinheiro, ainda que às vezes restrinja demais o presente.",
        PLANNER_BALANCED, "Você tende a planejar suas decisões e a equilibrar compromissos, escolhas do presente e objetivos futuros.",
        FLEXIBLE_CONSCIOUSLY, "Você tende a valorizar o presente sem perder de vista seus limites e compromissos financeiros.",
        SPONTANEOUS, "Você tende a decidir mais pelo momento e a planejar com menor regularidade, sem sinais recorrentes de impacto crítico sobre seus compromissos.",
        PLANNER_UNDER_CONSTRUCTION, "Você demonstra preocupação com o futuro, mas ainda está transformando essa intenção em organização e hábitos financeiros consistentes.",
        IMPULSE_AT_THE_CONTROLS, "Decisões imediatas, emocionais ou sociais estão interferindo com frequência no seu planejamento ou nos seus compromissos financeiros."
    );

    public static final Map<String, String> MOMENT_SYNTHESIS = Map.of(
        UNDER_PRESSURE, "Neste momento, manter contas, necessidades ou compromissos exige atenção imediata.",
        UNDER_REORGANIZATION, "Neste momento, sua vida financeira está em reorganização e ainda pede ajustes importantes.",
        STABLE, "Neste momento, você consegue manter os compromissos atuais, embora sua margem para imprevistos ainda possa ser limitada.",
        COMFORTABLE, "Neste momento, suas despesas e obrigações estão sob controle e existe alguma margem financeira.",
        RESILIENT, "Neste momento, além de manter seus compromissos, você demonstra boa capacidade para enfrentar imprevistos."
    );

    public static final Map<String, String> STRENGTH_SYNTHESIS = Map.of(
        FINANCIAL_SECURITY, "Sua principal força está em manter gastos e compromissos dentro dos recursos disponíveis.",
        FINANCIAL_RESILIENCE, "Sua principal força está na capacidade de lidar com imprevistos e períodos de instabilidade.",
        ORGANIZATION_AND_CONTROL, "Sua principal força está no acompanhamento de contas, limites, vencimentos e metas.",
        IMPULSE_REGULATION, "Sua principal força está em refletir antes de comprar e proteger seus compromissos de decisões imediatas.",
        ORIENTATION_FOR_THE_FUTURE, "Sua principal força está em considerar seus objetivos futuros nas decisões do presente.",
        BALANCE_AND_FREEDOM, "Sua principal força está em conciliar o presente com necessidades, compromissos e planos importantes."
    );

    public static final Map<String, String> ATTENTION_SYNTHESIS = Map.ofEntries(
        Map.entry(OVERDUE_BILLS, "Agora, o ponto que mais merece atenção é reduzir os atrasos no pagamento das contas."),
        Map.entry(UNPAID_IMPORTANT_OBLIGATION, "Agora, o ponto que mais merece atenção é recuperar o pagamento de compromissos importantes."),
        Map.entry(RECURRING_RELIANCE_ON_CREDIT, "Agora, o ponto que mais merece atenção é diminuir a dependência de crédito para concluir o mês."),
        Map.entry(LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS, "Agora, o ponto que mais merece atenção é aumentar sua capacidade de enfrentar despesas inesperadas."),
        Map.entry(POSTPONING_BILL_TO_UNFORESEEN_EVENTS, "Agora, o ponto que mais merece atenção é evitar que imprevistos obriguem você a adiar outros compromissos."),
        Map.entry(LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS, "Agora, o ponto que mais merece atenção é conciliar o lazer com o pagamento das obrigações pendentes."),
        Map.entry(ESSENTIALS_NEEDS_SACRIFICED, "Agora, o ponto que mais merece atenção é preservar necessidades essenciais antes de outros gastos."),
        Map.entry(DIFFICULTY_KEEPING_UP, "Agora, o ponto que mais merece atenção é recuperar a visibilidade sobre saldos, faturas e contas."),
        Map.entry(IMPULSES_AFFECTING_COMMITMENTS, "Agora, o ponto que mais merece atenção é impedir que compras impulsivas prejudiquem compromissos importantes.")
    );

    public static final Map<String, String> OPPORTUNITY_SYNTHESIS = Map.of(
        IMPROVE_FINANCIAL_SECURITY, "Como próximo passo, equilibrar melhor renda, despesas e compromissos pode trazer mais tranquilidade.",
        BUILD_FINANCIAL_RESILIENCE, "Como próximo passo, construir uma reserva ou estratégia para imprevistos pode aumentar sua proteção.",
        IMPROVE_ORGANIZATION_AND_CONTROL, "Como próximo passo, acompanhar contas, limites e metas com mais frequência pode ampliar seu controle.",
        STRENGTHEN_IMPULSE_REGULATION, "Como próximo passo, criar uma pausa antes de compras não planejadas pode proteger seus objetivos.",
        STRENGTHEN_ORIENTATION_THE_FUTURE, "Como próximo passo, transformar objetivos futuros em pequenas ações recorrentes pode tornar seus planos mais concretos.",
        IMPROVE_BALANCE_AND_FREEDOM, "Como próximo passo, buscar mais equilíbrio entre necessidades, lazer, segurança e objetivos pode melhorar sua relação com o dinheiro."
    );

    private InterpretiveSynthesisFlag() {}
}
