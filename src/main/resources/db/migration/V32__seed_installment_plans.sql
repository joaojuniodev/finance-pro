-- value is the purchase price before interest; total_value includes fees (%).
-- Invoice/start offsets are measured from the month in which migrations run.
INSERT INTO installment_plans (
    description,
    total_value,
    value,
    times,
    fees,
    start_date,
    end_date,
    status,
    invoice_id,
    account_id
)
SELECT
    seed.description,
    ROUND(seed.value * (1 + seed.fees / 100.00), 2),
    seed.value,
    seed.times,
    seed.fees,
    dates.start_date,
    (dates.start_date + MAKE_INTERVAL(months => seed.times - 1))::DATE,
    seed.status,
    invoice.id,
    invoice.account_id
FROM (
    VALUES
        (
            'Teclado mecânico',
            270.00,
            3,
            0.00,
            -3,
            'PAID',
            'Nubank crédito',
            -1
        ),
        (
            'Celular',
            2400.00,
            12,
            0.00,
            -2,
            'PAYING',
            'Nubank crédito',
            0
        ),
        (
            'Notebook com juros',
            3000.00,
            10,
            10.00,
            0,
            'INVOICED',
            'Nubank crédito',
            0
        ),
        (
            'Curso de programação',
            600.00,
            3,
            0.00,
            -1,
            'PAYING',
            'Nubank crédito',
            0
        ),
        (
            'Fones de ouvido',
            299.99,
            3,
            0.00,
            0,
            'INVOICED',
            'Nubank crédito',
            0
        ),
        (
            'Conserto do carro',
            900.00,
            3,
            0.00,
            -2,
            'EXPIRED',
            'Inter crédito',
            -1
        ),
        (
            'Monitor',
            600.00,
            3,
            0.00,
            -2,
            'PAID',
            'C6 crédito',
            0
        ),
        (
            'Viagem de férias',
            1800.00,
            6,
            0.00,
            1,
            'INVOICED',
            'C6 crédito',
            1
        )
) AS seed (
    description,
    value,
    times,
    fees,
    start_month_offset,
    status,
    wallet_name,
    invoice_month_offset
)
CROSS JOIN LATERAL (
    SELECT
        (DATE_TRUNC('month', CURRENT_DATE)
            + MAKE_INTERVAL(months => seed.start_month_offset))::DATE AS start_date,
        (DATE_TRUNC('month', CURRENT_DATE)
            + MAKE_INTERVAL(months => seed.invoice_month_offset))::DATE AS invoice_month
) AS dates
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN wallets AS wallet
    ON wallet.account_id = account.id
   AND wallet.name = seed.wallet_name
JOIN invoices AS invoice
    ON invoice.wallet_id = wallet.id
   AND DATE_TRUNC('month', invoice.closing_date)::DATE = dates.invoice_month;
