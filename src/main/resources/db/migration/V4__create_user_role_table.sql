CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES users (id),
    role_id UUID NOT NULL REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_role_role ON user_role (role_id);
