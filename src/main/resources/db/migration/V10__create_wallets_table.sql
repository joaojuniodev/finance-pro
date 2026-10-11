CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description VARCHAR(255),
    balance NUMERIC(38, 2),
    card_digits VARCHAR(4),
    type VARCHAR(255),
    color VARCHAR(255),
    goal_id UUID REFERENCES goals (id),
    bank_id UUID REFERENCES banks (id),
    account_id UUID REFERENCES accounts (id),
    CONSTRAINT uk_wallets_goal UNIQUE (goal_id),
    CONSTRAINT ck_wallets_type CHECK (
        type IN ('CHECKING', 'SAVING', 'CREDIT_CARD', 'RESERVE', 'INVESTMENTS', 'PHYSICAL', 'OTHER')
    )
);

-- Billing dates and limits belong to invoices, not wallets.
CREATE INDEX idx_wallets_account ON wallets (account_id);
CREATE INDEX idx_wallets_bank ON wallets (bank_id);
