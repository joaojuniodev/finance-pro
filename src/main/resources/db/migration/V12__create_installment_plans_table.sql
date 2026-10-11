CREATE TABLE installment_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255),
    total_value NUMERIC(38, 2),
    value NUMERIC(38, 2),
    times NUMERIC(38, 2),
    fees NUMERIC(38, 2),
    start_date DATE,
    end_date DATE,
    status VARCHAR(255),
    invoice_id UUID REFERENCES invoices (id),
    CONSTRAINT ck_installment_plans_status CHECK (
        status IN ('PAYING', 'INVOICED', 'PAID', 'EXPIRED')
    )
);

-- The card is reached through invoice_id; no wallet_id or join table.
CREATE INDEX idx_installment_plans_invoice ON installment_plans (invoice_id);
