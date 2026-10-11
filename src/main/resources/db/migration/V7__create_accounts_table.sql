CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    current_balance NUMERIC(38, 2),
    income NUMERIC(38, 2),
    expenses NUMERIC(38, 2),
    net_income NUMERIC(38, 2),
    biggest_expense_category_id UUID,
    biggest_expense_value NUMERIC(38, 2),
    user_id UUID REFERENCES users (id),
    CONSTRAINT uk_accounts_user UNIQUE (user_id)
);
