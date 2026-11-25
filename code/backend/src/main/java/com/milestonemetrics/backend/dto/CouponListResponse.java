package com.milestonemetrics.backend.dto;

import java.util.List;

/** The user's current points plus the catalog with per-user status. */
public class CouponListResponse {

    private int points;
    private List<CouponResponse> coupons;

    public CouponListResponse(int points, List<CouponResponse> coupons) {
        this.points = points;
        this.coupons = coupons;
    }

    public int getPoints() { return points; }
    public List<CouponResponse> getCoupons() { return coupons; }
}
