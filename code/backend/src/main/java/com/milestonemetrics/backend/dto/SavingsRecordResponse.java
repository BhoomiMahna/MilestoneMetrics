package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.PeriodType;
import com.milestonemetrics.backend.entity.SavingsRecord;

import java.time.LocalDate;

/**
 * One historical savings entry with its per-period progress figures.
 */
public class SavingsRecordResponse {

    private Long id;
    private PeriodType periodType;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String periodLabel;
    private Double targetAmount;
    private Double actualAmount;
    private Double remaining;
    private Double percent;

    public SavingsRecordResponse(SavingsRecord record) {
        this.id = record.getId();
        this.periodType = record.getPeriodType();
        this.periodStart = record.getPeriodStart();
        this.periodEnd = record.getPeriodEnd();
        this.periodLabel = record.getPeriodLabel();
        this.targetAmount = record.getTargetAmount();
        this.actualAmount = record.getActualAmount();

        double target = targetAmount != null ? targetAmount : 0.0;
        double actual = actualAmount != null ? actualAmount : 0.0;
        this.remaining = Math.max(target - actual, 0.0);
        this.percent = target > 0 ? Math.round((actual / target) * 1000.0) / 10.0 : 0.0;
    }

    public Long getId() { return id; }
    public PeriodType getPeriodType() { return periodType; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public String getPeriodLabel() { return periodLabel; }
    public Double getTargetAmount() { return targetAmount; }
    public Double getActualAmount() { return actualAmount; }
    public Double getRemaining() { return remaining; }
    public Double getPercent() { return percent; }
}
