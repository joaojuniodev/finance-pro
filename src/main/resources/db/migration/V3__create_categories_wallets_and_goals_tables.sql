CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    type VARCHAR(255),
    icon VARCHAR(255),
    color VARCHAR(255),
    system BOOLEAN,
    account_id UUID REFERENCES accounts(id)
);

ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_biggest_category
    FOREIGN KEY (biggest_expense_category_id) REFERENCES categories(id);

CREATE TABLE goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description VARCHAR(255),
    total_amount NUMERIC(19, 2),
    current_amount NUMERIC(19, 2),
    category_id UUID REFERENCES categories(id)
);

CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description VARCHAR(255),
    balance NUMERIC(19, 2),
    card_digits VARCHAR(4),
    type VARCHAR(255),
    closing_date DATE,
    expiration_date DATE,
    days_until_expiration INTEGER,
    color VARCHAR(255),
    goal_id UUID UNIQUE REFERENCES goals(id),
    bank_id UUID REFERENCES banks(id),
    account_id UUID REFERENCES accounts(id)
);
