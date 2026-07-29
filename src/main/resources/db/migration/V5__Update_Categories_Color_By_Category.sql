-- ============================================================
-- V5 - Cor por categoria
--
-- Adiciona coluna color e distribui um matiz (hue) distinto
-- para cada categoria existente, usando o angulo aureo
-- (~137.5 graus) para maximizar a distancia perceptual entre
-- cores vizinhas na ordenacao, independente da quantidade
-- de categorias cadastradas.
-- ============================================================

ALTER TABLE categories
    ADD COLUMN color VARCHAR(20);

WITH ranked AS (
    SELECT
        id,
        row_number() OVER (ORDER BY type, name) AS rn
    FROM categories
)
UPDATE categories c
SET color = 'hsl(' ||
    round(mod(ranked.rn * 137.508, 360)::numeric)::int ||
    ',70%,55%)'
FROM ranked
WHERE c.id = ranked.id;

ALTER TABLE categories
    ALTER COLUMN color SET NOT NULL;