package com.milestonemetrics.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Questionnaire + financial goal submission used to generate / update the
 * user's personalized financial plan.
 */
public class PlanRequest {

    @NotNull(message = "Monthly income is required")
    @PositiveOrZero(message = "Monthly income cannot be negative")
    private Double monthlyIncome;

    @NotNull(message = "Monthly spending is required")
    @PositiveOrZero(message = "Monthly spending cannot be negative")
    private Double monthlySpend;

    @NotNull(message = "Current investments is required")
    @PositiveOrZero(message = "Current investments cannot be negative")
    private Double currentInvestments;

    private String ageBand;

    @NotNull(message = "Goal amount is required")
    @Positive(message = "Goal amount must be greater than zero")
    private Double goalAmount;

    @NotNull(message = "Timeline is required")
    @Positive(message = "Timeline must be greater than zero")
    private Double timelineYears;

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Double getMonthlySpend() { return monthlySpend; }
    public void setMonthlySpend(Double monthlySpend) { this.monthlySpend = monthlySpend; }

    public Double getCurrentInvestments() { return currentInvestments; }
    public void setCurrentInvestments(Double currentInvestments) { this.currentInvestments = currentInvestments; }

    public String getAgeBand() { return ageBand; }
    public void setAgeBand(String ageBand) { this.ageBand = ageBand; }

    public Double getGoalAmount() { return goalAmount; }
    public void setGoalAmount(Double goalAmount) { this.goalAmount = goalAmount; }

    public Double getTimelineYears() { return timelineYears; }
    public void setTimelineYears(Double timelineYears) { this.timelineYears = timelineYears; }
}
