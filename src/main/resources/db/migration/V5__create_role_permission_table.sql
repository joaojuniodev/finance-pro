CREATE TABLE role_permission (
    role_id UUID NOT NULL REFERENCES roles (id),
    permission_id UUID NOT NULL REFERENCES permissions (id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permission_permission ON role_permission (permission_id);
