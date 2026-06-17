-- ============================================================
-- V3 - Tabelas wallet e goals
--
-- Depende de: accounts (FK account_id em wallet)
-- ============================================================

-- ------------------------------------------------------------
-- STEP 1: wallet sem goal_id ainda
-- Depende de accounts
-- ------------------------------------------------------------
CREATE TABLE wallet (
    id          UUID           NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(150),
    description VARCHAR(255),
    balance     NUMERIC(19, 2),
    account_id  UUID           NOT NULL,

    CONSTRAINT pk_wallet PRIMARY KEY (id),
    CONSTRAINT fk_wallet_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- STEP 2: goals
-- Depende de wallet (FK wallet_id)
-- goal monitora o progresso de uma wallet específica
-- ------------------------------------------------------------
CREATE TABLE goals (
    id             UUID           NOT NULL DEFAULT gen_random_uuid(),
    name           VARCHAR(150),
    description    VARCHAR(255),
    total_amount   NUMERIC(19, 2),
    current_amount NUMERIC(19, 2),

    -- Enum Category armazenado como string
    category       VARCHAR(100),

    CONSTRAINT pk_goals PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- STEP 3: adiciona goal_id em wallet agora que goals existe
-- Nullable pois uma wallet pode existir sem meta associada
-- UniqueConstraint pois é OneToOne (uma goal por wallet)
-- ------------------------------------------------------------
ALTER TABLE wallet
    ADD COLUMN goal_id UUID,
    ADD CONSTRAINT uq_wallet_goal_id UNIQUE (goal_id),
    ADD CONSTRAINT fk_wallet_goal
        FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE SET NULL;

-- ------------------------------------------------------------
-- Índices de performance
-- ------------------------------------------------------------
CREATE INDEX idx_wallet_account_id ON wallet (account_id);
CREATE INDEX idx_wallet_goal_id    ON wallet (goal_id);
