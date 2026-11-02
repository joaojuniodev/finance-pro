CREATE TABLE invoice_recurrence_charges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    recurrence_id UUID REFERENCES recurrences(id) ON DELETE SET NULL,
    scheduled_date DATE NOT NULL,
    slot INTEGER NOT NULL CHECK (slot IN (1, 2)),
    description VARCHAR(200) NOT NULL,
    amount NUMERIC(38, 2) NOT NULL CHECK (amount > 0),
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_invoice_recurrence_occurrence UNIQUE (recurrence_id, scheduled_date, slot)
);
CREATE INDEX idx_invoice_recurrence_charges_invoice ON invoice_recurrence_charges(invoice_id);

UPDATE recurrences SET execution_type = 'MANUALLY'
WHERE wallet_id IN (SELECT id FROM wallets WHERE type = 'CREDIT_CARD');

-- Recover future occurrences for currently open card bills without changing paid history.
WITH candidates AS (
    SELECT i.id AS invoice_id, r.id AS recurrence_id, r.description, r.amount,
           slot.number AS slot,
           (months.month_start + (LEAST(slot.day_number,
               EXTRACT(DAY FROM (months.month_start + INTERVAL '1 month' - INTERVAL '1 day'))::int) - 1) * INTERVAL '1 day')::date AS scheduled_date,
           i.closing_date, r.next_execution_date, cycle.previous_closing_date
    FROM invoices i
    JOIN wallets w ON w.id = i.wallet_id AND w.type = 'CREDIT_CARD'
    JOIN recurrences r ON r.wallet_id = w.id AND r.status = 'ACTIVE' AND r.type = 'DEBIT' AND r.amount > 0
    CROSS JOIN LATERAL (SELECT COALESCE((SELECT MAX(previous.closing_date) FROM invoices previous
                              WHERE previous.wallet_id = i.wallet_id AND previous.closing_date < i.closing_date),
                             (i.closing_date - INTERVAL '1 month')::date) AS previous_closing_date) cycle
    CROSS JOIN LATERAL generate_series(
        date_trunc('month', cycle.previous_closing_date),
        date_trunc('month', i.closing_date), INTERVAL '1 month'
    ) AS months(month_start)
    CROSS JOIN LATERAL (VALUES (1, r.day_one), (2, r.day_two)) AS slot(number, day_number)
    WHERE i.status <> 'PAID' AND i.closing_date IS NOT NULL
      AND slot.day_number BETWEEN 1 AND 31
      AND r.day_one BETWEEN 1 AND 31
      AND (r.frequency_type <> 'BIWEEKLY' OR (r.day_two > r.day_one AND r.day_two <= 31))
      AND (r.frequency_type <> 'YEARLY' OR r.month_of_the_year BETWEEN 1 AND 12)
      AND (slot.number = 1 OR r.frequency_type = 'BIWEEKLY')
      AND (r.frequency_type <> 'YEARLY' OR EXTRACT(MONTH FROM months.month_start) = r.month_of_the_year)
), selected AS (
    SELECT DISTINCT ON (recurrence_id, scheduled_date, slot) * FROM candidates
    WHERE scheduled_date > previous_closing_date
      AND scheduled_date <= closing_date
      AND scheduled_date >= GREATEST(COALESCE(next_execution_date, CURRENT_DATE), CURRENT_DATE)
    ORDER BY recurrence_id, scheduled_date, slot, closing_date
), inserted AS (
    INSERT INTO invoice_recurrence_charges (invoice_id, recurrence_id, scheduled_date, slot, description, amount)
    SELECT invoice_id, recurrence_id, scheduled_date, slot, description, amount FROM selected
    RETURNING invoice_id, amount
)
UPDATE invoices i SET credit_limit = i.credit_limit - totals.total
FROM (SELECT invoice_id, SUM(amount) AS total FROM inserted GROUP BY invoice_id) totals
WHERE i.id = totals.invoice_id;
