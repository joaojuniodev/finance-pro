package br.com.financepro.financePro.models;

import br.com.financepro.financePro.models.enums.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private BigDecimal totalAmount;

    @Column
    private BigDecimal currentAmount;

    @Enumerated(EnumType.STRING)
    @Column
    private Category category;

    @OneToOne(mappedBy = "goal")
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public Goal() {}

    public Goal(UUID id, String name, String description, BigDecimal totalAmount, BigDecimal currentAmount, Category category, Wallet wallet) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.category = category;
        this.wallet = wallet;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Goal goal = (Goal) o;
        return Objects.equals(getId(), goal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
