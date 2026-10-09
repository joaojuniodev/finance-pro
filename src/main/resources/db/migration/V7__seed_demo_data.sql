INSERT INTO roles (id, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'ROLE_USER');

INSERT INTO users (
    id, username, password, full_name, email,
    account_non_expired, account_non_locked,
    credentials_non_expired, enabled
)
VALUES
    (
        '00000000-0000-0000-0000-000000000101',
        'joaojunio',
        '{pbkdf2}21ccfa8516f6db6b18025688331fb0e64bf187da08cea8e0f7a3b904758643e6ccc265f1b4b477a5',
        'João Junio',
        'joaojunio@example.com',
        TRUE, TRUE, TRUE, TRUE
    ),
    (
        '00000000-0000-0000-0000-000000000102',
        'demo',
        '{pbkdf2}981e44a7480e0491f7cf5b937419e125842a9d7db45e23ee40d1f46c643531ac8e3421cb17dd5b9f',
        'Usuário de demonstração',
        'demo@example.com',
        TRUE, TRUE, TRUE, TRUE
    );

INSERT INTO user_role (user_id, role_id)
VALUES
    ('00000000-0000-0000-0000-000000000101', '00000000-0000-0000-0000-000000000001'),
    ('00000000-0000-0000-0000-000000000102', '00000000-0000-0000-0000-000000000001');

INSERT INTO banks (id, name, icon, color, gradient, shadow)
VALUES (
    '00000000-0000-0000-0000-000000000201',
    'Banco FinancePro', 'bank', '#2563EB',
    'linear-gradient(135deg,#2563EB,#1D4ED8)',
    'rgba(37,99,235,.25)'
);

INSERT INTO plans (id, name, description, type, price, currency, active, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000301',
    'Plano Pro', 'Plano de demonstração', 'PRO', 0, 'BRL',
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO accounts (id, current_balance, income, expenses, net_income, user_id)
VALUES (
    '00000000-0000-0000-0000-000000000401',
    3500.00, 6500.00, 3000.00, 3500.00,
    '00000000-0000-0000-0000-000000000101'
);

INSERT INTO categories (id, name, type, icon, color, system, account_id)
VALUES
    ('00000000-0000-0000-0000-000000000501', 'Salário', 'CREDIT', 'briefcase', '#16A34A', FALSE, '00000000-0000-0000-0000-000000000401'),
    ('00000000-0000-0000-0000-000000000502', 'Moradia', 'DEBIT', 'home', '#DC2626', FALSE, '00000000-0000-0000-0000-000000000401'),
    ('00000000-0000-0000-0000-000000000503', 'Alimentação', 'DEBIT', 'utensils', '#F59E0B', FALSE, '00000000-0000-0000-0000-000000000401');

UPDATE accounts
SET biggest_expense_category_id = '00000000-0000-0000-0000-000000000502',
    biggest_expense_value = 1800.00
WHERE id = '00000000-0000-0000-0000-000000000401';

INSERT INTO wallets (id, name, description, balance, type, color, bank_id, account_id)
VALUES (
    '00000000-0000-0000-0000-000000000601',
    'Conta principal', 'Carteira principal', 3500.00, 'CHECKING', '#2563EB',
    '00000000-0000-0000-0000-000000000201',
    '00000000-0000-0000-0000-000000000401'
);

INSERT INTO recurrences (
    id, amount, type, frequency_type, execution_type, status,
    day_one, next_execution_date, description, category_id, wallet_id, account_id
)
VALUES (
    '00000000-0000-0000-0000-000000000701',
    1800.00, 'DEBIT', 'MONTHLY', 'AUTOMATIC', 'ACTIVE', 5,
    (CURRENT_DATE + INTERVAL '1 month')::date,
    'Aluguel mensal',
    '00000000-0000-0000-0000-000000000502',
    '00000000-0000-0000-0000-000000000601',
    '00000000-0000-0000-0000-000000000401'
);

INSERT INTO transactions (
    id, amount, description, observation, type, status,
    registered_at, category_id, recurrence_id, wallet_id, account_id
)
VALUES
    (
        '00000000-0000-0000-0000-000000000801', 6500.00,
        'Salário de outubro', 'Recebimento mensal', 'CREDIT', 'COMPLETED',
        CURRENT_TIMESTAMP - INTERVAL '5 days',
        '00000000-0000-0000-0000-000000000501', NULL,
        '00000000-0000-0000-0000-000000000601',
        '00000000-0000-0000-0000-000000000401'
    ),
    (
        '00000000-0000-0000-0000-000000000802', 1800.00,
        'Aluguel de outubro', 'Despesa recorrente', 'DEBIT', 'COMPLETED',
        CURRENT_TIMESTAMP - INTERVAL '3 days',
        '00000000-0000-0000-0000-000000000502',
        '00000000-0000-0000-0000-000000000701',
        '00000000-0000-0000-0000-000000000601',
        '00000000-0000-0000-0000-000000000401'
    ),
    (
        '00000000-0000-0000-0000-000000000803', 1200.00,
        'Supermercado', 'Compras do mês', 'DEBIT', 'COMPLETED',
        CURRENT_TIMESTAMP - INTERVAL '2 days',
        '00000000-0000-0000-0000-000000000503', NULL,
        '00000000-0000-0000-0000-000000000601',
        '00000000-0000-0000-0000-000000000401'
    );

INSERT INTO subscriptions (
    id, status, started_at, current_period_start, current_period_end,
    cancel_at_period_end, created_at, updated_at, account_id, plan_id
)
VALUES (
    '00000000-0000-0000-0000-000000000901', 'ACTIVE', CURRENT_TIMESTAMP,
    CURRENT_DATE, (CURRENT_DATE + INTERVAL '1 month')::date, FALSE,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    '00000000-0000-0000-0000-000000000401',
    '00000000-0000-0000-0000-000000000301'
);
