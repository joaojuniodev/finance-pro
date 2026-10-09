CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    current_balance NUMERIC(19, 2),
    income NUMERIC(19, 2),
    expenses NUMERIC(19, 2),
    net_income NUMERIC(19, 2),
    biggest_expense_category_id UUID,
    biggest_expense_value NUMERIC(19, 2),
    user_id UUID UNIQUE REFERENCES users(id)
);

CREATE TABLE banks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    gradient VARCHAR(180) NOT NULL,
    shadow VARCHAR(120) NOT NULL
);
