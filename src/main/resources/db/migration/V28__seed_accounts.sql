-- Snapshot of the completed transactions and wallet movements seeded below.
-- Pending transactions do not enter these totals. The test account stays empty.
INSERT INTO accounts (
    current_balance,
    income,
    expenses,
    net_income,
    biggest_expense_category_id,
    biggest_expense_value,
    user_id
)
SELECT
    seed.current_balance,
    seed.income,
    seed.expenses,
    seed.net_income,
    category.id,
    seed.biggest_expense_value,
    app_user.id
FROM (
    VALUES
        (
            'demo',
            7200.10,
            9610.00,
            2409.90,
            7200.10,
            'Moradia',
            1800.00
        ),
        (
            'joaojunio',
            0.00,
            0.00,
            0.00,
            0.00,
            NULL,
            0.00
        ),
        (
            'mediano',
            400.00,
            4000.00,
            3600.00,
            400.00,
            'Moradia',
            1600.00
        ),
        (
            'ruim',
            -400.00,
            2500.00,
            2900.00,
            -400.00,
            'Moradia',
            1000.00
        )
) AS seed (
    username,
    current_balance,
    income,
    expenses,
    net_income,
    biggest_expense_category_name,
    biggest_expense_value
)
JOIN users AS app_user
    ON app_user.username = seed.username
LEFT JOIN categories AS category
    ON category.name = seed.biggest_expense_category_name
   AND category.type = 'DEBIT'
   AND category.system = TRUE;
