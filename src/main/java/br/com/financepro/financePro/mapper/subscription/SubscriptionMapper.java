package br.com.financepro.financePro.mapper.subscription;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.account.AccountMapper;
import br.com.financepro.financePro.mapper.plan.PlanMapper;
import br.com.financepro.financePro.plan.repository.PlanRepository;
import br.com.financepro.financePro.subscription.dto.request.SubscriptionRequestDTO;
import br.com.financepro.financePro.subscription.dto.response.SubscriptionResponseDTO;
import br.com.financepro.financePro.subscription.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper implements ObjectMapper<Subscription, SubscriptionResponseDTO, SubscriptionRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PlanMapper planMapper;

    @Override
    public Subscription toEntity(SubscriptionRequestDTO request) {
        var account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        var plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new NotFoundException("Not found this Plan Id: " + request.getPlanId()));
        var subscription = new Subscription();
        subscription.setId(request.getId());
        subscription.setStatus(request.getStatus());
        subscription.setExternalSubscriptionId(request.getExternalSubscriptionId());
        subscription.setExternalCustomerId(request.getExternalCustomerId());
        subscription.setStartedAt(request.getStartedAt());
        subscription.setCurrentPeriodStart(request.getCurrentPeriodStart());
        subscription.setCurrentPeriodEnd(request.getCurrentPeriodEnd());
        subscription.setCreatedAt(request.getCreatedAt());
        subscription.setUpdatedAt(request.getUpdatedAt());
        subscription.setAccount(account);
        subscription.setPlan(plan);
        return subscription;
    }

    @Override
    public SubscriptionResponseDTO toResponse(Subscription entity) {
        var account = accountMapper.toResponse(entity.getAccount());
        var plan = planMapper.toResponse(entity.getPlan());
        return new SubscriptionResponseDTO(
            entity.getId(),
            entity.getStatus(),
            entity.getExternalSubscriptionId(),
            entity.getExternalCustomerId(),
            entity.getStartedAt(),
            entity.getCurrentPeriodStart(),
            entity.getCurrentPeriodEnd(),
            entity.getCanceledAt(),
            entity.getCancelAtPeriodEnd(),
            entity.getEndedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            account,
            plan
        );
    }
}