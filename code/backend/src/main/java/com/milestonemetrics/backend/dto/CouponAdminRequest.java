package com.milestonemetrics.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/** Admin create/update payload for a coupon/offer. */
public class CouponAdminRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Reward label is required")
    private String rewardLabel;

    private String category;

    @PositiveOrZero(message = "Points required cannot be negative")
    private int pointsRequired;

    private LocalDate expiryDate;

    private Boolean active;

    private String demoCode;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRewardLabel() { return rewardLabel; }
    public void setRewardLabel(String rewardLabel) { this.rewardLabel = rewardLabel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDemoCode() { return demoCode; }
    public void setDemoCode(String demoCode) { this.demoCode = demoCode; }
}
