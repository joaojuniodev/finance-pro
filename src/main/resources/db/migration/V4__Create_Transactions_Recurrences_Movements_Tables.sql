-- ============================================================
-- V4 - Tabelas transactions, recurrences e movements
--
-- Depende de: accounts (FK account_id)
--             wallet   (FK from_wallet, to_wallet em movements)
-- ============================================================

-- ------------------------------------------------------------
-- transactions
-- Registros de entradas e saídas financeiras de uma conta
-- ------------------------------------------------------------
CREATE TABLE transactions (
    id            UUID           NOT NULL DEFAULT gen_random_uuid(),
    amount        NUMERIC(19, 2),

    -- Enum TransactionType (ex: INCOME, EXPENSE)
    -- Armazenado como VARCHAR pois não há @Enumerated(STRING)
    -- explícito, mas seguimos a convenção do projeto
    type          VARCHAR(50),

    -- Enum Category com @Enumerated(EnumType.STRING)
    category      VARCHAR(100),

    registered_at TIMESTAMP,
    account_id    UUID           NOT NULL,

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

CREATE INDEX idx_transactions_account_id    ON transactions (account_id);
CREATE INDEX idx_transactions_registered_at ON transactions (registered_at);

-- ------------------------------------------------------------
-- recurrences
-- Despesas ou receitas que se repetem periodicamente
-- (ex: aluguel todo mês, salário quinzenal)
-- ------------------------------------------------------------
CREATE TABLE recurrences (
    id                UUID           NOT NULL DEFAULT gen_random_uuid(),
    amount            NUMERIC(19, 2),

    -- Enum RecurrenceType (ex: FIXED, VARIABLE)
    type              VARCHAR(50),

    -- Enum BillingTimeType (ex: DAILY, WEEKLY, MONTHLY, YEARLY)
    billing_time_type VARCHAR(50),

    -- day_one e day_two: dias do mês/semana de cobrança
    -- Exemplo: cobrança nos dias 5 e 20 do mês
    day_one           INTEGER,
    day_two           INTEGER,

    -- Para recorrências anuais: qual mês do ano
    month_of_the_year INTEGER,

    account_id        UUID           NOT NULL,

    CONSTRAINT pk_recurrences PRIMARY KEY (id),
    CONSTRAINT fk_recurrences_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

CREATE INDEX idx_recurrences_account_id ON recurrences (account_id);

-- ------------------------------------------------------------
-- movements
-- Transferências entre wallets do mesmo usuário
-- from_wallet → to_wallet com determinado amount
-- ------------------------------------------------------------
CREATE TABLE movements (
    id            UUID           NOT NULL DEFAULT gen_random_uuid(),
    amount        NUMERIC(19, 2),
    from_wallet   UUID,
    to_wallet     UUID,
    registered_at TIMESTAMP,

    CONSTRAINT pk_movements PRIMARY KEY (id),
    CONSTRAINT fk_movements_from_wallet
        FOREIGN KEY (from_wallet) REFERENCES wallet (id) ON DELETE SET NULL,
    CONSTRAINT fk_movements_to_wallet
        FOREIGN KEY (to_wallet) REFERENCES wallet (id) ON DELETE SET NULL
);

-- Índices para consultas de extrato por wallet
CREATE INDEX idx_movements_from_wallet   ON movements (from_wallet);
CREATE INDEX idx_movements_to_wallet     ON movements (to_wallet);
CREATE INDEX idx_movements_registered_at ON movements (registered_at);
