-- ============================================================
-- V6 - Tabelas de planos, assinaturas, pagamentos e webhooks
--
-- plans
-- subscriptions
-- payments
-- webhook_events
--
-- Dependências:
-- accounts
-- ============================================================


-- ============================================================
-- STEP 1: plans
--
-- Planos disponíveis no FinancePro.
--
-- FREE     -> R$ 0,00
-- PRO      -> R$ 14,99
-- PREMIUM  -> R$ 19,99
-- ============================================================

CREATE TABLE plans (
    id          UUID          NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(50)   NOT NULL,
    description VARCHAR(150),
    type        VARCHAR(50)   NOT NULL,
    price       NUMERIC(19, 2) NOT NULL,
    currency    VARCHAR(3)    NOT NULL,
    active      BOOLEAN       NOT NULL DEFAULT true,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,

    CONSTRAINT pk_plans
        PRIMARY KEY (id),

    CONSTRAINT uq_plans_type
        UNIQUE (type)
);

CREATE INDEX idx_plans_type
    ON plans (type);

CREATE INDEX idx_plans_active
    ON plans (active);


-- ============================================================
-- STEP 2: Seed inicial dos planos
-- ============================================================

INSERT INTO plans (
    id,
    name,
    description,
    type,
    price,
    currency,
    active,
    created_at,
    updated_at
)
VALUES
(
    gen_random_uuid(),
    'Free',
    'Plano gratuito do FinancePro',
    'FREE',
    0.00,
    'BRL',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'Pro',
    'Plano profissional do FinancePro',
    'PRO',
    14.99,
    'BRL',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'Premium',
    'Plano premium do FinancePro',
    'PREMIUM',
    19.99,
    'BRL',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- ============================================================
-- STEP 3: subscriptions
--
-- Uma conta possui uma única Subscription atual.
--
-- Um Plan pode estar associado a várias Subscriptions.
--
-- external_subscription_id:
-- Será utilizado futuramente com a assinatura recorrente
-- do Mercado Pago.
--
-- Atualmente permanece NULL.
-- ============================================================

CREATE TABLE subscriptions (
    id                       UUID          NOT NULL DEFAULT gen_random_uuid(),

    status                   VARCHAR(50)   NOT NULL,

    external_subscription_id VARCHAR(255),
    external_customer_id     VARCHAR(255),

    started_at               TIMESTAMP     NOT NULL,

    current_period_start     DATE          NOT NULL,
    current_period_end       DATE          NOT NULL,

    canceled_at              DATE,
    cancel_at_period_end     BOOLEAN       NOT NULL DEFAULT false,
    ended_at                 DATE,

    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP,

    account_id               UUID          NOT NULL,
    plan_id                  UUID          NOT NULL,

    CONSTRAINT pk_subscriptions
        PRIMARY KEY (id),

    CONSTRAINT uq_subscriptions_account_id
        UNIQUE (account_id),

    CONSTRAINT fk_subscriptions_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_subscriptions_plan
        FOREIGN KEY (plan_id)
        REFERENCES plans (id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_subscriptions_period
        CHECK (current_period_end >= current_period_start)
);

CREATE INDEX idx_subscriptions_account_id
    ON subscriptions (account_id);

CREATE INDEX idx_subscriptions_plan_id
    ON subscriptions (plan_id);

CREATE INDEX idx_subscriptions_status
    ON subscriptions (status);

CREATE INDEX idx_subscriptions_period_end
    ON subscriptions (current_period_end);

CREATE INDEX idx_subscriptions_external_subscription_id
    ON subscriptions (external_subscription_id);


-- ============================================================
-- STEP 4: payments
--
-- Cada Payment representa um pagamento registrado no FinancePro.
--
-- subscription_id:
-- Identifica a assinatura relacionada ao pagamento.
--
-- plan_id:
-- Identifica o plano referente ao pagamento.
--
-- Essa informação é mantida diretamente em Payment para preservar
-- o contexto histórico do plano no momento do pagamento.
--
-- Exemplo:
--
-- Payment #1
-- amount = 14.99
-- plan = PRO
--
-- Posteriormente:
--
-- Subscription.plan = PREMIUM
--
-- O Payment #1 continuará associado ao PRO.
-- ============================================================

CREATE TABLE payments (
    id                   UUID           NOT NULL DEFAULT gen_random_uuid(),

    external_preference_id  VARCHAR(255),
    external_payment_id  VARCHAR(255),
    status               VARCHAR(50)    NOT NULL,
    status_detail        VARCHAR(100),

    amount               NUMERIC(19, 2) NOT NULL,
    currency             VARCHAR(3)     NOT NULL,

    paid_at              TIMESTAMP,

    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,

    account_id           UUID           NOT NULL,
    plan_id              UUID           NOT NULL,
    subscription_id      UUID,

    CONSTRAINT pk_payments
        PRIMARY KEY (id),

    CONSTRAINT uq_payments_external_preference_id
        UNIQUE (external_preference_id),

    CONSTRAINT uq_payments_external_payment_id
        UNIQUE (external_payment_id),

    CONSTRAINT fk_payments_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payments_subscription
        FOREIGN KEY (subscription_id)
        REFERENCES subscriptions (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_payments_plan
        FOREIGN KEY (plan_id)
        REFERENCES plans (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_payments_external_payment_id
    ON payments (external_payment_id);

CREATE INDEX idx_payments_subscription_id
    ON payments (subscription_id);

CREATE INDEX idx_payments_plan_id
    ON payments (plan_id);

CREATE INDEX idx_payments_status
    ON payments (status);

CREATE INDEX idx_payments_paid_at
    ON payments (paid_at);

CREATE INDEX idx_payments_created_at
    ON payments (created_at);


-- ============================================================
-- STEP 5: webhook_events
--
-- Controle de idempotência dos eventos recebidos do Mercado Pago.
--
-- external_event_id:
-- Identificador único do evento recebido.
--
-- Se o Mercado Pago reenviar exatamente o mesmo evento,
-- external_event_id será igual e o FinancePro poderá impedir
-- o processamento duplicado.
-- ============================================================

CREATE TABLE webhook_events (
    id                UUID          NOT NULL DEFAULT gen_random_uuid(),

    external_event_id VARCHAR(255)  NOT NULL,

    type              VARCHAR(100)  NOT NULL,
    action            VARCHAR(100)  NOT NULL,

    received_at       TIMESTAMP WITH TIME ZONE
                      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    processed_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_webhook_events
        PRIMARY KEY (id),

    CONSTRAINT uq_webhook_events_external_event_id
        UNIQUE (external_event_id)
);

CREATE INDEX idx_webhook_events_type
    ON webhook_events (type);

CREATE INDEX idx_webhook_events_action
    ON webhook_events (action);

CREATE INDEX idx_webhook_events_received_at
    ON webhook_events (received_at);