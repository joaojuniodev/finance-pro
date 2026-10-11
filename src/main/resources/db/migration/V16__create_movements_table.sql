CREATE TABLE movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(38, 2),
    from_wallet UUID REFERENCES wallets (id),
    to_wallet UUID REFERENCES wallets (id),
    registered_at TIMESTAMP
);

CREATE INDEX idx_movements_from_wallet ON movements (from_wallet);
CREATE INDEX idx_movements_to_wallet ON movements (to_wallet);
