CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50),
    description VARCHAR(150),
    type VARCHAR(255),
    price NUMERIC(19, 2),
    currency VARCHAR(255),
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

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
    account_id UUID UNIQUE REFERENCES accounts(id),
    plan_id UUID NOT NULL REFERENCES plans(id)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_preference_id VARCHAR(255),
    external_payment_id VARCHAR(255),
    status VARCHAR(255),
    status_detail VARCHAR(255),
    amount NUMERIC(19, 2),
    currency VARCHAR(255),
    paid_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    account_id UUID REFERENCES accounts(id),
    plan_id UUID NOT NULL REFERENCES plans(id),
    subscription_id UUID REFERENCES subscriptions(id)
);
