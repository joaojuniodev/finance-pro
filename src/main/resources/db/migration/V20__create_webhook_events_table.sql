CREATE TABLE webhook_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_event_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_webhook_events_external_event_id UNIQUE (external_event_id)
);
