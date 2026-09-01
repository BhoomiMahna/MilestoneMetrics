package com.milestonemetrics.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuestionnaireResponse {

    private Long id;

    private BigDecimal monthlyIncome;
    private BigDecimal monthlySpend;
    private BigDecimal currentInvestments;
    private String ageGroup;
    private BigDecimal goalAmount;
    private Integer timelineYears;

    private Integer totalMonths;
    private BigDecimal monthlyTarget;
    private BigDecimal monthlySurplus;
    private boolean feasible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlySpend() {
        return monthlySpend;
    }

    public void setMonthlySpend(BigDecimal monthlySpend) {
        this.monthlySpend = monthlySpend;
    }

    public BigDecimal getCurrentInvestments() {
        return currentInvestments;
    }

    public void setCurrentInvestments(BigDecimal currentInvestments) {
        this.currentInvestments = currentInvestments;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public BigDecimal getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(BigDecimal goalAmount) {
        this.goalAmount = goalAmount;
    }

    public Integer getTimelineYears() {
        return timelineYears;
    }

    public void setTimelineYears(Integer timelineYears) {
        this.timelineYears = timelineYears;
    }

    public Integer getTotalMonths() {
        return totalMonths;
    }

    public void setTotalMonths(Integer totalMonths) {
        this.totalMonths = totalMonths;
    }

    public BigDecimal getMonthlyTarget() {
        return monthlyTarget;
    }

    public void setMonthlyTarget(BigDecimal monthlyTarget) {
        this.monthlyTarget = monthlyTarget;
    }

    public BigDecimal getMonthlySurplus() {
        return monthlySurplus;
    }

    public void setMonthlySurplus(BigDecimal monthlySurplus) {
        this.monthlySurplus = monthlySurplus;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
