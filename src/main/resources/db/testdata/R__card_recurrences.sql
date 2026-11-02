-- Dados de demonstração para os perfis dev e test, usando o usuário demo das migrations base.
WITH card_invoice AS (
    SELECT invoice.id AS invoice_id, invoice.closing_date, wallet.id AS wallet_id, account.id AS account_id
    FROM users app_user
    JOIN accounts account ON account.user_id = app_user.id
    JOIN wallets wallet ON wallet.account_id = account.id
    JOIN invoices invoice ON invoice.wallet_id = wallet.id
    WHERE app_user.username = 'demo' AND wallet.name = 'Nubank crédito' AND invoice.status = 'PAYING'
    ORDER BY invoice.closing_date DESC
    LIMIT 1
), fixtures(description, amount, frequency, day_one, day_two) AS (
    VALUES
        ('Teste cartão mensal', 39.90::numeric, 'MONTHLY', 10, NULL::integer),
        ('Teste cartão quinzenal', 29.90::numeric, 'BIWEEKLY', 5, 15),
        ('Teste cartão anual', 120.00::numeric, 'YEARLY', 20, NULL::integer)
)
INSERT INTO recurrences (
    amount, type, frequency_type, execution_type, status, day_one, day_two, month_of_the_year,
    next_execution_date, description, category_id, wallet_id, account_id
)
SELECT fixtures.amount, 'DEBIT', fixtures.frequency, 'MANUALLY', 'ACTIVE', fixtures.day_one, fixtures.day_two,
       CASE WHEN fixtures.frequency = 'YEARLY' THEN EXTRACT(MONTH FROM card_invoice.closing_date)::integer END,
       date_trunc('month', card_invoice.closing_date)::date + fixtures.day_one - 1,
       fixtures.description, category.id, card_invoice.wallet_id, card_invoice.account_id
FROM card_invoice
CROSS JOIN fixtures
JOIN categories category ON category.name = 'Assinaturas' AND category.type = 'DEBIT' AND category.system = TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM recurrences existing
    WHERE existing.wallet_id = card_invoice.wallet_id AND existing.description = fixtures.description
);

-- Uma linha por ocorrência mantém duas cobranças quinzenais no mesmo ciclo.
WITH card_invoice AS (
    SELECT invoice.id AS invoice_id, invoice.closing_date, wallet.id AS wallet_id
    FROM users app_user
    JOIN accounts account ON account.user_id = app_user.id
    JOIN wallets wallet ON wallet.account_id = account.id
    JOIN invoices invoice ON invoice.wallet_id = wallet.id
    WHERE app_user.username = 'demo' AND wallet.name = 'Nubank crédito' AND invoice.status = 'PAYING'
    ORDER BY invoice.closing_date DESC
    LIMIT 1
), occurrences(description, slot, day) AS (
    VALUES ('Teste cartão mensal', 1, 10), ('Teste cartão quinzenal', 1, 5),
           ('Teste cartão quinzenal', 2, 15), ('Teste cartão anual', 1, 20)
), inserted AS (
    INSERT INTO invoice_recurrence_charges (invoice_id, recurrence_id, scheduled_date, slot, description, amount)
    SELECT card_invoice.invoice_id, recurrence.id,
           date_trunc('month', card_invoice.closing_date)::date + occurrences.day - 1,
           occurrences.slot, recurrence.description, recurrence.amount
    FROM card_invoice
    CROSS JOIN occurrences
    JOIN recurrences recurrence ON recurrence.wallet_id = card_invoice.wallet_id
                               AND recurrence.description = occurrences.description
    WHERE recurrence.frequency_type <> 'YEARLY'
       OR recurrence.month_of_the_year = EXTRACT(MONTH FROM card_invoice.closing_date)::integer
    ON CONFLICT (recurrence_id, scheduled_date, slot) DO NOTHING
    RETURNING invoice_id, amount
)
UPDATE invoices invoice SET credit_limit = invoice.credit_limit - totals.amount
FROM (SELECT invoice_id, SUM(amount) AS amount FROM inserted GROUP BY invoice_id) totals
WHERE invoice.id = totals.invoice_id;
