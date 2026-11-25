package com.milestonemetrics.backend.dto;

/** Aggregate, non-personal system statistics for the admin dashboard. */
public class AdminAnalyticsResponse {

    private long userCount;
    private long adminCount;
    private long planCount;
    private long savingsRecordCount;
    private long goalsAchieved;
    private long achievementsAwarded;
    private long couponCount;
    private long activeCouponCount;
    private long couponRedemptions;

    public AdminAnalyticsResponse(long userCount, long adminCount, long planCount,
                                  long savingsRecordCount, long goalsAchieved,
                                  long achievementsAwarded, long couponCount,
                                  long activeCouponCount, long couponRedemptions) {
        this.userCount = userCount;
        this.adminCount = adminCount;
        this.planCount = planCount;
        this.savingsRecordCount = savingsRecordCount;
        this.goalsAchieved = goalsAchieved;
        this.achievementsAwarded = achievementsAwarded;
        this.couponCount = couponCount;
        this.activeCouponCount = activeCouponCount;
        this.couponRedemptions = couponRedemptions;
    }

    public long getUserCount() { return userCount; }
    public long getAdminCount() { return adminCount; }
    public long getPlanCount() { return planCount; }
    public long getSavingsRecordCount() { return savingsRecordCount; }
    public long getGoalsAchieved() { return goalsAchieved; }
    public long getAchievementsAwarded() { return achievementsAwarded; }
    public long getCouponCount() { return couponCount; }
    public long getActiveCouponCount() { return activeCouponCount; }
    public long getCouponRedemptions() { return couponRedemptions; }
}
