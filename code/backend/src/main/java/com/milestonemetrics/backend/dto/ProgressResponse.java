package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.PeriodType;

import java.util.List;

/**
 * Everything the Financial Progress dashboard needs for one period view
 * (weekly / monthly / yearly).
 */
public class ProgressResponse {

    private PeriodType periodType;
    private Double goalAmount;
    private Double targetPerPeriod;   // planned savings for a single period of this type
    private Double totalTarget;       // sum of the target snapshots across logged periods
    private Double totalActual;       // sum of actual savings across logged periods
    private Double remainingToGoal;   // goalAmount - totalActual (>= 0)
    private Double goalPercent;       // totalActual / goalAmount * 100 (0..100)
    private List<SavingsRecordResponse> records;

    public ProgressResponse(PeriodType periodType,
                            Double goalAmount,
                            Double targetPerPeriod,
                            Double totalTarget,
                            Double totalActual,
                            Double remainingToGoal,
                            Double goalPercent,
                            List<SavingsRecordResponse> records) {
        this.periodType = periodType;
        this.goalAmount = goalAmount;
        this.targetPerPeriod = targetPerPeriod;
        this.totalTarget = totalTarget;
        this.totalActual = totalActual;
        this.remainingToGoal = remainingToGoal;
        this.goalPercent = goalPercent;
        this.records = records;
    }

    public PeriodType getPeriodType() { return periodType; }
    public Double getGoalAmount() { return goalAmount; }
    public Double getTargetPerPeriod() { return targetPerPeriod; }
    public Double getTotalTarget() { return totalTarget; }
    public Double getTotalActual() { return totalActual; }
    public Double getRemainingToGoal() { return remainingToGoal; }
    public Double getGoalPercent() { return goalPercent; }
    public List<SavingsRecordResponse> getRecords() { return records; }
}
