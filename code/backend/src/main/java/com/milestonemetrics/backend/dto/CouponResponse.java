package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.Coupon;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponResponse {

    /** LOCKED, AVAILABLE, REDEEMED, EXPIRED. */
    private Long id;
    private String title;
    private String description;
    private String rewardLabel;
    private String category;
    private int pointsRequired;
    private LocalDate expiryDate;
    private boolean demo;
    private String status;
    private String codeIssued;        // only present once redeemed
    private LocalDateTime redeemedAt;  // only present once redeemed

    public CouponResponse(Coupon c, String status, String codeIssued, LocalDateTime redeemedAt) {
        this.id = c.getId();
        this.title = c.getTitle();
        this.description = c.getDescription();
        this.rewardLabel = c.getRewardLabel();
        this.category = c.getCategory();
        this.pointsRequired = c.getPointsRequired();
        this.expiryDate = c.getExpiryDate();
        this.demo = c.isDemo();
        this.status = status;
        this.codeIssued = codeIssued;
        this.redeemedAt = redeemedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRewardLabel() { return rewardLabel; }
    public String getCategory() { return category; }
    public int getPointsRequired() { return pointsRequired; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isDemo() { return demo; }
    public String getStatus() { return status; }
    public String getCodeIssued() { return codeIssued; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
}
