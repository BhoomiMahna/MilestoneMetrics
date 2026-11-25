package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A user's personalized financial plan, generated from their questionnaire /
 * financial profile and their savings goal. One plan per user.
 *
 * The weekly / monthly / yearly targets are the "planned savings" the progress
 * dashboard compares the user's actual savings against.
 */
@Entity
@Table(name = "financial_plans",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class FinancialPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Financial profile (from the questionnaire) ---
    @Column(name = "monthly_income")
    private Double monthlyIncome;

    @Column(name = "monthly_spend")
    private Double monthlySpend;

    @Column(name = "current_investments")
    private Double currentInvestments;

    @Column(name = "age_band", length = 20)
    private String ageBand;

    // --- Financial goal ---
    @Column(name = "goal_amount", nullable = false)
    private Double goalAmount;

    @Column(name = "timeline_years", nullable = false)
    private Double timelineYears;

    // --- Personalized plan (computed target savings per period) ---
    @Column(name = "weekly_target")
    private Double weeklyTarget;

    @Column(name = "monthly_target")
    private Double monthlyTarget;

    @Column(name = "yearly_target")
    private Double yearlyTarget;

    // --- Side-hustle profile (skills / interests / availability) ---
    // Skills and interests are stored as comma-separated values.
    @Column(name = "skills", length = 500)
    private String skills;

    @Column(name = "interests", length = 500)
    private String interests;

    @Column(name = "available_hours_per_week")
    private Integer availableHoursPerWeek;

    @Column(name = "desired_monthly_income")
    private Double desiredMonthlyIncome;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public FinancialPlan() {}

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public Double getWeeklyTarget() { return weeklyTarget; }
    public void setWeeklyTarget(Double weeklyTarget) { this.weeklyTarget = weeklyTarget; }

    public Double getMonthlyTarget() { return monthlyTarget; }
    public void setMonthlyTarget(Double monthlyTarget) { this.monthlyTarget = monthlyTarget; }

    public Double getYearlyTarget() { return yearlyTarget; }
    public void setYearlyTarget(Double yearlyTarget) { this.yearlyTarget = yearlyTarget; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public Integer getAvailableHoursPerWeek() { return availableHoursPerWeek; }
    public void setAvailableHoursPerWeek(Integer availableHoursPerWeek) { this.availableHoursPerWeek = availableHoursPerWeek; }

    public Double getDesiredMonthlyIncome() { return desiredMonthlyIncome; }
    public void setDesiredMonthlyIncome(Double desiredMonthlyIncome) { this.desiredMonthlyIncome = desiredMonthlyIncome; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
