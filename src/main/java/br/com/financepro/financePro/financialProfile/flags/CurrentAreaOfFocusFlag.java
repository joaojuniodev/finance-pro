package br.com.financepro.financePro.financialProfile.flags;

import java.util.Map;

public class CurrentAreaOfFocusFlag {

    public final String OVERDUE_BILLS = "Contas atrasadas";
    public final String OVERDUE_BILLS_DESC = "Algumas contas estão sendo pagas com atraso e merecem atenção prioritária.";

    public final String UNPAID_IMPORTANT_OBLIGATION = "Compromisso importante não pago";
    public final String UNPAID_IMPORTANT_OBLIGATION_DESC = "Existe indicação de que uma obrigação importante precisou ficar sem pagamento.";

    public final String RECURRING_RELIANCE_ON_CREDIT = "Dependência recorrente de crédito";
    public final String RECURRING_RELIANCE_ON_CREDIT_DESC = "O usuário precisa recorrer frequentemente a crédito, empréstimos ou parcelamentos para concluir o mês.";

    public final String LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS = "Baixa capacidade para enfrentar imprevistos";
    public final String LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS_DESC = "Uma despesa inesperada pode comprometer contas ou necessidades importantes.";

    public final String POSTPONING_BILL_TO_UNFORESEEN_EVENTS = "Adiamento de contas diante de imprevistos";
    public final String POSTPONING_BILL_TO_UNFORESEEN_EVENTS_DESC = "Uma situação inesperada pode exigir que o usuário deixe outro compromisso para depois.";

    public final String LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS = "Lazer mantido com obrigações pendentes";
    public final String LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS_DESC = "Gastos de lazer estão sendo mantidos mesmo quando existem contas importantes aguardando pagamento.";

    public final String ESSENTIALS_NEEDS_SACRIFICED = "Necessidades essenciais sacrificadas";
    public final String ESSENTIALS_NEEDS_SACRIFICED_DESC = "O usuário pode estar deixando de atender necessidades essenciais para manter lazer, compras ou experiências.";

    public final String DIFFICULTY_KEEPING_UP = "Dificuldade de acompanhamento";
    public final String DIFFICULTY_KEEPING_UP_DESC = "Evitar olhar saldo, fatura ou contas está reduzindo a visibilidade sobre a situação financeira.";

    public final String IMPULSES_AFFECTING_COMMITMENTS = "Impulsos afetando compromissos";
    public final String IMPULSES_AFFECTING_COMMITMENTS_DESC = "Compras emocionais ou não planejadas estão interferindo em obrigações ou objetivos importantes.";

    public final String NO_CURRENT_CRITICAL_ATTENTION = "Nenhuma atenção crítica atual";
    public final String NO_CURRENT_CRITICAL_ATTENTION_DESC = "Nenhum sinal crítico relevante foi identificado nas respostas atuais.";

    public final Map<String, String> currentAreasOfFocus = Map.of(
        OVERDUE_BILLS, OVERDUE_BILLS_DESC,
        UNPAID_IMPORTANT_OBLIGATION, UNPAID_IMPORTANT_OBLIGATION_DESC,
        RECURRING_RELIANCE_ON_CREDIT, RECURRING_RELIANCE_ON_CREDIT_DESC,
        LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS, LOW_CAPACITY_TO_DEAL_WITH_UNFORESEEN_EVENTS_DESC,
        POSTPONING_BILL_TO_UNFORESEEN_EVENTS, POSTPONING_BILL_TO_UNFORESEEN_EVENTS_DESC,
        LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS, LEISURE_MAINTAINED_WITH_PENDING_OBLIGATIONS_DESC,
        ESSENTIALS_NEEDS_SACRIFICED, ESSENTIALS_NEEDS_SACRIFICED_DESC,
        DIFFICULTY_KEEPING_UP, DIFFICULTY_KEEPING_UP_DESC,
        IMPULSES_AFFECTING_COMMITMENTS, IMPULSES_AFFECTING_COMMITMENTS_DESC,
        NO_CURRENT_CRITICAL_ATTENTION, NO_CURRENT_CRITICAL_ATTENTION_DESC
    );
}