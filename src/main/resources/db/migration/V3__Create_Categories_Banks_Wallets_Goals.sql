-- ============================================================
-- V3 - Tabela categories + banks + wallets + goals
--
-- Depende de: accounts (FK account_id em categories e wallets)
--
-- Ordem:
--   1. categories  (depende de accounts)
--   2. Seed das categorias padrao do sistema
--   3. FK retroativa de accounts -> categories
--   4. banks       (catalogo do sistema)
--   5. Seed dos bancos mais usados no Brasil
--   6. wallets     (depende de accounts e banks)
--   7. goals       (depende de categories)
--   8. goal_id em wallets (referencia circular resolvida apos goals)
-- ============================================================

-- ------------------------------------------------------------
-- STEP 1: categories
-- ------------------------------------------------------------
CREATE TABLE categories (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(10)  NOT NULL,
    icon       VARCHAR(50),
    system     BOOLEAN      NOT NULL DEFAULT false,
    account_id UUID,

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_name_type_account UNIQUE (name, type, account_id),
    CONSTRAINT fk_categories_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT ck_categories_system_account CHECK (
        (system = true  AND account_id IS NULL) OR
        (system = false AND account_id IS NOT NULL)
    )
);

CREATE INDEX idx_categories_account_id ON categories (account_id);
CREATE INDEX idx_categories_type       ON categories (type);
CREATE INDEX idx_categories_system     ON categories (system);

-- ------------------------------------------------------------
-- STEP 2: Seed - categorias padrao do sistema
-- ------------------------------------------------------------
INSERT INTO categories (id, name, type, icon, system, account_id) VALUES
    (gen_random_uuid(), 'Moradia',                'DEBIT',  'home',              true, NULL),
    (gen_random_uuid(), 'Aluguel',                'DEBIT',  'key',               true, NULL),
    (gen_random_uuid(), 'Condominio',             'DEBIT',  'building-2',        true, NULL),
    (gen_random_uuid(), 'Agua e Esgoto',          'DEBIT',  'droplet',           true, NULL),
    (gen_random_uuid(), 'Energia Eletrica',       'DEBIT',  'zap',               true, NULL),
    (gen_random_uuid(), 'Gas',                    'DEBIT',  'flame',             true, NULL),
    (gen_random_uuid(), 'Internet',               'DEBIT',  'wifi',              true, NULL),
    (gen_random_uuid(), 'Telefone',               'DEBIT',  'phone',             true, NULL),
    (gen_random_uuid(), 'Alimentacao',            'DEBIT',  'utensils',          true, NULL),
    (gen_random_uuid(), 'Supermercado',           'DEBIT',  'shopping-cart',     true, NULL),
    (gen_random_uuid(), 'Restaurante',            'DEBIT',  'utensils-crossed',  true, NULL),
    (gen_random_uuid(), 'Delivery',               'DEBIT',  'bike',              true, NULL),
    (gen_random_uuid(), 'Padaria e Cafe',         'DEBIT',  'coffee',            true, NULL),
    (gen_random_uuid(), 'Transporte',             'DEBIT',  'car',               true, NULL),
    (gen_random_uuid(), 'Combustivel',            'DEBIT',  'fuel',              true, NULL),
    (gen_random_uuid(), 'Estacionamento',         'DEBIT',  'square-parking',    true, NULL),
    (gen_random_uuid(), 'Transporte Publico',     'DEBIT',  'bus',               true, NULL),
    (gen_random_uuid(), 'Aplicativo de Taxi',     'DEBIT',  'car-taxi-front',    true, NULL),
    (gen_random_uuid(), 'Manutencao Veicular',    'DEBIT',  'wrench',            true, NULL),
    (gen_random_uuid(), 'Saude',                  'DEBIT',  'heart-pulse',       true, NULL),
    (gen_random_uuid(), 'Plano de Saude',         'DEBIT',  'shield-plus',       true, NULL),
    (gen_random_uuid(), 'Consulta Medica',        'DEBIT',  'stethoscope',       true, NULL),
    (gen_random_uuid(), 'Farmacia',               'DEBIT',  'pill',              true, NULL),
    (gen_random_uuid(), 'Exames e Laboratorio',   'DEBIT',  'flask-conical',     true, NULL),
    (gen_random_uuid(), 'Academia',               'DEBIT',  'dumbbell',          true, NULL),
    (gen_random_uuid(), 'Educacao',               'DEBIT',  'graduation-cap',    true, NULL),
    (gen_random_uuid(), 'Mensalidade Escolar',    'DEBIT',  'school',            true, NULL),
    (gen_random_uuid(), 'Cursos e Treinamentos',  'DEBIT',  'book-open',         true, NULL),
    (gen_random_uuid(), 'Material Escolar',       'DEBIT',  'pencil',            true, NULL),
    (gen_random_uuid(), 'Livros',                 'DEBIT',  'book',              true, NULL),
    (gen_random_uuid(), 'Lazer',                  'DEBIT',  'party-popper',      true, NULL),
    (gen_random_uuid(), 'Streaming',              'DEBIT',  'tv',                true, NULL),
    (gen_random_uuid(), 'Cinema e Teatro',        'DEBIT',  'clapperboard',      true, NULL),
    (gen_random_uuid(), 'Viagens',                'DEBIT',  'plane',             true, NULL),
    (gen_random_uuid(), 'Hobbies',                'DEBIT',  'palette',           true, NULL),
    (gen_random_uuid(), 'Vestuario',              'DEBIT',  'shirt',             true, NULL),
    (gen_random_uuid(), 'Roupas e Calcados',      'DEBIT',  'footprints',        true, NULL),
    (gen_random_uuid(), 'Acessorios',             'DEBIT',  'watch',             true, NULL),
    (gen_random_uuid(), 'Financiamento',          'DEBIT',  'landmark',          true, NULL),
    (gen_random_uuid(), 'Cartao de Credito',      'DEBIT',  'credit-card',       true, NULL),
    (gen_random_uuid(), 'Emprestimo',             'DEBIT',  'hand-coins',        true, NULL),
    (gen_random_uuid(), 'Seguro',                 'DEBIT',  'shield-check',      true, NULL),
    (gen_random_uuid(), 'Impostos e Taxas',       'DEBIT',  'receipt',           true, NULL),
    (gen_random_uuid(), 'Tarifas Bancarias',      'DEBIT',  'banknote',          true, NULL),
    (gen_random_uuid(), 'Beleza e Cuidados',      'DEBIT',  'sparkles',          true, NULL),
    (gen_random_uuid(), 'Tecnologia',             'DEBIT',  'laptop',            true, NULL),
    (gen_random_uuid(), 'Presentes e Doacoes',    'DEBIT',  'gift',              true, NULL),
    (gen_random_uuid(), 'Outros',                 'DEBIT',  'circle-ellipsis',   true, NULL),
    (gen_random_uuid(), 'Salario',                'CREDIT', 'banknote-arrow-up', true, NULL),
    (gen_random_uuid(), 'Pro-labore',             'CREDIT', 'briefcase',         true, NULL),
    (gen_random_uuid(), 'Hora Extra',             'CREDIT', 'clock',             true, NULL),
    (gen_random_uuid(), 'Decimo Terceiro',        'CREDIT', 'gift',              true, NULL),
    (gen_random_uuid(), 'Ferias',                 'CREDIT', 'palmtree',          true, NULL),
    (gen_random_uuid(), 'Bonus e Comissao',       'CREDIT', 'award',             true, NULL),
    (gen_random_uuid(), 'Freelance',              'CREDIT', 'laptop-minimal',    true, NULL),
    (gen_random_uuid(), 'Trabalho Autonomo',      'CREDIT', 'hammer',            true, NULL),
    (gen_random_uuid(), 'Renda Extra',            'CREDIT', 'circle-plus',       true, NULL),
    (gen_random_uuid(), 'Investimentos',          'CREDIT', 'trending-up',       true, NULL),
    (gen_random_uuid(), 'Dividendos',             'CREDIT', 'chart-line',        true, NULL),
    (gen_random_uuid(), 'Aluguel Recebido',       'CREDIT', 'house',             true, NULL),
    (gen_random_uuid(), 'Juros Recebidos',        'CREDIT', 'percent',           true, NULL),
    (gen_random_uuid(), 'Vale Alimentacao',       'CREDIT', 'utensils',          true, NULL),
    (gen_random_uuid(), 'Vale Refeicao',          'CREDIT', 'sandwich',          true, NULL),
    (gen_random_uuid(), 'Vale Transporte',        'CREDIT', 'bus',               true, NULL),
    (gen_random_uuid(), 'Reembolso',              'CREDIT', 'rotate-ccw',        true, NULL),
    (gen_random_uuid(), 'Transferencia Recebida', 'CREDIT', 'arrow-down-left',   true, NULL),
    (gen_random_uuid(), 'Cashback',               'CREDIT', 'badge-percent',     true, NULL),
    (gen_random_uuid(), 'Venda de Bens',          'CREDIT', 'tag',               true, NULL),
    (gen_random_uuid(), 'Outros',                 'CREDIT', 'circle-ellipsis',   true, NULL);

-- ------------------------------------------------------------
-- STEP 3: FK retroativa de accounts -> categories
-- ------------------------------------------------------------
ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_biggest_expense_category
        FOREIGN KEY (biggest_expense_category_id) REFERENCES categories (id) ON DELETE SET NULL;

CREATE INDEX idx_accounts_biggest_expense_category_id
    ON accounts (biggest_expense_category_id);

-- ------------------------------------------------------------
-- STEP 4: banks
-- Catalogo compartilhado do sistema.
-- icon deve ser um nome valido do lucide-react.
-- ------------------------------------------------------------
CREATE TABLE banks (
    id       UUID          NOT NULL DEFAULT gen_random_uuid(),
    name     VARCHAR(100) NOT NULL,
    icon     VARCHAR(50)  NOT NULL,
    color    VARCHAR(20)  NOT NULL,
    gradient VARCHAR(180) NOT NULL,
    shadow   VARCHAR(120) NOT NULL,

    CONSTRAINT pk_banks PRIMARY KEY (id),
    CONSTRAINT uq_banks_name UNIQUE (name)
);

CREATE INDEX idx_banks_name ON banks (name);

-- ------------------------------------------------------------
-- STEP 5: Seed - bancos populares no Brasil
-- ------------------------------------------------------------
INSERT INTO banks (id, name, icon, color, gradient, shadow) VALUES
(gen_random_uuid(), 'Nubank',                  'landmark', '#8B5CF6', 'linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)',                 'rgba(139,92,246,0.35)'),
(gen_random_uuid(), 'Itau',                    'landmark', '#F59E0B', 'linear-gradient(135deg, #F59E0B 0%, #D97706 60%, #92400E 100%)',    'rgba(245,158,11,0.30)'),
(gen_random_uuid(), 'Bradesco',                'landmark', '#EF4444', 'linear-gradient(135deg, #EF4444 0%, #B91C1C 100%)',                 'rgba(239,68,68,0.30)'),

(gen_random_uuid(), 'Banco do Brasil',         'landmark', '#FEDD00', 'linear-gradient(135deg, #0038A8 0%, #FEDD00 60%, #FFE866 100%)',    'rgba(254,221,0,0.30)'),

(gen_random_uuid(), 'Caixa Economica Federal', 'landmark', '#1D4ED8', 'linear-gradient(135deg, #1D4ED8 0%, #1E3A8A 100%)',                 'rgba(29,78,216,0.35)'),

(gen_random_uuid(), 'Santander',               'landmark', '#DC2626', 'linear-gradient(135deg, #DC2626 0%, #991B1B 100%)',                 'rgba(220,38,38,0.30)'),

(gen_random_uuid(), 'Inter',                   'landmark', '#F97316', 'linear-gradient(135deg, #F97316 0%, #EA580C 100%)',                 'rgba(249,115,22,0.30)'),

(gen_random_uuid(), 'BTG Pactual',             'landmark', '#1D3557', 'linear-gradient(135deg, #07111F 0%, #1D3557 60%, #2F80ED 100%)',   'rgba(29,53,87,0.35)'),

(gen_random_uuid(), 'C6 Bank',                 'landmark', '#2D2D44', 'linear-gradient(135deg, #1C1C2E 0%, #2D2D44 50%, #1C1C2E 100%)',   'rgba(0,0,0,0.50)'),

(gen_random_uuid(), 'Banco Pan',               'landmark', '#00A6D6', 'linear-gradient(135deg, #006B99 0%, #00A6D6 55%, #5EE7FF 100%)',   'rgba(0,166,214,0.30)'),

(gen_random_uuid(), 'Banco Original',          'landmark', '#00A859', 'linear-gradient(135deg, #006B3A 0%, #00A859 55%, #7ED957 100%)',   'rgba(0,168,89,0.30)'),

(gen_random_uuid(), 'Mercado Pago',            'wallet',   '#00A6F4', 'linear-gradient(135deg, #00A6F4 0%, #009EE3 45%, #0078C9 100%)',   'rgba(0,166,244,0.32)'),

(gen_random_uuid(), 'PicPay',                  'wallet',   '#21C25E', 'linear-gradient(135deg, #0B7A34 0%, #21C25E 55%, #76F2A8 100%)',   'rgba(33,194,94,0.30)'),

(gen_random_uuid(), 'PagBank',                 'credit-card','#FFD100','linear-gradient(135deg, #F5A400 0%, #FFD100 55%, #FFE873 100%)',   'rgba(255,209,0,0.30)'),

(gen_random_uuid(), 'Neon',                    'landmark', '#00AEEF', 'linear-gradient(135deg, #0057D9 0%, #00AEEF 55%, #63E6FF 100%)',   'rgba(0,174,239,0.30)'),

(gen_random_uuid(), 'Sicoob',                  'landmark', '#003641', 'linear-gradient(135deg, #003641 0%, #007C89 60%, #7AC143 100%)',   'rgba(0,54,65,0.35)'),

(gen_random_uuid(), 'Sicredi',                 'landmark', '#3FAE2A', 'linear-gradient(135deg, #236B1F 0%, #3FAE2A 55%, #8CC63F 100%)',   'rgba(63,174,42,0.30)'),

(gen_random_uuid(), 'XP Investimentos',        'trending-up','#111111','linear-gradient(135deg, #050505 0%, #111111 60%, #D6A329 100%)',  'rgba(17,17,17,0.35)');

-- ------------------------------------------------------------
-- STEP 6: wallets
-- Uma wallet pode ser conta bancaria, carteira fisica, meta/reserva etc.
-- bank_id e card_digits sao opcionais porque nem toda wallet e bancaria.
-- ------------------------------------------------------------
CREATE TABLE wallets (
    id          UUID          NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(150),
    description VARCHAR(255),
    balance     NUMERIC(19, 2),
    card_digits VARCHAR(4),
    type        VARCHAR(50),
    color       VARCHAR(100),
    account_id  UUID          NOT NULL,
    bank_id     UUID,
    goal_id     UUID,

    CONSTRAINT pk_wallets PRIMARY KEY (id),
    CONSTRAINT fk_wallets_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_wallets_bank
        FOREIGN KEY (bank_id) REFERENCES banks (id) ON DELETE SET NULL,
    CONSTRAINT ck_wallets_card_digits CHECK (
        card_digits IS NULL OR card_digits ~ '^[0-9]{4}$'
    )
);

CREATE INDEX idx_wallets_account_id ON wallets (account_id);
CREATE INDEX idx_wallets_bank_id    ON wallets (bank_id);

-- ------------------------------------------------------------
-- STEP 7: goals
-- ------------------------------------------------------------
CREATE TABLE goals (
    id             UUID          NOT NULL DEFAULT gen_random_uuid(),
    name           VARCHAR(150),
    description    VARCHAR(255),
    total_amount   NUMERIC(19, 2),
    current_amount NUMERIC(19, 2),
    category_id    UUID,

    CONSTRAINT pk_goals PRIMARY KEY (id),
    CONSTRAINT fk_goals_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_goals_category_id ON goals (category_id);

-- ------------------------------------------------------------
-- STEP 8: FK e UNIQUE de wallets.goal_id agora que goals existe
-- ------------------------------------------------------------
ALTER TABLE wallets
    ADD CONSTRAINT uq_wallets_goal_id UNIQUE (goal_id),
    ADD CONSTRAINT fk_wallets_goal
        FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE SET NULL;

CREATE INDEX idx_wallets_goal_id ON wallets (goal_id);