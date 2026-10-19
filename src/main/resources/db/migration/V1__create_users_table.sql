-- Fresh baseline for PostgreSQL 17. Apply only to an empty database/schema.
-- This replaces the previous migration history; do not repair an existing database
-- to force this baseline onto it. No existing data is deleted by these scripts.
-- PostgreSQL generates every seeded entity ID through gen_random_uuid().
-- Passwords need room for the complete encoded PBKDF2 hash, not only 12 characters.

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL,
    surname VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    email VARCHAR(255),
    recovery_email VARCHAR(255),
    completed_questionnaire_financial_life BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN,
    account_non_locked BOOLEAN,
    credentials_non_expired BOOLEAN,
    enabled BOOLEAN,
    CONSTRAINT uk_users_username UNIQUE (username)
);
