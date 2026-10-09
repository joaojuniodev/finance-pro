CREATE TABLE recurrences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(19, 2),
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
    category_id UUID REFERENCES categories(id),
    wallet_id UUID REFERENCES wallets(id),
    account_id UUID REFERENCES accounts(id)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(19, 2),
    description VARCHAR(200) NOT NULL,
    observation VARCHAR(200),
    type VARCHAR(255),
    status VARCHAR(255),
    registered_at TIMESTAMP,
    category_id UUID REFERENCES categories(id),
    recurrence_id UUID REFERENCES recurrences(id),
    wallet_id UUID REFERENCES wallets(id),
    account_id UUID REFERENCES accounts(id)
);

CREATE TABLE movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(19, 2),
    from_wallet UUID REFERENCES wallets(id),
    to_wallet UUID REFERENCES wallets(id),
    registered_at TIMESTAMP
);
