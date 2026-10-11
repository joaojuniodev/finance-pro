-- Demonstration authorities; endpoint authorization remains in the application.
INSERT INTO permissions (
    name,
    description
)
VALUES
    (
        'FINANCE_READ',
        'Consultar a vida financeira'
    ),
    (
        'FINANCE_WRITE',
        'Gerenciar carteiras e lançamentos'
    ),
    (
        'REPORTS_READ',
        'Consultar relatórios avançados'
    ),
    (
        'USERS_MANAGE',
        'Administrar usuários'
    );
