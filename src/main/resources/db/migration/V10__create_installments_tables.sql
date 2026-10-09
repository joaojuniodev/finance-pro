CREATE TABLE installment_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255),
    total_value NUMERIC(19, 2),
    value NUMERIC(19, 2),
    times NUMERIC(19, 2),
    fees NUMERIC(19, 2),
    start_date DATE,
    end_date DATE,
    status VARCHAR(255),
    wallet_id UUID REFERENCES wallets(id)
);

CREATE TABLE installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    value NUMERIC(19, 2),
    is_paid BOOLEAN,
    month INTEGER,
    year INTEGER,
    installment_plan_id UUID NOT NULL REFERENCES installment_plans(id)
);
