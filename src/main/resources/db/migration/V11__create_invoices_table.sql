CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "limit" NUMERIC(38, 2),
    status VARCHAR(255),
    closing_date DATE,
    expiration_date DATE,
    days_until_expiration INTEGER,
    wallet_id UUID REFERENCES wallets (id),
    CONSTRAINT ck_invoices_status CHECK (status IN ('PAYING', 'PAID', 'EXPIRED'))
);

-- One billing cycle per card and closing month.
-- "limit" matches Invoice.limit and must be quoted: LIMIT is a SQL keyword.
CREATE UNIQUE INDEX uk_invoices_wallet_closing_month
    ON invoices (
        wallet_id,
        EXTRACT(YEAR FROM closing_date),
        EXTRACT(MONTH FROM closing_date)
    );
