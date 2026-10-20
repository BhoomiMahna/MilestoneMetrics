package com.milestonemetrics.backend.dto;

import java.time.LocalDateTime;

public class RedeemResponse {

    private Long couponId;
    private String title;
    private String codeIssued;
    private LocalDateTime redeemedAt;
    private String message;

    public RedeemResponse(Long couponId, String title, String codeIssued,
                          LocalDateTime redeemedAt, String message) {
        this.couponId = couponId;
        this.title = title;
        this.codeIssued = codeIssued;
        this.redeemedAt = redeemedAt;
        this.message = message;
    }

    public Long getCouponId() { return couponId; }
    public String getTitle() { return title; }
    public String getCodeIssued() { return codeIssued; }
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
    public String getMessage() { return message; }
}
