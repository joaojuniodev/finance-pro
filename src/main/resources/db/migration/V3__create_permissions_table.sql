CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uk_permissions_name UNIQUE (name)
);
