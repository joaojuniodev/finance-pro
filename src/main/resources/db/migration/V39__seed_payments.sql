-- Billing of the Capto subscription, not credit-card invoice settlements.
-- The approved payment is a mock provider record; no external request is made.
INSERT INTO payments (
    external_preference_id,
    external_payment_id,
    status,
    status_detail,
    amount,
    currency,
    paid_at,
    created_at,
    updated_at,
    account_id,
    plan_id,
    subscription_id
)
SELECT
    seed.preference_id,
    seed.payment_id,
    seed.status,
    seed.status_detail,
    plan.price,
    plan.currency,
    CASE WHEN seed.status = 'APPROVED' THEN CURRENT_TIMESTAMP END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    account.id,
    plan.id,
    subscription.id
FROM (
    VALUES
        (
            'mock-preference-demo-approved',
            'mock-payment-demo-approved',
            'APPROVED',
            'ACCREDITED'
        ),
        (
            'mock-preference-demo-rejected',
            'mock-payment-demo-rejected',
            'REJECTED',
            'REJECTED_INSUFFICIENT_AMOUNT'
        )
) AS seed (
    preference_id,
    payment_id,
    status,
    status_detail
)
JOIN users AS app_user
    ON app_user.username = 'demo'
JOIN accounts AS account
    ON account.user_id = app_user.id
JOIN subscriptions AS subscription
    ON subscription.account_id = account.id
JOIN plans AS plan
    ON plan.id = subscription.plan_id;
