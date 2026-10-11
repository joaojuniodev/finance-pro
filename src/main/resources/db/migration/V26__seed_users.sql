-- Local test credentials only. Never deploy these known passwords to production.
-- username/password: demo/demo and joaojunio/joaojunio.
-- Same encoder as SecurityConfig: PBKDF2-HMAC-SHA256, empty secret,
-- 8-byte salt, 1,815,000 iterations, 256-bit hash, hex with {pbkdf2} prefix.
INSERT INTO users (
    username,
    password,
    full_name,
    email,
    recovery_email,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    enabled
)
VALUES
    (
        'demo',
        '{pbkdf2}981e44a7480e0491f7cf5b937419e125842a9d7db45e23ee40d1f46c643531ac8e3421cb17dd5b9f',
        'Usuário Demonstração',
        'demo@example.test',
        'demo.recovery@example.test',
        TRUE,
        TRUE,
        TRUE,
        TRUE
    ),
    (
        'joaojunio',
        '{pbkdf2}21ccfa8516f6db6b18025688331fb0e64bf187da08cea8e0f7a3b904758643e6ccc265f1b4b477a5',
        'João Junio',
        'joaojunio@example.test',
        'joaojunio.recovery@example.test',
        TRUE,
        TRUE,
        TRUE,
        TRUE
    );
