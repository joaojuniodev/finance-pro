package br.com.financepro.financePro.installment.model;

import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "installments")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal value;

    @Column
    private Boolean isPaid;

    @Column
    private Integer month;

    @Column
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "installment_plan_id")
    private InstallmentPlan installmentPlan;

    public Installment() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public InstallmentPlan getInstallmentPlan() {
        return installmentPlan;
    }

    public void setInstallmentPlan(InstallmentPlan installmentPlan) {
        this.installmentPlan = installmentPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Installment that = (Installment) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}