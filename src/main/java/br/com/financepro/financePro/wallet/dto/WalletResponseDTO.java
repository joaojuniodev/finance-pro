package br.com.financepro.financePro.wallet.dto;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class WalletResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal balance;

    public WalletResponseDTO() {}

    public WalletResponseDTO(UUID id, String name, String description, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WalletResponseDTO that = (WalletResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getBalance(), that.getBalance());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getBalance());
        return result;
    }
}