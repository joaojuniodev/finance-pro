CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50),
    description VARCHAR(150),
    type VARCHAR(255),
    price NUMERIC(38, 2),
    currency VARCHAR(255),
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT ck_plans_type CHECK (type IN ('FREE', 'PRO', 'PREMIUM'))
);
