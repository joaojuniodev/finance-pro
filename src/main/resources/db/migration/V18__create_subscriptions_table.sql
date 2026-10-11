CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(255),
    external_subscription_id VARCHAR(255),
    external_customer_id VARCHAR(255),
    started_at TIMESTAMP NOT NULL,
    current_period_start DATE NOT NULL,
    current_period_end DATE NOT NULL,
    canceled_at DATE,
    cancel_at_period_end BOOLEAN,
    ended_at DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    account_id UUID REFERENCES accounts (id),
    plan_id UUID NOT NULL REFERENCES plans (id),
    CONSTRAINT uk_subscriptions_account UNIQUE (account_id),
    CONSTRAINT ck_subscriptions_status CHECK (status IN ('ACTIVE', 'PENDING', 'PAUSED', 'EXPIRED'))
);

CREATE INDEX idx_subscriptions_plan ON subscriptions (plan_id);
