-- Dates are relative to execution, so fixtures remain useful in any month/year.
-- Nubank: paid history + open current cycle.
-- Inter: overdue previous cycle.
-- C6: paid current cycle + distinct next-month invoice, as after bill payment.
WITH billing_dates AS (
    SELECT
        DATE_TRUNC('month', CURRENT_DATE)::DATE AS month_start,
        (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day')::DATE AS month_end
),
seed AS (
    SELECT
        'Nubank crédito' AS wallet_name,
        8000.00 AS credit_limit,
        'PAID' AS status,
        (month_start - INTERVAL '1 day')::DATE - 7 AS closing_date,
        7 AS days_until_expiration
    FROM billing_dates

    UNION ALL

    SELECT
        'Nubank crédito',
        8000.00,
        'PAYING',
        month_end - 7,
        7
    FROM billing_dates

    UNION ALL

    SELECT
        'Inter crédito',
        5000.00,
        'EXPIRED',
        (month_start - INTERVAL '1 month')::DATE + 19,
        7
    FROM billing_dates

    UNION ALL

    SELECT
        'C6 crédito',
        6000.00,
        'PAID',
        LEAST(CURRENT_DATE, month_start + 19),
        7
    FROM billing_dates

    UNION ALL

    SELECT
        'C6 crédito',
        6000.00,
        'PAYING',
        (month_start + INTERVAL '1 month')::DATE + 19,
        7
    FROM billing_dates
)
INSERT INTO invoices (
    credit_limit,
    status,
    closing_date,
    expiration_date,
    days_until_expiration,
    wallet_id,
    account_id
)
SELECT
    seed.credit_limit,
    seed.status,
    seed.closing_date,
    seed.closing_date + seed.days_until_expiration,
    seed.days_until_expiration,
    wallet.id,
    account.id
FROM seed
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS wallet
    ON wallet.account_id = account.id
   AND wallet.name = seed.wallet_name
   AND wallet.type = 'CREDIT_CARD';
