-- ============================================================
-- V1 - Tabelas de Segurança
-- permissions, roles, role_permission, users, user_role
--
-- Ordem respeita dependências de FK:
-- permissions → roles → role_permission
-- users → user_role
-- ============================================================

-- ------------------------------------------------------------
-- permissions
-- Permissões granulares do sistema (ex: MANAGE_TRANSACTIONS)
-- ------------------------------------------------------------
CREATE TABLE permissions (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name)
);

-- ------------------------------------------------------------
-- roles
-- Papéis agrupadores de permissões (ex: ROLE_ADMIN, ROLE_USER)
-- ------------------------------------------------------------
CREATE TABLE roles (
    id   UUID         NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ------------------------------------------------------------
-- role_permission (join table ManyToMany: Role ↔ Permission)
-- ------------------------------------------------------------
CREATE TABLE role_permission (
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,

    CONSTRAINT pk_role_permission PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role
        FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- users
-- Usuários da aplicação
-- ------------------------------------------------------------
CREATE TABLE users (
    id                      UUID         NOT NULL DEFAULT gen_random_uuid(),
    username                VARCHAR(100) NOT NULL,
    password                VARCHAR(100) NOT NULL,
    full_name               VARCHAR(150),
    email                   VARCHAR(255),
    recovery_email          VARCHAR(255),
    account_non_expired     BOOLEAN,
    account_non_locked      BOOLEAN,
    credentials_non_expired BOOLEAN,
    enabled                 BOOLEAN,

    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- user_role (join table ManyToMany: User ↔ Role)
-- ------------------------------------------------------------
CREATE TABLE user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Índices de performance para joins frequentes
-- ------------------------------------------------------------
CREATE INDEX idx_user_role_user_id       ON user_role (user_id);
CREATE INDEX idx_user_role_role_id       ON user_role (role_id);
CREATE INDEX idx_role_permission_role_id ON role_permission (role_id);