package br.com.financepro.financePro.subscription.dto.request;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.plan.model.Plan;
import br.com.financepro.financePro.subscription.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SubscriptionRequestDTO {

    private UUID id;
    private SubscriptionStatus status;
    private String externalSubscriptionId;
    private String externalCustomerId;
    private LocalDateTime startedAt;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID accountId;
    private UUID planId;

    public SubscriptionRequestDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getExternalSubscriptionId() {
        return externalSubscriptionId;
    }

    public void setExternalSubscriptionId(String externalSubscriptionId) {
        this.externalSubscriptionId = externalSubscriptionId;
    }

    public String getExternalCustomerId() {
        return externalCustomerId;
    }

    public void setExternalCustomerId(String externalCustomerId) {
        this.externalCustomerId = externalCustomerId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDate getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public void setCurrentPeriodStart(LocalDate currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }

    public LocalDate getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(LocalDate currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionRequestDTO that = (SubscriptionRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && getStatus() == that.getStatus() && Objects.equals(getExternalSubscriptionId(), that.getExternalSubscriptionId()) && Objects.equals(getExternalCustomerId(), that.getExternalCustomerId()) && Objects.equals(getStartedAt(), that.getStartedAt()) && Objects.equals(getCurrentPeriodStart(), that.getCurrentPeriodStart()) && Objects.equals(getCurrentPeriodEnd(), that.getCurrentPeriodEnd()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && Objects.equals(getAccountId(), that.getAccountId()) && Objects.equals(getPlanId(), that.getPlanId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getExternalSubscriptionId());
        result = 31 * result + Objects.hashCode(getExternalCustomerId());
        result = 31 * result + Objects.hashCode(getStartedAt());
        result = 31 * result + Objects.hashCode(getCurrentPeriodStart());
        result = 31 * result + Objects.hashCode(getCurrentPeriodEnd());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        result = 31 * result + Objects.hashCode(getUpdatedAt());
        result = 31 * result + Objects.hashCode(getAccountId());
        result = 31 * result + Objects.hashCode(getPlanId());
        return result;
    }
}