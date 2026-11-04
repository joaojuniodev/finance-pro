-- External identifiers are deliberately fictitious, not provider resources.
INSERT INTO subscriptions (
    status,
    external_subscription_id,
    external_customer_id,
    started_at,
    current_period_start,
    current_period_end,
    canceled_at,
    cancel_at_period_end,
    ended_at,
    created_at,
    updated_at,
    account_id,
    plan_id
)
SELECT
    'ACTIVE',
    'mock-subscription-demo',
    'mock-customer-demo',
    CURRENT_TIMESTAMP,
    CURRENT_DATE,
    (CURRENT_DATE + INTERVAL '1 month')::DATE,
    NULL,
    FALSE,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    account.id,
    plan.id
FROM users AS app_user
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN plans AS plan
    ON plan.type = 'PRO'
WHERE app_user.username = 'demo';
