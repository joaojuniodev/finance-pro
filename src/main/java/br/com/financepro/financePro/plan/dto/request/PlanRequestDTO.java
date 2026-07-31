package br.com.financepro.financePro.plan.dto.request;

import br.com.financepro.financePro.plan.enums.PlanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PlanRequestDTO {

    private UUID id;
    private String name;
    private String description;
    private PlanType type;
    private BigDecimal price;
    private String currency;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlanRequestDTO() {}

    public PlanRequestDTO(UUID id, String name, String description, PlanType type, BigDecimal price, String currency, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanType getType() {
        return type;
    }

    public void setType(PlanType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        PlanRequestDTO that = (PlanRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && getType() == that.getType() && Objects.equals(getPrice(), that.getPrice()) && Objects.equals(getCurrency(), that.getCurrency()) && Objects.equals(getActive(), that.getActive()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getPrice());
        result = 31 * result + Objects.hashCode(getCurrency());
        result = 31 * result + Objects.hashCode(getActive());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        result = 31 * result + Objects.hashCode(getUpdatedAt());
        return result;
    }
}