-- Completed: income 9610.00, expenses 2409.90, net income 7200.10.
-- Pending entries are separate and have not changed wallet balances.
INSERT INTO transactions (
    amount,
    description,
    observation,
    type,
    status,
    registered_at,
    category_id,
    recurrence_id,
    wallet_id,
    account_id
)
SELECT
    seed.amount,
    seed.description,
    'Lançamento fictício para testes locais',
    seed.type,
    seed.status,
    CURRENT_TIMESTAMP,
    category.id,
    recurrence.id,
    wallet.id,
    account.id
FROM (
    VALUES
        (
            8000.00,
            'Salário do mês',
            'CREDIT',
            'COMPLETED',
            'Salário',
            'Salário mensal',
            'Conta principal'
        ),
        (
            1500.00,
            'Projeto freelance',
            'CREDIT',
            'COMPLETED',
            'Freelance',
            NULL,
            'Conta principal'
        ),
        (
            110.00,
            'Dividendos recebidos',
            'CREDIT',
            'COMPLETED',
            'Dividendos',
            NULL,
            'Investimentos'
        ),
        (
            1800.00,
            'Aluguel do mês',
            'DEBIT',
            'COMPLETED',
            'Moradia',
            'Aluguel mensal',
            'Conta principal'
        ),
        (
            450.00,
            'Compras no supermercado',
            'DEBIT',
            'COMPLETED',
            'Alimentação',
            NULL,
            'Conta principal'
        ),
        (
            39.90,
            'Assinatura de streaming',
            'DEBIT',
            'COMPLETED',
            'Assinaturas',
            'Streaming mensal',
            'Conta principal'
        ),
        (
            120.00,
            'Transporte em dinheiro',
            'DEBIT',
            'COMPLETED',
            'Transporte',
            NULL,
            'Dinheiro físico'
        ),
        (
            250.00,
            'Consulta a confirmar',
            'DEBIT',
            'PENDING',
            'Saúde',
            NULL,
            'Conta principal'
        ),
        (
            400.00,
            'Freelance a receber',
            'CREDIT',
            'PENDING',
            'Freelance',
            NULL,
            'Conta principal'
        )
) AS seed (
    amount,
    description,
    type,
    status,
    category_name,
    recurrence_description,
    wallet_name
)
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS wallet
    ON wallet.account_id = account.id
   AND wallet.name = seed.wallet_name
JOIN categories AS category
    ON category.name = seed.category_name
   AND category.type = seed.type
   AND category.system = TRUE
LEFT JOIN recurrences AS recurrence
    ON recurrence.account_id = account.id
   AND recurrence.description = seed.recurrence_description;

-- Expected FinancialHealthService scores in the migration month:
-- mediano: 25 equilibrium + 10 essentials + 5 subscriptions + 10 leisure
--          + 3 without reserve + 3 for one savings goal = 56/100.
-- ruim: expenses exceed income, essentials 64%, subscriptions 34%, leisure 28%,
--       no savings goals and no reserve = 3/100.
-- The pending 600.00 debit affects the score but not the completed balances.
INSERT INTO transactions (
    amount,
    description,
    observation,
    type,
    status,
    registered_at,
    category_id,
    wallet_id,
    account_id
)
SELECT
    seed.amount,
    seed.description,
    'Cenário fictício para testar o score de saúde financeira',
    seed.type,
    seed.status,
    CURRENT_TIMESTAMP,
    category.id,
    wallet.id,
    account.id
FROM (
    VALUES
        (
            'mediano',
            4000.00,
            'Salário do mês',
            'CREDIT',
            'COMPLETED',
            'Salário'
        ),
        (
            'mediano',
            1600.00,
            'Aluguel do mês',
            'DEBIT',
            'COMPLETED',
            'Moradia'
        ),
        (
            'mediano',
            1000.00,
            'Assinaturas e serviços digitais',
            'DEBIT',
            'COMPLETED',
            'Assinaturas'
        ),
        (
            'mediano',
            300.00,
            'Passeios e lazer',
            'DEBIT',
            'COMPLETED',
            'Lazer'
        ),
        (
            'mediano',
            300.00,
            'Compras pessoais',
            'DEBIT',
            'COMPLETED',
            'Compras'
        ),
        (
            'mediano',
            400.00,
            'Compras no supermercado',
            'DEBIT',
            'COMPLETED',
            'Alimentação'
        ),
        (
            'ruim',
            2500.00,
            'Salário do mês',
            'CREDIT',
            'COMPLETED',
            'Salário'
        ),
        (
            'ruim',
            1000.00,
            'Parte paga do aluguel',
            'DEBIT',
            'COMPLETED',
            'Moradia'
        ),
        (
            'ruim',
            600.00,
            'Restante do aluguel a pagar',
            'DEBIT',
            'PENDING',
            'Moradia'
        ),
        (
            'ruim',
            850.00,
            'Assinaturas acima do orçamento',
            'DEBIT',
            'COMPLETED',
            'Assinaturas'
        ),
        (
            'ruim',
            400.00,
            'Passeios acima do orçamento',
            'DEBIT',
            'COMPLETED',
            'Lazer'
        ),
        (
            'ruim',
            300.00,
            'Compras por impulso',
            'DEBIT',
            'COMPLETED',
            'Compras'
        ),
        (
            'ruim',
            350.00,
            'Compras no supermercado',
            'DEBIT',
            'COMPLETED',
            'Alimentação'
        )
) AS seed (
    username,
    amount,
    description,
    type,
    status,
    category_name
)
JOIN users AS app_user
    ON app_user.username = seed.username
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS wallet
    ON wallet.account_id = account.id
   AND wallet.name = 'Conta principal'
JOIN categories AS category
    ON category.name = seed.category_name
   AND category.type = seed.type
   AND category.system = TRUE;
