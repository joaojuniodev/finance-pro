-- Generate every installment, including future months.
-- The final installment absorbs rounding (e.g. 299.99 / 3 = 100 + 100 + 99.99).
-- Curso de programação has its current installment already paid individually.
-- Other open plans have paid past installments and unpaid current/future ones.
INSERT INTO installments (
    value,
    is_paid,
    month,
    year,
    installment_plan_id,
    account_id
)
SELECT
    CASE
        WHEN sequence.number = plan.times::INTEGER - 1 THEN
            plan.total_value - amounts.base_value * (plan.times::INTEGER - 1)
        ELSE amounts.base_value
    END,
    CASE
        WHEN plan.status = 'PAID' THEN TRUE
        WHEN plan.description = 'Curso de programação' THEN
            dates.installment_month <= DATE_TRUNC('month', invoice.closing_date)::DATE
        ELSE
            dates.installment_month < DATE_TRUNC('month', invoice.closing_date)::DATE
    END,
    EXTRACT(MONTH FROM dates.installment_month)::INTEGER,
    EXTRACT(YEAR FROM dates.installment_month)::INTEGER,
    plan.id,
    plan.account_id
FROM installment_plans AS plan
JOIN invoices AS invoice
    ON invoice.id = plan.invoice_id
JOIN wallets AS wallet
    ON wallet.id = invoice.wallet_id
JOIN accounts AS account
    ON account.id = wallet.account_id
JOIN users AS app_user
    ON app_user.id = account.user_id
   AND app_user.username = 'demo'
CROSS JOIN LATERAL GENERATE_SERIES(0, plan.times::INTEGER - 1) AS sequence(number)
CROSS JOIN LATERAL (
    SELECT ROUND(plan.total_value / plan.times, 2) AS base_value
) AS amounts
CROSS JOIN LATERAL (
    SELECT
        (plan.start_date + MAKE_INTERVAL(months => sequence.number))::DATE AS installment_month
) AS dates;
