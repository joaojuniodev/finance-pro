-- ============================================================
-- V3 - Tabela categories + wallet + goals
--
-- Depende de: accounts (FK account_id em categories e wallet)
--
-- Ordem:
--   1. categories  (depende de accounts)
--   2. Seed das categorias padrão do sistema
--   3. FK retroativa de accounts → categories
--   4. wallet      (depende de accounts)
--   5. goals       (depende de categories)
--   6. goal_id em wallet (referência circular resolvida após goals)
-- ============================================================

-- ------------------------------------------------------------
-- STEP 1: categories
-- ------------------------------------------------------------
CREATE TABLE categories (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,

    -- Enum CategoryType: CREDIT (receitas) ou DEBIT (despesas)
    type       VARCHAR(10)  NOT NULL,

    -- Nome do ícone Lucide (kebab-case, ex: 'home', 'utensils')
    -- compatível com <DynamicIcon name="..." /> do lucide-react
    icon       VARCHAR(50),

    -- true  → categoria padrão do sistema (account_id = NULL)
    -- false → categoria customizada criada pelo usuário
    system     BOOLEAN      NOT NULL DEFAULT false,

    -- NULL para categorias do sistema;
    -- FK para a conta que criou a categoria customizada
    account_id UUID,

    CONSTRAINT pk_categories PRIMARY KEY (id),

    -- Mesmo nome não pode se repetir no mesmo tipo e escopo de conta
    CONSTRAINT uq_categories_name_type_account UNIQUE (name, type, account_id),

    CONSTRAINT fk_categories_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,

    -- Categorias do sistema nunca têm account_id; customizadas sempre têm
    CONSTRAINT ck_categories_system_account CHECK (
        (system = true  AND account_id IS NULL) OR
        (system = false AND account_id IS NOT NULL)
    )
);

CREATE INDEX idx_categories_account_id ON categories (account_id);
CREATE INDEX idx_categories_type       ON categories (type);
CREATE INDEX idx_categories_system     ON categories (system);

-- ------------------------------------------------------------
-- STEP 2: Seed — categorias padrão do sistema
-- system = true, account_id = NULL
-- ------------------------------------------------------------

-- DEBIT — Despesas
INSERT INTO categories (id, name, type, icon, system, account_id) VALUES
    -- Moradia
    (gen_random_uuid(), 'Moradia',               'DEBIT', 'home',            true, NULL),
    (gen_random_uuid(), 'Aluguel',               'DEBIT', 'key',             true, NULL),
    (gen_random_uuid(), 'Condomínio',            'DEBIT', 'building-2',      true, NULL),
    (gen_random_uuid(), 'Água e Esgoto',         'DEBIT', 'droplet',         true, NULL),
    (gen_random_uuid(), 'Energia Elétrica',      'DEBIT', 'zap',             true, NULL),
    (gen_random_uuid(), 'Gás',                   'DEBIT', 'flame',           true, NULL),
    (gen_random_uuid(), 'Internet',              'DEBIT', 'wifi',            true, NULL),
    (gen_random_uuid(), 'Telefone',               'DEBIT', 'phone',           true, NULL),
    -- Alimentação
    (gen_random_uuid(), 'Alimentação',           'DEBIT', 'utensils',        true, NULL),
    (gen_random_uuid(), 'Supermercado',          'DEBIT', 'shopping-cart',   true, NULL),
    (gen_random_uuid(), 'Restaurante',           'DEBIT', 'utensils-crossed',true, NULL),
    (gen_random_uuid(), 'Delivery',              'DEBIT', 'bike',            true, NULL),
    (gen_random_uuid(), 'Padaria e Café',        'DEBIT', 'coffee',          true, NULL),
    -- Transporte
    (gen_random_uuid(), 'Transporte',            'DEBIT', 'car',             true, NULL),
    (gen_random_uuid(), 'Combustível',           'DEBIT', 'fuel',            true, NULL),
    (gen_random_uuid(), 'Estacionamento',        'DEBIT', 'square-parking',  true, NULL),
    (gen_random_uuid(), 'Transporte Público',    'DEBIT', 'bus',             true, NULL),
    (gen_random_uuid(), 'Aplicativo de Táxi',    'DEBIT', 'car-taxi-front',  true, NULL),
    (gen_random_uuid(), 'Manutenção Veicular',   'DEBIT', 'wrench',          true, NULL),
    -- Saúde
    (gen_random_uuid(), 'Saúde',                 'DEBIT', 'heart-pulse',     true, NULL),
    (gen_random_uuid(), 'Plano de Saúde',        'DEBIT', 'shield-plus',     true, NULL),
    (gen_random_uuid(), 'Consulta Médica',       'DEBIT', 'stethoscope',     true, NULL),
    (gen_random_uuid(), 'Farmácia',              'DEBIT', 'pill',            true, NULL),
    (gen_random_uuid(), 'Exames e Laboratório',  'DEBIT', 'flask-conical',   true, NULL),
    (gen_random_uuid(), 'Academia',              'DEBIT', 'dumbbell',        true, NULL),
    -- Educação
    (gen_random_uuid(), 'Educação',              'DEBIT', 'graduation-cap',  true, NULL),
    (gen_random_uuid(), 'Mensalidade Escolar',   'DEBIT', 'school',          true, NULL),
    (gen_random_uuid(), 'Cursos e Treinamentos', 'DEBIT', 'book-open',       true, NULL),
    (gen_random_uuid(), 'Material Escolar',      'DEBIT', 'pencil',          true, NULL),
    (gen_random_uuid(), 'Livros',                'DEBIT', 'book',            true, NULL),
    -- Lazer e Entretenimento
    (gen_random_uuid(), 'Lazer',                 'DEBIT', 'party-popper',    true, NULL),
    (gen_random_uuid(), 'Streaming',             'DEBIT', 'tv',              true, NULL),
    (gen_random_uuid(), 'Cinema e Teatro',       'DEBIT', 'clapperboard',    true, NULL),
    (gen_random_uuid(), 'Viagens',               'DEBIT', 'plane',           true, NULL),
    (gen_random_uuid(), 'Hobbies',               'DEBIT', 'palette',         true, NULL),
    -- Vestuário
    (gen_random_uuid(), 'Vestuário',             'DEBIT', 'shirt',           true, NULL),
    (gen_random_uuid(), 'Roupas e Calçados',     'DEBIT', 'footprints',      true, NULL),
    (gen_random_uuid(), 'Acessórios',            'DEBIT', 'watch',           true, NULL),
    -- Finanças e Dívidas
    (gen_random_uuid(), 'Financiamento',         'DEBIT', 'landmark',        true, NULL),
    (gen_random_uuid(), 'Cartão de Crédito',     'DEBIT', 'credit-card',     true, NULL),
    (gen_random_uuid(), 'Empréstimo',            'DEBIT', 'hand-coins',      true, NULL),
    (gen_random_uuid(), 'Seguro',                'DEBIT', 'shield-check',    true, NULL),
    (gen_random_uuid(), 'Impostos e Taxas',      'DEBIT', 'receipt',         true, NULL),
    (gen_random_uuid(), 'Tarifas Bancárias',     'DEBIT', 'banknote',        true, NULL),
    -- Pets
    (gen_random_uuid(), 'Pets',                  'DEBIT', 'paw-print',       true, NULL),
    (gen_random_uuid(), 'Veterinário',           'DEBIT', 'cross',           true, NULL),
    (gen_random_uuid(), 'Ração e Petiscos',      'DEBIT', 'bone',            true, NULL),
    -- Beleza e Cuidados
    (gen_random_uuid(), 'Beleza e Cuidados',     'DEBIT', 'sparkles',        true, NULL),
    (gen_random_uuid(), 'Salão e Barbearia',     'DEBIT', 'scissors',        true, NULL),
    (gen_random_uuid(), 'Cosméticos',            'DEBIT', 'spray-can',       true, NULL),
    -- Tecnologia
    (gen_random_uuid(), 'Tecnologia',            'DEBIT', 'laptop',          true, NULL),
    (gen_random_uuid(), 'Assinaturas de Software','DEBIT','app-window',      true, NULL),
    (gen_random_uuid(), 'Equipamentos',          'DEBIT', 'monitor',         true, NULL),
    -- Outros
    (gen_random_uuid(), 'Presentes e Doações',   'DEBIT', 'gift',            true, NULL),
    (gen_random_uuid(), 'Outros',                'DEBIT', 'circle-ellipsis', true, NULL);

-- CREDIT — Receitas
INSERT INTO categories (id, name, type, icon, system, account_id) VALUES
    -- Renda Principal
    (gen_random_uuid(), 'Salário',               'CREDIT', 'banknote-arrow-up', true, NULL),
    (gen_random_uuid(), 'Pró-labore',            'CREDIT', 'briefcase',         true, NULL),
    (gen_random_uuid(), 'Hora Extra',            'CREDIT', 'clock',             true, NULL),
    (gen_random_uuid(), 'Décimo Terceiro',       'CREDIT', 'gift',              true, NULL),
    (gen_random_uuid(), 'Férias',                'CREDIT', 'palmtree',          true, NULL),
    (gen_random_uuid(), 'Bônus e Comissão',      'CREDIT', 'award',             true, NULL),
    -- Renda Extra / Freelance
    (gen_random_uuid(), 'Freelance',             'CREDIT', 'laptop-minimal',    true, NULL),
    (gen_random_uuid(), 'Trabalho Autônomo',     'CREDIT', 'hammer',            true, NULL),
    (gen_random_uuid(), 'Renda Extra',           'CREDIT', 'circle-plus',       true, NULL),
    -- Investimentos
    (gen_random_uuid(), 'Investimentos',         'CREDIT', 'trending-up',       true, NULL),
    (gen_random_uuid(), 'Rendimento CDB/LCI',    'CREDIT', 'piggy-bank',        true, NULL),
    (gen_random_uuid(), 'Dividendos',            'CREDIT', 'chart-line',        true, NULL),
    (gen_random_uuid(), 'Aluguel Recebido',      'CREDIT', 'house',             true, NULL),
    (gen_random_uuid(), 'Juros Recebidos',       'CREDIT', 'percent',           true, NULL),
    -- Benefícios
    (gen_random_uuid(), 'Vale Alimentação',      'CREDIT', 'utensils',          true, NULL),
    (gen_random_uuid(), 'Vale Refeição',         'CREDIT', 'sandwich',          true, NULL),
    (gen_random_uuid(), 'Vale Transporte',       'CREDIT', 'bus',               true, NULL),
    (gen_random_uuid(), 'Auxílio Home Office',   'CREDIT', 'monitor',           true, NULL),
    (gen_random_uuid(), 'Bolsa de Estudos',      'CREDIT', 'graduation-cap',    true, NULL),
    -- Transferências e Reembolsos
    (gen_random_uuid(), 'Reembolso',             'CREDIT', 'rotate-ccw',        true, NULL),
    (gen_random_uuid(), 'Transferência Recebida','CREDIT', 'arrow-down-left',   true, NULL),
    (gen_random_uuid(), 'Cashback',              'CREDIT', 'badge-percent',     true, NULL),
    (gen_random_uuid(), 'Restituição IR',        'CREDIT', 'file-text',         true, NULL),
    -- Vendas
    (gen_random_uuid(), 'Venda de Bens',         'CREDIT', 'tag',               true, NULL),
    (gen_random_uuid(), 'Venda Online',          'CREDIT', 'shopping-bag',      true, NULL),
    -- Benefícios Governamentais
    (gen_random_uuid(), 'Aposentadoria / INSS',  'CREDIT', 'landmark',          true, NULL),
    (gen_random_uuid(), 'Auxílio Governamental', 'CREDIT', 'hand-helping',      true, NULL),
    -- Outros
    (gen_random_uuid(), 'Doação Recebida',       'CREDIT', 'heart-handshake',   true, NULL),
    (gen_random_uuid(), 'Outros',                'CREDIT', 'circle-ellipsis',   true, NULL);

-- ------------------------------------------------------------
-- STEP 3: FK retroativa de accounts → categories
-- Agora que categories existe, adicionamos a constraint que
-- não pôde ser declarada na V2
-- ------------------------------------------------------------
ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_biggest_expense_category
        FOREIGN KEY (biggest_expense_category_id) REFERENCES categories (id) ON DELETE SET NULL;

CREATE INDEX idx_accounts_biggest_expense_category_id
    ON accounts (biggest_expense_category_id);

-- ------------------------------------------------------------
-- STEP 4: wallet (depende de accounts)
-- goal_id adicionado após goals existir (STEP 6)
-- ------------------------------------------------------------
CREATE TABLE wallets (
    id          UUID          NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(150),
    description VARCHAR(255),
    balance     NUMERIC(19, 2),
    account_id  UUID          NOT NULL,
    goal_id     UUID,                    -- preenchido no STEP 6

    CONSTRAINT pk_wallets PRIMARY KEY (id),
    CONSTRAINT fk_wallets_account
        FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
    -- FK e UNIQUE de goal_id adicionados no STEP 6
);

CREATE INDEX idx_wallets_account_id ON wallets (account_id);

-- ------------------------------------------------------------
-- STEP 5: goals (depende de categories)
-- ------------------------------------------------------------
CREATE TABLE goals (
    id             UUID          NOT NULL DEFAULT gen_random_uuid(),
    name           VARCHAR(150),
    description    VARCHAR(255),
    total_amount   NUMERIC(19, 2),
    current_amount NUMERIC(19, 2),

    -- FK para categories no lugar do VARCHAR livre anterior
    category_id    UUID,

    CONSTRAINT pk_goals PRIMARY KEY (id),
    CONSTRAINT fk_goals_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_goals_category_id ON goals (category_id);

-- ------------------------------------------------------------
-- STEP 6: FK e UNIQUE de wallets.goal_id agora que goals existe
-- ------------------------------------------------------------
ALTER TABLE wallets
    ADD CONSTRAINT uq_wallets_goal_id UNIQUE (goal_id),
    ADD CONSTRAINT fk_wallets_goal
        FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE SET NULL;

CREATE INDEX idx_wallets_goal_id ON wallets (goal_id);