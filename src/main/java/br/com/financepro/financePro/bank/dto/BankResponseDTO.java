package br.com.financepro.financePro.bank.dto;

import java.util.Objects;
import java.util.UUID;

public class BankResponseDTO {

    private UUID id;
    private String name;
    private String icon;
    private String color;
    private String gradient;
    private String shadow;

    public BankResponseDTO() {}

    public BankResponseDTO(UUID id, String name, String icon, String color, String gradient, String shadow) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.gradient = gradient;
        this.shadow = shadow;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public String getShadow() {
        return shadow;
    }

    public void setShadow(String shadow) {
        this.shadow = shadow;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BankResponseDTO that = (BankResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getIcon(), that.getIcon()) && Objects.equals(getColor(), that.getColor()) && Objects.equals(gradient, that.gradient) && Objects.equals(shadow, that.shadow);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getIcon());
        result = 31 * result + Objects.hashCode(getColor());
        result = 31 * result + Objects.hashCode(gradient);
        result = 31 * result + Objects.hashCode(shadow);
        return result;
    }
}