package br.com.financepro.financePro.subscription.dto.response;

import br.com.financepro.financePro.account.dto.response.AccountResponseDTO;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.plan.dto.request.PlanRequestDTO;
import br.com.financepro.financePro.plan.dto.response.PlanResponseDTO;
import br.com.financepro.financePro.plan.model.Plan;
import br.com.financepro.financePro.subscription.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SubscriptionResponseDTO {

    private UUID id;
    private SubscriptionStatus status;
    private String externalSubscriptionId;
    private String externalCustomerId;
    private LocalDateTime startedAt;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private LocalDate canceledAt;
    private Boolean cancelAtPeriodEnd;
    private LocalDate endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccountResponseDTO account;
    private PlanResponseDTO plan;

    public SubscriptionResponseDTO() {}

    public SubscriptionResponseDTO(UUID id, SubscriptionStatus status, String externalSubscriptionId, String externalCustomerId, LocalDateTime startedAt, LocalDate currentPeriodStart, LocalDate currentPeriodEnd, LocalDate canceledAt, Boolean cancelAtPeriodEnd, LocalDate endedAt, LocalDateTime createdAt, LocalDateTime updatedAt, AccountResponseDTO account, PlanResponseDTO plan) {
        this.id = id;
        this.status = status;
        this.externalSubscriptionId = externalSubscriptionId;
        this.externalCustomerId = externalCustomerId;
        this.startedAt = startedAt;
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
        this.canceledAt = canceledAt;
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
        this.endedAt = endedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.account = account;
        this.plan = plan;
    }

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

    public LocalDate getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDate canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Boolean getCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }

    public void setCancelAtPeriodEnd(Boolean cancelAtPeriodEnd) {
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    }

    public LocalDate getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDate endedAt) {
        this.endedAt = endedAt;
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

    public AccountResponseDTO getAccount() {
        return account;
    }

    public void setAccount(AccountResponseDTO account) {
        this.account = account;
    }

    public PlanResponseDTO getPlan() {
        return plan;
    }

    public void setPlan(PlanResponseDTO plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionResponseDTO that = (SubscriptionResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && getStatus() == that.getStatus() && Objects.equals(getExternalSubscriptionId(), that.getExternalSubscriptionId()) && Objects.equals(getExternalCustomerId(), that.getExternalCustomerId()) && Objects.equals(getStartedAt(), that.getStartedAt()) && Objects.equals(getCurrentPeriodStart(), that.getCurrentPeriodStart()) && Objects.equals(getCurrentPeriodEnd(), that.getCurrentPeriodEnd()) && Objects.equals(getCanceledAt(), that.getCanceledAt()) && Objects.equals(getCancelAtPeriodEnd(), that.getCancelAtPeriodEnd()) && Objects.equals(getEndedAt(), that.getEndedAt()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && Objects.equals(getAccount(), that.getAccount()) && Objects.equals(getPlan(), that.getPlan());
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
        result = 31 * result + Objects.hashCode(getCanceledAt());
        result = 31 * result + Objects.hashCode(getCancelAtPeriodEnd());
        result = 31 * result + Objects.hashCode(getEndedAt());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        result = 31 * result + Objects.hashCode(getUpdatedAt());
        result = 31 * result + Objects.hashCode(getAccount());
        result = 31 * result + Objects.hashCode(getPlan());
        return result;
    }
}