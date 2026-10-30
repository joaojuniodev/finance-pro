ALTER TABLE movements ADD COLUMN request_id UUID;
CREATE UNIQUE INDEX uk_movements_request_id ON movements (request_id);
