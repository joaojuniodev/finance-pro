-- Non-credit balances reconcile with transactions and movements.
-- Credit-card debt is represented by invoices/installments, not cash balances.
INSERT INTO wallets (
    name,
    description,
    balance,
    card_digits,
    type,
    color,
    goal_id,
    bank_id,
    account_id
)
SELECT
    seed.name,
    seed.description,
    seed.balance,
    seed.card_digits,
    seed.type,
    seed.color,
    goal.id,
    bank.id,
    account.id
FROM (
    VALUES
        (
            'Conta principal',
            'Recebimentos, despesas e pagamento das faturas',
            3710.10,
            NULL,
            'CHECKING',
            '#FF7A00',
            NULL,
            'Inter'
        ),
        (
            'Dinheiro físico',
            'Dinheiro disponível na carteira',
            180.00,
            NULL,
            'PHYSICAL',
            '#A16207',
            NULL,
            NULL
        ),
        (
            'Reserva de emergência',
            'Reserva para imprevistos',
            1500.00,
            NULL,
            'RESERVE',
            '#16A34A',
            'Reserva de emergência',
            'Nubank'
        ),
        (
            'Investimentos',
            'Aportes e rendimentos',
            1110.00,
            NULL,
            'INVESTMENTS',
            '#172554',
            NULL,
            'BTG Pactual'
        ),
        (
            'Poupança de férias',
            'Economia para a próxima viagem',
            500.00,
            NULL,
            'SAVING',
            '#005CA9',
            'Viagem de férias',
            'Caixa Economica Federal'
        ),
        (
            'Saldo digital',
            'Saldo disponível em conta de pagamento',
            200.00,
            NULL,
            'OTHER',
            '#21C25E',
            NULL,
            'PicPay'
        ),
        (
            'Nubank crédito',
            'Fatura atual em aberto e histórico pago',
            0.00,
            '4582',
            'CREDIT_CARD',
            '#820AD1',
            NULL,
            'Nubank'
        ),
        (
            'Inter crédito',
            'Fatura anterior vencida para testes',
            0.00,
            '9013',
            'CREDIT_CARD',
            '#FF7A00',
            NULL,
            'Inter'
        ),
        (
            'C6 crédito',
            'Fatura atual paga e próximo ciclo aberto',
            0.00,
            '7720',
            'CREDIT_CARD',
            '#242424',
            NULL,
            'C6 Bank'
        )
) AS seed (
    name,
    description,
    balance,
    card_digits,
    type,
    color,
    goal_name,
    bank_name
)
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
LEFT JOIN goals AS goal
    ON goal.name = seed.goal_name
LEFT JOIN banks AS bank
    ON bank.name = seed.bank_name;

-- Health profiles have no emergency reserve. Only mediano has a savings goal.
-- Balances match completed transactions; the pending debit of ruim is excluded.
INSERT INTO wallets (
    name,
    description,
    balance,
    type,
    color,
    goal_id,
    bank_id,
    account_id
)
SELECT
    seed.name,
    seed.description,
    seed.balance,
    seed.type,
    seed.color,
    goal.id,
    bank.id,
    account.id
FROM (
    VALUES
        (
            'mediano',
            'Conta principal',
            'Conta do perfil com saúde financeira mediana',
            400.00,
            'CHECKING',
            '#FF7A00',
            NULL,
            'Inter'
        ),
        (
            'mediano',
            'Cofrinho do notebook',
            'Meta criada para começar a economizar',
            0.00,
            'SAVING',
            '#820AD1',
            'Notebook do perfil mediano',
            'Nubank'
        ),
        (
            'ruim',
            'Conta principal',
            'Conta com saldo negativo e despesas pendentes',
            -400.00,
            'CHECKING',
            '#FF7A00',
            NULL,
            'Inter'
        )
) AS seed (
    username,
    name,
    description,
    balance,
    type,
    color,
    goal_name,
    bank_name
)
JOIN users AS app_user
    ON app_user.username = seed.username
JOIN accounts AS account
    ON account.user_id = app_user.id
LEFT JOIN goals AS goal
    ON goal.name = seed.goal_name
LEFT JOIN banks AS bank
    ON bank.name = seed.bank_name;
