INSERT INTO goals (
    name,
    description,
    total_amount,
    current_amount,
    category_id
)
SELECT
    seed.name,
    seed.description,
    seed.total_amount,
    seed.current_amount,
    category.id
FROM (
    VALUES
        (
            'Reserva de emergência',
            'Construir uma reserva para imprevistos',
            18000.00,
            1500.00,
            'Outras despesas'
        ),
        (
            'Viagem de férias',
            'Planejamento da próxima viagem',
            6000.00,
            500.00,
            'Viagens'
        )
) AS seed (
    name,
    description,
    total_amount,
    current_amount,
    category_name
)
JOIN categories AS category
    ON category.name = seed.category_name
   AND category.type = 'DEBIT'
   AND category.system = TRUE;
