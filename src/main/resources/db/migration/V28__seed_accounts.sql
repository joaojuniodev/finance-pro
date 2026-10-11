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
    CASE WHEN app_user.username = 'demo' THEN 7200.10 ELSE 0.00 END,
    CASE WHEN app_user.username = 'demo' THEN 9610.00 ELSE 0.00 END,
    CASE WHEN app_user.username = 'demo' THEN 2409.90 ELSE 0.00 END,
    CASE WHEN app_user.username = 'demo' THEN 7200.10 ELSE 0.00 END,
    CASE WHEN app_user.username = 'demo' THEN (
        SELECT id
        FROM categories
        WHERE name = 'Moradia'
          AND type = 'DEBIT'
          AND system = TRUE
    ) END,
    CASE WHEN app_user.username = 'demo' THEN 1800.00 ELSE 0.00 END,
    app_user.id
FROM users AS app_user
WHERE app_user.username IN ('demo', 'joaojunio');
