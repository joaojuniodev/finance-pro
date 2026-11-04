-- Synthetic financial profiles for the users created by V26__seed_users.sql.
-- These records are development/demo fixtures based on Capto's own methodology.
-- They are not I-SFB results and must not be treated as scientifically validated diagnoses.
WITH profile_seed (
    username,
    financial_trend,
    financial_moment,
    main_strength,
    key_opportunity,
    current_area_of_focus,
    main_attention,
    confidence_score,
    confidence_in_the_result,
    interpretive_synthesis,
    general_financial_index,
    financial_security,
    financial_resilience,
    organization_and_control,
    impulse_regulation,
    orientation_the_future,
    balance_and_freedom
) AS (
    VALUES
        (
            'demo',
            '{"Planejador equilibrado":"Organiza seus compromissos, controla decisões impulsivas e consegue equilibrar necessidades atuais, lazer e objetivos futuros."}'::jsonb,
            '{"Confortável":"As despesas e obrigações estão sob controle, existe alguma margem financeira e não foram encontrados sinais críticos relevantes."}'::jsonb,
            '{"Organização e controle":"O usuário acompanha contas, vencimentos, limites e metas, mantendo boa visibilidade sobre sua vida financeira."}'::jsonb,
            '{"Construir resiliência financeira":"Criar uma reserva ou uma estratégia para imprevistos pode diminuir a dependência de crédito e proteger os compromissos atuais."}'::jsonb,
            '{"Nenhuma atenção crítica atual":"Nenhum sinal crítico relevante foi identificado nas respostas atuais."}'::jsonb,
            '{"Nenhuma atenção crítica atual":"Nenhum sinal crítico relevante foi identificado nas respostas atuais."}'::jsonb,
            100,
            '{"Alta":"As respostas fornecem informações suficientes em todas as dimensões e não apresentam contradições relevantes."}'::jsonb,
            'Suas respostas formam um retrato consistente da sua vida financeira atual. Você tende a planejar suas decisões e a equilibrar compromissos, escolhas do presente e objetivos futuros. Neste momento, suas despesas e obrigações estão sob controle e existe alguma margem financeira. Sua principal força está no acompanhamento de contas, limites, vencimentos e metas. Nenhum sinal crítico relevante apareceu nas suas respostas atuais. Como próximo passo, construir uma reserva ou estratégia para imprevistos pode aumentar sua proteção.',
            78,
            78,
            65,
            86,
            82,
            80,
            76
        ),
        (
            'joaojunio',
            '{"Planejador em construção":"Demonstra intenção de se organizar e preocupação com o futuro, mas ainda encontra dificuldade para transformar essa intenção em acompanhamento, reserva e hábitos consistentes."}'::jsonb,
            '{"Em reorganização":"Existem fragilidades importantes, mas a situação permite ajustes. O usuário precisa reorganizar compromissos, melhorar o acompanhamento ou começar a construir proteção para imprevistos."}'::jsonb,
            '{"Orientação para o futuro":"O usuário considera objetivos e consequências futuras ao tomar decisões financeiras no presente."}'::jsonb,
            '{"Construir resiliência financeira":"Criar uma reserva ou uma estratégia para imprevistos pode diminuir a dependência de crédito e proteger os compromissos atuais."}'::jsonb,
            '{"Baixa capacidade para enfrentar imprevistos":"Uma despesa inesperada pode comprometer contas ou necessidades importantes."}'::jsonb,
            '{"Baixa capacidade para enfrentar imprevistos":"Uma despesa inesperada pode comprometer contas ou necessidades importantes."}'::jsonb,
            100,
            '{"Alta":"As respostas fornecem informações suficientes em todas as dimensões e não apresentam contradições relevantes."}'::jsonb,
            'Suas respostas formam um retrato consistente da sua vida financeira atual. Você demonstra preocupação com o futuro, mas ainda está transformando essa intenção em organização e hábitos financeiros consistentes. Neste momento, sua vida financeira está em reorganização e ainda pede ajustes importantes. Sua principal força está em considerar seus objetivos futuros nas decisões do presente. Agora, o ponto que mais merece atenção é aumentar sua capacidade de enfrentar despesas inesperadas. Como próximo passo, construir uma reserva ou estratégia para imprevistos pode aumentar sua proteção.',
            63,
            58,
            42,
            55,
            74,
            82,
            65
        ),
        (
            'mediano',
            '{"Flexível consciente":"Valoriza experiências e escolhas do presente, mas normalmente respeita seus limites financeiros e preserva compromissos importantes."}'::jsonb,
            '{"Estável":"O usuário consegue manter os compromissos atuais, mas ainda pode possuir pouca margem financeira ou proteção limitada contra situações inesperadas."}'::jsonb,
            '{"Equilíbrio e liberdade":"O usuário consegue aproveitar o presente sem abandonar necessidades, compromissos ou planos importantes."}'::jsonb,
            '{"Construir resiliência financeira":"Criar uma reserva ou uma estratégia para imprevistos pode diminuir a dependência de crédito e proteger os compromissos atuais."}'::jsonb,
            '{"Nenhuma atenção crítica atual":"Nenhum sinal crítico relevante foi identificado nas respostas atuais."}'::jsonb,
            '{"Nenhuma atenção crítica atual":"Nenhum sinal crítico relevante foi identificado nas respostas atuais."}'::jsonb,
            100,
            '{"Alta":"As respostas fornecem informações suficientes em todas as dimensões e não apresentam contradições relevantes."}'::jsonb,
            'Suas respostas formam um retrato consistente da sua vida financeira atual. Você tende a valorizar o presente sem perder de vista seus limites e compromissos financeiros. Neste momento, você consegue manter os compromissos atuais, embora sua margem para imprevistos ainda possa ser limitada. Sua principal força está em conciliar o presente com necessidades, compromissos e planos importantes. Nenhum sinal crítico relevante apareceu nas suas respostas atuais. Como próximo passo, construir uma reserva ou estratégia para imprevistos pode aumentar sua proteção.',
            57,
            55,
            50,
            58,
            60,
            55,
            62
        ),
        (
            'ruim',
            '{"Impulso no comando":"Decisões imediatas, emocionais ou sociais interferem com frequência no planejamento, nos objetivos ou nos compromissos financeiros."}'::jsonb,
            '{"Sob pressão":"O usuário enfrenta dificuldades atuais para manter contas, necessidades ou compromissos. Pode estar recorrendo a crédito, adiando pagamentos ou terminando o mês com insuficiência financeira."}'::jsonb,
            '{"Orientação para o futuro":"Entre as áreas avaliadas, esta é a dimensão relativamente mais preservada e pode apoiar a evolução da vida financeira do usuário."}'::jsonb,
            '{"Construir resiliência financeira":"Criar uma reserva ou uma estratégia para imprevistos pode diminuir a dependência de crédito e proteger os compromissos atuais."}'::jsonb,
            '{"Dependência recorrente de crédito":"O usuário precisa recorrer frequentemente a crédito, empréstimos ou parcelamentos para concluir o mês."}'::jsonb,
            '{"Dependência recorrente de crédito":"O usuário precisa recorrer frequentemente a crédito, empréstimos ou parcelamentos para concluir o mês."}'::jsonb,
            100,
            '{"Alta":"As respostas fornecem informações suficientes em todas as dimensões e não apresentam contradições relevantes."}'::jsonb,
            'Suas respostas formam um retrato consistente da sua vida financeira atual. Decisões imediatas, emocionais ou sociais estão interferindo com frequência no seu planejamento ou nos seus compromissos financeiros. Neste momento, manter contas, necessidades ou compromissos exige atenção imediata. Entre as áreas avaliadas, a orientação para o futuro está relativamente mais preservada e pode apoiar sua evolução financeira. Agora, o ponto que mais merece atenção é diminuir a dependência de crédito para concluir o mês. Como próximo passo, construir uma reserva ou estratégia para imprevistos pode aumentar sua proteção.',
            30,
            22,
            18,
            35,
            28,
            45,
            30
        )
)
INSERT INTO financial_profiles (
    financial_trend,
    financial_moment,
    main_strength,
    key_opportunity,
    current_area_of_focus,
    main_attention,
    confidence_score,
    confidence_in_the_result,
    interpretive_synthesis,
    general_financial_index,
    financial_security,
    financial_resilience,
    organization_and_control,
    impulse_regulation,
    orientation_the_future,
    balance_and_freedom,
    registered_at,
    user_id
)
SELECT
    seed.financial_trend,
    seed.financial_moment,
    seed.main_strength,
    seed.key_opportunity,
    seed.current_area_of_focus,
    seed.main_attention,
    seed.confidence_score,
    seed.confidence_in_the_result,
    seed.interpretive_synthesis,
    seed.general_financial_index,
    seed.financial_security,
    seed.financial_resilience,
    seed.organization_and_control,
    seed.impulse_regulation,
    seed.orientation_the_future,
    seed.balance_and_freedom,
    CURRENT_TIMESTAMP,
    users.id
FROM profile_seed seed
JOIN users ON users.username = seed.username
ON CONFLICT (user_id) DO NOTHING;

UPDATE users
SET completed_questionnaire_financial_life = TRUE
WHERE username IN ('demo', 'joaojunio', 'mediano', 'ruim')
  AND EXISTS (
      SELECT 1
      FROM financial_profiles
      WHERE financial_profiles.user_id = users.id
  );
