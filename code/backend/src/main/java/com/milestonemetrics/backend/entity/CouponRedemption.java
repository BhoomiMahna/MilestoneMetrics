package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Records that a user redeemed a coupon. The unique (user_id, coupon_id)
 * constraint prevents a user from redeeming the same offer more than once.
 */
@Entity
@Table(name = "coupon_redemptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"}))
public class CouponRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "code_issued", length = 60)
    private String codeIssued;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt = LocalDateTime.now();

    public CouponRedemption() {}

    public CouponRedemption(User user, Coupon coupon, String codeIssued) {
        this.user = user;
        this.coupon = coupon;
        this.codeIssued = codeIssued;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Coupon getCoupon() { return coupon; }
    public String getCodeIssued() { return codeIssued; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
}
