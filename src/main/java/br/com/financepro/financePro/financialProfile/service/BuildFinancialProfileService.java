package br.com.financepro.financePro.financialProfile.service;

import br.com.financepro.financePro.account.dto.request.firstSteps.FirstStepsRequestDTO;
import br.com.financepro.financePro.account.dto.request.firstSteps.Question;
import br.com.financepro.financePro.common.json.RootFinancialData;
import br.com.financepro.financePro.financialProfile.dto.FinancialDimensionsDTO;
import br.com.financepro.financePro.financialProfile.flags.FinancialMomentsFlag;
import br.com.financepro.financePro.financialProfile.repository.FinancialProfileRepository;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildFinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(BuildFinancialProfileService.class.getName());

    // Momento financeiro:
    // Segurança financeira - Resiliência financeira - respostas críticas relacinadas a contas, crédito e imprevistos

    // Identificar as atenções críticas

    // Tendência financeira:
    // Organização e controle - Regulação de impulsos - Orientação para o futuro - Equilíbrio e liberdade - respostas críticas comportamentais

    // Descobrir a força principal

    // Descobrir a principal oportunidade

    // Calcular a confiança (confiança do resultado do perfil financeiro)

    // Construir a síntese interpretativa

    @Autowired
    private FinancialProfileRepository repository;

    @Autowired
    private UserRepository userRepository;

    private final String[] responsesCriticalOfFinancialMoment = new String[]
        {"fp-fc-01", "fp-fc-02", "fp-fc-03", "fp-fr-04", "fp-fr-05", "fp-foc-09", "fp-fbf-13", "fp-fbf-16", "fp-fbf-17"};

    public void execute(FinancialDimensionsDTO dimensions, FirstStepsRequestDTO firstSteps, User user) {
        log.info("Building Financial Profile this User");

        var moment = analyzeFinancialMoment(dimensions.financialSecurity(), dimensions.financialResilience(), firstSteps);

        System.out.println(moment);
    }

    private String analyzeFinancialMoment(double security, double resilience, FirstStepsRequestDTO firstSteps) {

        String moment = "";

        if (security <= 24 && resilience <= 24) moment = FinancialMomentsFlag.UNDER_PRESSURE;
        if ((security >= 25 && security <= 44) && resilience <= 24) moment = FinancialMomentsFlag.UNDER_PRESSURE;
        if ((security >= 25 && security <= 44) && resilience >= 25) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        if ((security >= 45 && security <= 59) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        if ((security >= 45 && security <= 59) && resilience >= 45) moment = FinancialMomentsFlag.STABLE;
        if ((security >= 60 && security <= 74) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        if ((security >= 60 && security <= 74) && (resilience >= 45 && resilience <= 59)) moment = FinancialMomentsFlag.STABLE;
        if ((security >= 60 && security <= 74) && resilience >= 60) moment = FinancialMomentsFlag.COMFORTABLE;
        if ((security >= 75 && security <= 100) && resilience <= 44) moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
        if ((security >= 75 && security <= 100) && (resilience >= 45 && resilience <= 59)) moment = FinancialMomentsFlag.STABLE;
        if ((security >= 75 && security <= 100) && (resilience >= 60 && resilience <= 74)) moment = FinancialMomentsFlag.COMFORTABLE;
        if ((security >= 75 && security <= 100) && (resilience >= 75 && resilience <= 100)) moment = FinancialMomentsFlag.RESILIENT;

        int under_pressure = 0;
        int under_reorganization = 0;
        int stable = 0;
        int comfortable = 0;

        final String spent_more = "Gastaram bem mais";
        final String several_were_delayed = "Várias atrasaram";
        final String not_paid_bill = "Precisei deixar alguma conta sem pagar";
        final String practically_zero = "Fico praticamente no zero";
        final String almost_there = "Falta um pouco";
        final String i_need_credit = "Preciso recorrer a crédito, empréstimo ou atraso de contas";

        for (Question question : firstSteps.questionsFinancialSecurity()) {
            if (question.questionId().equals("fp-fc-01")) {
                if (question.response().equals(spent_more)) under_reorganization++;
            }
            if (question.questionId().equals("fp-fc-02")) {
                if (question.response().equals(several_were_delayed)) under_reorganization++;
                if (question.response().equals(not_paid_bill)) under_pressure++;
            }
            if (question.questionId().equals("fp-fc-03")) {
                if (question.response().equals(practically_zero)) stable++;
                if (question.response().equals(almost_there)) under_reorganization++;
                if (question.response().equals(i_need_credit)) under_pressure++;
            }
        }

        final String pay_but_reorganize_the_month = "Conseguiria pagar reorganizando o mês";
        final String processes_installments_and_maintains_accounts = "Parcelaria, mas manteria as outras contas";
        final String defer_bill_or_loan_repayment = "Precisaria adiar uma conta ou pedir dinheiro emprestado";
        final String not_what_do = "Eu não saberia o que fazer";
        final String from_one_to_three_months = "De um a três meses";
        final String a_few_weeks = "Algumas semanas";
        final String less_than_a_week = "Menos de uma semana";

        for (Question question : firstSteps.questionsFinancialResilience()) {
            if (question.questionId().equals("fp-fr-04")) {
                if (question.response().equals(pay_but_reorganize_the_month)) comfortable++;
                if (question.response().equals(processes_installments_and_maintains_accounts)) stable++;
                if (question.response().equals(defer_bill_or_loan_repayment)) under_reorganization++;
                if (question.response().equals(not_what_do)) under_reorganization++;
            }
            if (question.questionId().equals("fp-fr-05")) {
                if (question.response().equals(from_one_to_three_months)) comfortable++;
                if (question.response().equals(a_few_weeks)) stable++;
                if (question.response().equals(less_than_a_week)) under_reorganization++;
            }
        }

        // Do pior para o melhor:
        // 1. Sob pressão
        // 2. No máximo em reorganização
        // 3. No máximo estável
        // 4. No máximo confortável
        // 5. Resiliente

        if (under_pressure > 0) {
            if (moment.equals(FinancialMomentsFlag.UNDER_REORGANIZATION) || moment.equals(FinancialMomentsFlag.STABLE) ||
                moment.equals(FinancialMomentsFlag.COMFORTABLE) || moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.UNDER_PRESSURE;
            }
        }
        else if (under_reorganization > 0) {
            if (moment.equals(FinancialMomentsFlag.STABLE) || moment.equals(FinancialMomentsFlag.COMFORTABLE) ||
                moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.UNDER_REORGANIZATION;
            }
        }
        else if (stable > 0) {
            if (moment.equals(FinancialMomentsFlag.COMFORTABLE) || moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.STABLE;
            }

        }
        else if (comfortable > 0) {
            if (moment.equals(FinancialMomentsFlag.RESILIENT)) {
                moment = FinancialMomentsFlag.COMFORTABLE;
            }
        }

        return moment;
    }
}