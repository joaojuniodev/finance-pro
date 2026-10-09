WITH inserted_plan AS (
    INSERT INTO installment_plans (
        description,
        total_value,
        value,
        times,
        fees,
        start_date,
        end_date,
        status,
        wallet_id
    )
    VALUES (
        'Notebook para trabalho',
        3600.00,
        3000.00,
        12,
        20.00,
        CURRENT_DATE - INTERVAL '3 months',
        CURRENT_DATE + INTERVAL '9 months',
        'PAYING',
        '00000000-0000-0000-0000-000000000601'
    )
    RETURNING id
)
INSERT INTO installments (value, is_paid, month, year, installment_plan_id)
SELECT
    installment.value,
    installment.is_paid,
    installment.month,
    installment.year,
    inserted_plan.id
FROM inserted_plan
CROSS JOIN (
    VALUES
        (300.00, TRUE, 7, 2026),
        (300.00, TRUE, 8, 2026),
        (300.00, TRUE, 9, 2026),
        (300.00, FALSE, 10, 2026),
        (300.00, FALSE, 11, 2026),
        (300.00, FALSE, 12, 2026)
) AS installment(value, is_paid, month, year);

WITH inserted_plan AS (
    INSERT INTO installment_plans (
        description,
        total_value,
        value,
        times,
        fees,
        start_date,
        end_date,
        status,
        wallet_id
    )
    VALUES (
        'Curso de especialização',
        1200.00,
        1200.00,
        6,
        0.00,
        CURRENT_DATE - INTERVAL '1 month',
        CURRENT_DATE + INTERVAL '5 months',
        'PAYING',
        '00000000-0000-0000-0000-000000000601'
    )
    RETURNING id
)
INSERT INTO installments (value, is_paid, month, year, installment_plan_id)
SELECT
    installment.value,
    installment.is_paid,
    installment.month,
    installment.year,
    inserted_plan.id
FROM inserted_plan
CROSS JOIN (
    VALUES
        (200.00, TRUE, 9, 2026),
        (200.00, FALSE, 10, 2026),
        (200.00, FALSE, 11, 2026),
        (200.00, FALSE, 12, 2026),
        (200.00, FALSE, 1, 2027),
        (200.00, FALSE, 2, 2027)
) AS installment(value, is_paid, month, year);

WITH inserted_plan AS (
    INSERT INTO installment_plans (
        description,
        total_value,
        value,
        times,
        fees,
        start_date,
        end_date,
        status,
        wallet_id
    )
    VALUES (
        'Celular antigo',
        1800.00,
        1800.00,
        3,
        0.00,
        CURRENT_DATE - INTERVAL '4 months',
        CURRENT_DATE - INTERVAL '1 month',
        'PAID',
        '00000000-0000-0000-0000-000000000601'
    )
    RETURNING id
)
INSERT INTO installments (value, is_paid, month, year, installment_plan_id)
SELECT
    installment.value,
    installment.is_paid,
    installment.month,
    installment.year,
    inserted_plan.id
FROM inserted_plan
CROSS JOIN (
    VALUES
        (600.00, TRUE, 6, 2026),
        (600.00, TRUE, 7, 2026),
        (600.00, TRUE, 8, 2026)
) AS installment(value, is_paid, month, year);
