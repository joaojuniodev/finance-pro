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
