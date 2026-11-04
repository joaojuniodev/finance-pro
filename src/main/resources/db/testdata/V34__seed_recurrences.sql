INSERT INTO recurrences (
    amount,
    type,
    frequency_type,
    execution_type,
    status,
    day_one,
    day_two,
    month_of_the_year,
    next_execution_date,
    last_execution_date,
    description,
    category_id,
    wallet_id,
    account_id
)
SELECT
    seed.amount,
    seed.type,
    seed.frequency_type,
    seed.execution_type,
    seed.status,
    seed.day_one,
    seed.day_two,
    seed.month_of_the_year,
    CASE
        WHEN seed.status = 'ENDED' THEN NULL
        WHEN seed.frequency_type = 'YEARLY' THEN
            MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER + 1, seed.month_of_the_year, seed.day_one)
        ELSE
            (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month')::DATE + seed.day_one - 1
    END,
    CASE
        WHEN seed.status = 'ENDED' THEN (CURRENT_DATE - INTERVAL '1 month')::DATE
        ELSE CURRENT_DATE
    END,
    seed.description,
    category.id,
    wallet.id,
    account.id
FROM (
    VALUES
        (
            8000.00,
            'CREDIT',
            'MONTHLY',
            'AUTOMATIC',
            'ACTIVE',
            1,
            NULL::INTEGER,
            NULL::INTEGER,
            'Salário mensal',
            'Salário'
        ),
        (
            1800.00,
            'DEBIT',
            'MONTHLY',
            'AUTOMATIC',
            'ACTIVE',
            3,
            NULL,
            NULL,
            'Aluguel mensal',
            'Moradia'
        ),
        (
            39.90,
            'DEBIT',
            'MONTHLY',
            'MANUALLY',
            'ACTIVE',
            5,
            NULL,
            NULL,
            'Streaming mensal',
            'Assinaturas'
        ),
        (
            150.00,
            'DEBIT',
            'BIWEEKLY',
            'MANUALLY',
            'PAUSED',
            5,
            20,
            NULL,
            'Aulas de idiomas',
            'Educação'
        ),
        (
            600.00,
            'DEBIT',
            'YEARLY',
            'MANUALLY',
            'ENDED',
            10,
            NULL,
            1,
            'Seguro anual encerrado',
            'Seguros'
        )
) AS seed (
    amount,
    type,
    frequency_type,
    execution_type,
    status,
    day_one,
    day_two,
    month_of_the_year,
    description,
    category_name
)
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS wallet
    ON wallet.account_id = account.id
   AND wallet.name = 'Conta principal'
JOIN categories AS category
    ON category.name = seed.category_name
   AND category.type = seed.type
   AND category.system = TRUE;
