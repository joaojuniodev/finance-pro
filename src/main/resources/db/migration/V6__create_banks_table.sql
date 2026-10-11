CREATE TABLE banks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    gradient VARCHAR(180) NOT NULL,
    shadow VARCHAR(120) NOT NULL,
    CONSTRAINT uk_banks_name UNIQUE (name)
);
