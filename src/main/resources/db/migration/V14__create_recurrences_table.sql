CREATE TABLE recurrences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(38, 2),
    type VARCHAR(255),
    frequency_type VARCHAR(255),
    execution_type VARCHAR(255),
    status VARCHAR(255),
    day_one INTEGER,
    day_two INTEGER,
    month_of_the_year INTEGER,
    next_execution_date DATE,
    last_execution_date DATE,
    description VARCHAR(200) NOT NULL,
    category_id UUID REFERENCES categories (id),
    wallet_id UUID REFERENCES wallets (id),
    account_id UUID REFERENCES accounts (id),
    CONSTRAINT ck_recurrences_type CHECK (type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT ck_recurrences_frequency CHECK (frequency_type IN ('MONTHLY', 'BIWEEKLY', 'YEARLY')),
    CONSTRAINT ck_recurrences_execution CHECK (execution_type IN ('AUTOMATIC', 'MANUALLY')),
    CONSTRAINT ck_recurrences_status CHECK (status IN ('ACTIVE', 'PAUSED', 'ENDED'))
);

CREATE INDEX idx_recurrences_account ON recurrences (account_id);
CREATE INDEX idx_recurrences_wallet ON recurrences (wallet_id);
CREATE INDEX idx_recurrences_category ON recurrences (category_id);
