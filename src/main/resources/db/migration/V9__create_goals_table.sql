CREATE TABLE goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description VARCHAR(255),
    total_amount NUMERIC(38, 2),
    current_amount NUMERIC(38, 2),
    category_id UUID REFERENCES categories (id)
);

CREATE INDEX idx_goals_category ON goals (category_id);
