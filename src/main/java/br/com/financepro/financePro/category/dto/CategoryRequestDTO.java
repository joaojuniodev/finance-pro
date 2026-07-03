package br.com.financepro.financePro.category.dto;

import br.com.financepro.financePro.common.enums.CategoryType;

import java.util.Objects;
import java.util.UUID;

public class CategoryRequestDTO {

    private UUID id;
    private String name;
    private CategoryType type;
    private String icon;
    private Boolean system = false;
    private UUID accountId;

    public CategoryRequestDTO() {}

    public CategoryRequestDTO(UUID id, String name, CategoryType type, String icon, Boolean system, UUID accountId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.system = system;
        this.accountId = accountId;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CategoryRequestDTO that = (CategoryRequestDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && getType() == that.getType() && Objects.equals(getSystem(), that.getSystem()) && Objects.equals(getAccountId(), that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getSystem());
        result = 31 * result + Objects.hashCode(getAccountId());
        return result;
    }
}