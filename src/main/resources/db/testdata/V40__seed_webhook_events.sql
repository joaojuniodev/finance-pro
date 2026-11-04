-- Synthetic audit events only; these IDs do not refer to real provider events.
INSERT INTO webhook_events (
    external_event_id,
    type,
    action,
    received_at,
    processed_at
)
VALUES
    (
        'mock-event-demo-approved',
        'payment',
        'payment.updated',
        CURRENT_TIMESTAMP - INTERVAL '2 minutes',
        CURRENT_TIMESTAMP - INTERVAL '1 minute'
    ),
    (
        'mock-event-demo-pending',
        'payment',
        'payment.created',
        CURRENT_TIMESTAMP,
        NULL
    );
