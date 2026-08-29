package br.com.financepro.financePro.subscription.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.subscription.SubscriptionMapper;
import br.com.financepro.financePro.payment.model.Payment;
import br.com.financepro.financePro.subscription.dto.request.SubscriptionRequestDTO;
import br.com.financepro.financePro.subscription.dto.response.SubscriptionResponseDTO;
import br.com.financepro.financePro.subscription.enums.SubscriptionStatus;
import br.com.financepro.financePro.subscription.model.Subscription;
import br.com.financepro.financePro.subscription.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionService.class.getName());

    @Autowired
    private SubscriptionRepository repository;

    @Autowired
    private SubscriptionMapper mapper;

    public List<SubscriptionResponseDTO> getAll() {
        log.info("Getting all Subscriptions");

        return repository.findAll().stream()
            .map(subscription -> mapper.toResponse(subscription))
            .toList();
    }

    public SubscriptionResponseDTO getById(UUID id) {
        log.info("Getting by Id Subscription");

        var subscription = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + id));
        return mapper.toResponse(subscription);
    }

    public SubscriptionResponseDTO create(SubscriptionRequestDTO subscription) {
        log.info("Creating Subscription");

        var subscriptionCreated = repository.save(mapper.toEntity(subscription));
        return mapper.toResponse(subscriptionCreated);
    }

    public SubscriptionResponseDTO update(SubscriptionRequestDTO subscription) {
        log.info("Updating Subscription");

        var entity = repository.findById(subscription.getId())
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + subscription.getId()));
        entity.setStatus(subscription.getStatus());
        var subscriptionUpdated = repository.save(entity);
        return mapper.toResponse(subscriptionUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Subscription");

        var subscription = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + id));
        repository.delete(subscription);
    }

    @Transactional
    public Subscription activateFromPayment(Payment payment) {
        Subscription subscription = repository
            .findByAccountId(payment.getAccount().getId())
            .orElseGet(Subscription::new);

        LocalDate startDate = payment.getPaidAt().toLocalDate();;
        LocalDate endDate = startDate.plusMonths(1);

        subscription.setAccount(payment.getAccount());
        subscription.setPlan(payment.getPlan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        if (subscription.getStartedAt() == null) {
            subscription.setStartedAt(payment.getPaidAt());
        }

        subscription.setCurrentPeriodStart(startDate);
        subscription.setCurrentPeriodEnd(endDate);

        subscription.setCancelAtPeriodEnd(false);
        subscription.setUpdatedAt(LocalDateTime.now());

        if (subscription.getCreatedAt() == null) {
            subscription.setCreatedAt(LocalDateTime.now());
        }

        var savedSubscription = repository.save(subscription);

        payment.setSubscription(savedSubscription);

        return savedSubscription;

    }
}