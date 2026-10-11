CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_preference_id VARCHAR(255),
    external_payment_id VARCHAR(255),
    status VARCHAR(255),
    status_detail VARCHAR(255),
    amount NUMERIC(38, 2),
    currency VARCHAR(255),
    paid_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    account_id UUID REFERENCES accounts (id),
    plan_id UUID NOT NULL REFERENCES plans (id),
    subscription_id UUID REFERENCES subscriptions (id),
    CONSTRAINT ck_payments_status CHECK (
        status IN (
            'PENDING', 'APPROVED', 'AUTHORIZED', 'IN_PROCESS', 'IN_MEDIATION',
            'REJECTED', 'CANCELLED', 'REFUNDED', 'CHARGED_BACK'
        )
    ),
    CONSTRAINT ck_payments_status_detail CHECK (
        status_detail IN (
            'ACCREDITED', 'PENDING_REVIEW', 'PENDING_WAITING_PAYMENT',
            'PENDING_CONTINGENCY', 'REJECTED_HIGH_RISK', 'REJECTED_INVALID_INSTALLMENTS',
            'REJECTED_INVALID_CARD', 'REJECTED_INSUFFICIENT_AMOUNT',
            'REJECTED_CALL_FOR_AUTHORIZE', 'REJECTED_OTHER_REASON', 'REFUNDED', 'CHARGED_BACK'
        )
    )
);

CREATE INDEX idx_payments_account ON payments (account_id);
CREATE INDEX idx_payments_plan ON payments (plan_id);
CREATE INDEX idx_payments_subscription ON payments (subscription_id);
