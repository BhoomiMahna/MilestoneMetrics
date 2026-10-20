package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A reward / offer in the platform's curated demonstration catalog. These are
 * prototype offers only — {@code demo} is always true and codes are not
 * redeemable with any real merchant. There is no third-party coupon integration.
 */
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    /** Human-readable reward, e.g. "₹200 off" or "1 month free". */
    @Column(name = "reward_label", nullable = false, length = 120)
    private String rewardLabel;

    @Column(length = 60)
    private String category;

    /** Points the user must have earned to unlock this offer. */
    @Column(name = "points_required", nullable = false)
    private int pointsRequired = 0;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(nullable = false)
    private boolean active = true;

    /** Always true in this prototype — a demonstration coupon, not a real one. */
    @Column(nullable = false)
    private boolean demo = true;

    /** Demo code revealed to the user on redemption. */
    @Column(name = "demo_code", length = 60)
    private String demoCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Coupon() {}

    public Long getId() { return id; }

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isDemo() { return demo; }
    public void setDemo(boolean demo) { this.demo = demo; }

    public String getDemoCode() { return demoCode; }
    public void setDemoCode(String demoCode) { this.demoCode = demoCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
