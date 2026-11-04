INSERT INTO plans (
    name,
    description,
    type,
    price,
    currency,
    active,
    created_at,
    updated_at
)
SELECT
    seed.name,
    seed.description,
    seed.type,
    seed.price,
    'BRL',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    VALUES
        (
            'Capto Free',
            'Plano gratuito de demonstração',
            'FREE',
            0.00
        ),
        (
            'Capto Pro',
            'Plano profissional de demonstração',
            'PRO',
            29.90
        ),
        (
            'Capto Premium',
            'Plano completo de demonstração',
            'PREMIUM',
            49.90
        )
) AS seed (
    name,
    description,
    type,
    price
);
