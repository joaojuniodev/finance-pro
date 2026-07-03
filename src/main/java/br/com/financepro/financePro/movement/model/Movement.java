package br.com.financepro.financePro.movement.model;

import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "from_wallet")
    private Wallet fromWallet;

    @ManyToOne
    @JoinColumn(name = "to_wallet")
    private Wallet toWallet;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public Movement() {}

    public Movement(UUID id, BigDecimal amount, Wallet fromWallet, Wallet toWallet, LocalDateTime registeredAt) {
        this.id = id;
        this.amount = amount;
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.registeredAt = registeredAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Wallet getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Wallet fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Wallet getToWallet() {
        return toWallet;
    }

    public void setToWallet(Wallet toWallet) {
        this.toWallet = toWallet;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Movement movement = (Movement) o;
        return Objects.equals(getId(), movement.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
