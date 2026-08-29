package br.com.financepro.financePro.wallet.dto;

import br.com.financepro.financePro.bank.dto.BankResponseDTO;
import br.com.financepro.financePro.common.enums.WalletType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class WalletResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private String cardDigits;
    private WalletType type;
    private String color;
    private BigDecimal balance;
    private BankResponseDTO bank;

    public WalletResponseDTO() {
    }

    public WalletResponseDTO(UUID id, String name, String description, String cardDigits, WalletType type, String color, BigDecimal balance, BankResponseDTO bank) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cardDigits = cardDigits;
        this.type = type;
        this.color = color;
        this.balance = balance;
        this.bank = bank;
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

    public String getCardDigits() {
        return cardDigits;
    }

    public void setCardDigits(String cardDigits) {
        this.cardDigits = cardDigits;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BankResponseDTO getBank() {
        return bank;
    }

    public void setBank(BankResponseDTO bank) {
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WalletResponseDTO that = (WalletResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getCardDigits(), that.getCardDigits()) && getType() == that.getType() && Objects.equals(getColor(), that.getColor()) && Objects.equals(getBalance(), that.getBalance()) && Objects.equals(getBank(), that.getBank());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getCardDigits());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getColor());
        result = 31 * result + Objects.hashCode(getBalance());
        result = 31 * result + Objects.hashCode(getBank());
        return result;
    }
}