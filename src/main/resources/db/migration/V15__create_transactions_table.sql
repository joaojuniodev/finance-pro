CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(38, 2),
    description VARCHAR(200) NOT NULL,
    observation VARCHAR(200),
    type VARCHAR(255),
    status VARCHAR(255),
    registered_at TIMESTAMP,
    category_id UUID REFERENCES categories (id),
    recurrence_id UUID REFERENCES recurrences (id),
    wallet_id UUID REFERENCES wallets (id),
    account_id UUID REFERENCES accounts (id),
    CONSTRAINT ck_transactions_type CHECK (type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT ck_transactions_status CHECK (status IN ('COMPLETED', 'PENDING'))
);

CREATE INDEX idx_transactions_account_registered_at ON transactions (account_id, registered_at);
CREATE INDEX idx_transactions_category ON transactions (category_id);
CREATE INDEX idx_transactions_recurrence ON transactions (recurrence_id);
CREATE INDEX idx_transactions_wallet ON transactions (wallet_id);
