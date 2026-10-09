package br.com.financepro.financePro.invoice.model;

import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal limit;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "days_until_expiration")
    private Integer daysUntilExpiration;

    @OneToOne(mappedBy = "invoice")
    private Wallet wallet;

    @OneToMany(mappedBy = "invoice")
    private List<InstallmentPlan> installmentsPlans = new ArrayList<>();

    public Invoice() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getDaysUntilExpiration() {
        return daysUntilExpiration;
    }

    public void setDaysUntilExpiration(Integer daysUntilExpiration) {
        this.daysUntilExpiration = daysUntilExpiration;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public List<InstallmentPlan> getInstallmentsPlans() {
        return installmentsPlans;
    }

    public void setInstallmentsPlans(List<InstallmentPlan> installmentsPlans) {
        this.installmentsPlans = installmentsPlans;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;
        return Objects.equals(getId(), invoice.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}