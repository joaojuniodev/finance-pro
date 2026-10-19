package br.com.financepro.financePro.financialProfile.model;

import br.com.financepro.financePro.security.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "financial_profiles")
public class FinancialProfile {

    // Perfil Financeiro do usuário é calculado por 6 dimensões:
    // 1. Segurança finaneira
    // 2. Resiliência financeira
    // 3. Organização e controle
    // 4. Regulação de impulsos
    // 5. Orientação para o futuro
    // 6. Equilíbrio e liberdade

    // Cada dimensão é registro por pontos de 0 a 100;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "financial_trend", columnDefinition = "jsonb")
    private Map<String, String> financialTrend = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "financial_moment", columnDefinition = "jsonb")
    private Map<String, String> financialMoment = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "main_strength", columnDefinition = "jsonb")
    private Map<String, String> mainStrength = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_opportunity", columnDefinition = "jsonb")
    private Map<String, String> keyOpportunity = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "current_area_of_focus", columnDefinition = "jsonb")
    private Map<String, String> currentAreaOfFocus = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "confidence_in_the_result", columnDefinition = "jsonb")
    private Map<String, String> confidenceInTheResult = new HashMap<>();

    // Deve combinar quatro informações:
    // 1. Principal Força
    // 2. Tendência comportamental
    // 3. O momento financeiro
    // 4. Principal oportunidade ou atenção atual
    @Column(name = "interpretive_synthesis")
    private String interpretiveSynthesis;

    @Column(name = "general_financial_index")
    private Integer generalFinancialIndex; // índice financeiro geral = (soma de todas as dimensões) / 6

    @Column(name = "financial_security")
    private Integer financialSecurity;

    @Column(name = "financial_resilience")
    private Integer financialResilience;

    @Column(name = "organization_and_control")
    private Integer organizationAndControl;

    @Column(name = "impulse_regulation")
    private Integer impulseRegulation;

    @Column(name = "orientation_the_future")
    private Integer orientationTheFuture;

    @Column(name = "balance_and_freedom")
    private Integer balanceAndFreedom;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public FinancialProfile() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getFinancialTrend() {
        return financialTrend;
    }

    public void setFinancialTrend(Map<String, String> financialTrend) {
        this.financialTrend = financialTrend;
    }

    public Map<String, String> getFinancialMoment() {
        return financialMoment;
    }

    public void setFinancialMoment(Map<String, String> financialMoment) {
        this.financialMoment = financialMoment;
    }

    public Map<String, String> getMainStrength() {
        return mainStrength;
    }

    public void setMainStrength(Map<String, String> mainStrength) {
        this.mainStrength = mainStrength;
    }

    public Map<String, String> getKeyOpportunity() {
        return keyOpportunity;
    }

    public void setKeyOpportunity(Map<String, String> keyOpportunity) {
        this.keyOpportunity = keyOpportunity;
    }

    public Map<String, String> getCurrentAreaOfFocus() {
        return currentAreaOfFocus;
    }

    public void setCurrentAreaOfFocus(Map<String, String> currentAreaOfFocus) {
        this.currentAreaOfFocus = currentAreaOfFocus;
    }

    public Map<String, String> getConfidenceInTheResult() {
        return confidenceInTheResult;
    }

    public void setConfidenceInTheResult(Map<String, String> confidenceInTheResult) {
        this.confidenceInTheResult = confidenceInTheResult;
    }

    public String getInterpretiveSynthesis() {
        return interpretiveSynthesis;
    }

    public void setInterpretiveSynthesis(String interpretiveSynthesis) {
        this.interpretiveSynthesis = interpretiveSynthesis;
    }

    public Integer getGeneralFinancialIndex() {
        return generalFinancialIndex;
    }

    public void setGeneralFinancialIndex(Integer generalFinancialIndex) {
        this.generalFinancialIndex = generalFinancialIndex;
    }

    public Integer getFinancialSecurity() {
        return financialSecurity;
    }

    public void setFinancialSecurity(Integer financialSecurity) {
        this.financialSecurity = financialSecurity;
    }

    public Integer getFinancialResilience() {
        return financialResilience;
    }

    public void setFinancialResilience(Integer financialResilience) {
        this.financialResilience = financialResilience;
    }

    public Integer getOrganizationAndControl() {
        return organizationAndControl;
    }

    public void setOrganizationAndControl(Integer organizationAndControl) {
        this.organizationAndControl = organizationAndControl;
    }

    public Integer getImpulseRegulation() {
        return impulseRegulation;
    }

    public void setImpulseRegulation(Integer impulseRegulation) {
        this.impulseRegulation = impulseRegulation;
    }

    public Integer getOrientationTheFuture() {
        return orientationTheFuture;
    }

    public void setOrientationTheFuture(Integer orientationTheFuture) {
        this.orientationTheFuture = orientationTheFuture;
    }

    public Integer getBalanceAndFreedom() {
        return balanceAndFreedom;
    }

    public void setBalanceAndFreedom(Integer balanceAndFreedom) {
        this.balanceAndFreedom = balanceAndFreedom;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        FinancialProfile that = (FinancialProfile) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}