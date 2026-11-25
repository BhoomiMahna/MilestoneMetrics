package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A single actual-savings entry logged by a user for one period
 * (a specific week, month or year). {@code targetAmount} is a snapshot of the
 * planned savings for that period at the time the entry was made, so historical
 * progress stays stable even if the plan later changes.
 *
 * One entry per (user, periodType, periodStart) — logging the same period again
 * updates the existing entry.
 */
@Entity
@Table(name = "savings_records",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "period_type", "period_start"}))
public class SavingsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 10)
    private PeriodType periodType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "period_label", nullable = false, length = 60)
    private String periodLabel;

    @Column(name = "target_amount")
    private Double targetAmount;

    @Column(name = "actual_amount", nullable = false)
    private Double actualAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SavingsRecord() {}

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public PeriodType getPeriodType() { return periodType; }
    public void setPeriodType(PeriodType periodType) { this.periodType = periodType; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }

    public Double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(Double targetAmount) { this.targetAmount = targetAmount; }

    public Double getActualAmount() { return actualAmount; }
    public void setActualAmount(Double actualAmount) { this.actualAmount = actualAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
