package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.FinancialPlan;

/**
 * The user's personalized financial plan, including the computed target
 * savings for each period view.
 */
public class PlanResponse {

    private Long id;
    private Double monthlyIncome;
    private Double monthlySpend;
    private Double currentInvestments;
    private String ageBand;
    private Double goalAmount;
    private Double timelineYears;
    private Double weeklyTarget;
    private Double monthlyTarget;
    private Double yearlyTarget;

    public PlanResponse(FinancialPlan plan) {
        this.id = plan.getId();
        this.monthlyIncome = plan.getMonthlyIncome();
        this.monthlySpend = plan.getMonthlySpend();
        this.currentInvestments = plan.getCurrentInvestments();
        this.ageBand = plan.getAgeBand();
        this.goalAmount = plan.getGoalAmount();
        this.timelineYears = plan.getTimelineYears();
        this.weeklyTarget = plan.getWeeklyTarget();
        this.monthlyTarget = plan.getMonthlyTarget();
        this.yearlyTarget = plan.getYearlyTarget();
    }

    public Long getId() { return id; }
    public Double getMonthlyIncome() { return monthlyIncome; }
    public Double getMonthlySpend() { return monthlySpend; }
    public Double getCurrentInvestments() { return currentInvestments; }
    public String getAgeBand() { return ageBand; }
    public Double getGoalAmount() { return goalAmount; }
    public Double getTimelineYears() { return timelineYears; }
    public Double getWeeklyTarget() { return weeklyTarget; }
    public Double getMonthlyTarget() { return monthlyTarget; }
    public Double getYearlyTarget() { return yearlyTarget; }
}
