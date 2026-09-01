package com.milestonemetrics.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class QuestionnaireRequest {

    @NotNull(message = "Monthly income is required")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Monthly income cannot be negative"
    )
    private BigDecimal monthlyIncome;

    @NotNull(message = "Monthly spend is required")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Monthly spend cannot be negative"
    )
    private BigDecimal monthlySpend;

    @NotNull(message = "Current investments are required")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Current investments cannot be negative"
    )
    private BigDecimal currentInvestments;

    @NotBlank(message = "Age group is required")
    private String ageGroup;

    @NotNull(message = "Goal amount is required")
    @DecimalMin(
            value = "0.01",
            inclusive = true,
            message = "Goal amount must be greater than zero"
    )
    private BigDecimal goalAmount;

    @NotNull(message = "Timeline is required")
    @Min(value = 1, message = "Timeline must be at least 1 year")
    @Max(value = 100, message = "Timeline cannot exceed 100 years")
    private Integer timelineYears;

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
}