package br.com.financepro.financePro.bank.model;

import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String icon;

    @Column(nullable = false, length = 20)
    private String color;

    @Column(nullable = false, length = 180)
    private String gradient;

    @Column(nullable = false, length = 120)
    private String shadow;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<Wallet> wallets;

    public Bank() {}

    public Bank(UUID id, String name, String icon, String color, String gradient, String shadow) {
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

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Bank bank = (Bank) o;
        return Objects.equals(getId(), bank.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}