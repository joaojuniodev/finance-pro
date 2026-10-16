CREATE TABLE installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    value NUMERIC(38, 2),
    is_paid BOOLEAN,
    month INTEGER,
    year INTEGER,
    installment_plan_id UUID REFERENCES installment_plans (id),
    account_id UUID REFERENCES accounts (id),
    CONSTRAINT ck_installments_month CHECK (month BETWEEN 1 AND 12)
);

CREATE INDEX idx_installments_plan_billing_cycle
    ON installments (installment_plan_id, year, month);

CREATE INDEX idx_installments_account_billing_cycle
    ON installments (account_id, year, month);
