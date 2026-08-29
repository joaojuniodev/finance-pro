package br.com.financepro.financePro.payment.repository;

import br.com.financepro.financePro.payment.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

    boolean existsByExternalEventId(String externalEventId);
}