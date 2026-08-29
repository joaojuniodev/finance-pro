-- ============================================================
-- V4 - Tabelas transactions, recurrences e movements
--
-- Depende de: accounts  (FK account_id)
--             wallet    (FK from_wallet, to_wallet em movements)
--             categories (FK category_id em transactions)
-- ============================================================

-- ------------------------------------------------------------
-- transactions
-- Registros de entradas e saídas financeiras de uma conta
-- ------------------------------------------------------------
CREATE TABLE transactions (
    id            UUID          NOT NULL DEFAULT gen_random_uuid(),
    amount        NUMERIC(19, 2),
    description   VARCHAR(200)  NOT NULL,
    observation   VARCHAR(200),

    -- Enum TransactionType (ex: INCOME, EXPENSE)
    type          VARCHAR(50),
    -- Enum TransactionStatus (ex: COMPLETED, PENDING)
    status        VARCHAR(50),

    -- FK para categories no lugar do VARCHAR livre anterior
    category_id   UUID,

    registered_at TIMESTAMP,
    wallet_id     UUID,
    account_id    UUID          NOT NULL,

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_transactions_wallet_id     ON transactions (wallet_id);
CREATE INDEX idx_transactions_account_id    ON transactions (account_id);
CREATE INDEX idx_transactions_category_id   ON transactions (category_id);
CREATE INDEX idx_transactions_registered_at ON transactions (registered_at);

-- ------------------------------------------------------------
-- recurrences
-- Despesas ou receitas que se repetem periodicamente
-- (ex: aluguel todo mês, salário quinzenal)
-- ------------------------------------------------------------
CREATE TABLE recurrences (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid(),
    amount              NUMERIC(19, 2),

    -- Enum RecurrenceType (ex: FIXED, VARIABLE)
    type                VARCHAR(50),

    -- Enum FrequencyType (ex: DAILY, WEEKLY, MONTHLY, YEARLY)
    frequency_type  VARCHAR(50),

    -- Enum ExecutionType (ex: AUTOMATIC, MANUALLY)
    execution_type  VARCHAR(50),

    -- Enum RecurrenceStatus (ex: ACTIVE, PAUSED, ENDED)
    status          VARCHAR(50),

    -- day_one e day_two: dias do mês/semana de cobrança
    -- Exemplo: cobrança nos dias 5 e 20 do mês
    day_one             INTEGER,
    day_two             INTEGER,

    -- Para recorrências anuais: qual mês do ano
    month_of_the_year   INTEGER,

    description         VARCHAR(200),

    -- Controle de execução da recorrência (job/scheduler)
    next_execution_date DATE,
    last_execution_date DATE,

    -- Indica se a recorrência ainda está ativa (pode ser pausada/cancelada)
    active              BOOLEAN       NOT NULL DEFAULT true,

    wallet_id           UUID          NOT NULL,

    category_id         UUID          NOT NULL,

    account_id          UUID          NOT NULL,

    CONSTRAINT pk_recurrences PRIMARY KEY (id),
    CONSTRAINT fk_recurrences_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurrences_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurrences_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE INDEX idx_recurrences_account_id            ON recurrences (account_id);
CREATE INDEX idx_recurrences_WALLET_id             ON recurrences (wallet_id);
CREATE INDEX idx_recurrences_category_id           ON recurrences (category_id);
CREATE INDEX idx_recurrences_next_execution_date   ON recurrences (next_execution_date);
CREATE INDEX idx_recurrences_active                ON recurrences (active);

-- ------------------------------------------------------------
-- movements
-- Transferências entre wallets do mesmo usuário
-- from_wallet → to_wallet com determinado amount
-- ------------------------------------------------------------
CREATE TABLE movements (
    id            UUID          NOT NULL DEFAULT gen_random_uuid(),
    amount        NUMERIC(19, 2),
    from_wallet   UUID,
    to_wallet     UUID,
    registered_at TIMESTAMP,

    CONSTRAINT pk_movements PRIMARY KEY (id),
    CONSTRAINT fk_movements_from_wallet
        FOREIGN KEY (from_wallet) REFERENCES wallets (id) ON DELETE SET NULL,
    CONSTRAINT fk_movements_to_wallet
        FOREIGN KEY (to_wallet) REFERENCES wallets (id) ON DELETE SET NULL
);

CREATE INDEX idx_movements_from_wallet   ON movements (from_wallet);
CREATE INDEX idx_movements_to_wallet     ON movements (to_wallet);
CREATE INDEX idx_movements_registered_at ON movements (registered_at);

ALTER TABLE transactions ADD COLUMN recurrence_id UUID NULL;

ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_recurrence
FOREIGN KEY (recurrence_id)
REFERENCES recurrences(id);