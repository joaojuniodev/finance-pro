-- ============================================================
-- V2 - Tabela accounts
--
-- Depende de: users (FK user_id)
-- Representa a conta financeira do usuário.
-- Relacionamento OneToOne com users.
-- ============================================================

CREATE TABLE accounts (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    current_balance NUMERIC(19, 2),
    income          NUMERIC(19, 2),
    expenses        NUMERIC(19, 2),
    net_income      NUMERIC(19, 2),

    -- Maior categoria de gasto: FK para categories (definida na V3)
    -- nullable pois categories ainda não existe nesta migration
    biggest_expense_category_id UUID,
    biggest_expense_value       NUMERIC(19, 2),

    -- OneToOne com users — cada usuário tem exatamente uma conta
    user_id UUID,

    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT uq_accounts_user_id UNIQUE (user_id),
    CONSTRAINT fk_accounts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    -- FK biggest_expense_category_id → categories adicionada na V3
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);