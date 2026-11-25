package com.milestonemetrics.backend.entity;

/**
 * Categories of in-app notifications. All notifications are delivered in-app
 * only — there is no email / SMS / push integration in this prototype.
 */
public enum NotificationType {
    SAVINGS_REMINDER,
    BEHIND_TARGET,
    ACHIEVEMENT,
    GOAL_COMPLETED,
    COUPON_UNLOCKED,
    COUPON_REDEEMED,
    BILL_REMINDER,
    MILESTONE
}
