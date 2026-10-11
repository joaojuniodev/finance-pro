CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    type VARCHAR(255),
    icon VARCHAR(255),
    color VARCHAR(255),
    system BOOLEAN,
    account_id UUID REFERENCES accounts (id),
    CONSTRAINT ck_categories_type CHECK (type IN ('CREDIT', 'DEBIT'))
);

-- Accounts and categories reference each other; complete the FK after both exist.
ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_biggest_expense_category
    FOREIGN KEY (biggest_expense_category_id) REFERENCES categories (id);

CREATE INDEX idx_categories_account ON categories (account_id);
CREATE INDEX idx_accounts_biggest_expense_category ON accounts (biggest_expense_category_id);
