package br.com.financepro.financePro.payment.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "webhook_events",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_webhook_events_external_event_id",
            columnNames = "external_event_id"
        )
    }
)
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_event_id", nullable = false, unique = true)
    private String externalEventId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Instant receivedAt;

    private Instant processedAt;

    public WebhookEvent() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalEventId() {
        return externalEventId;
    }

    public void setExternalEventId(String externalEventId) {
        this.externalEventId = externalEventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WebhookEvent that = (WebhookEvent) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}