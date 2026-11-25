package com.milestonemetrics.backend.entity;

/**
 * The fixed catalog of achievements a user can earn. Each is awarded at most
 * once per user (enforced by a unique constraint on UserAchievement) and only
 * when genuinely satisfied by the user's real savings records.
 */
public enum AchievementType {

    FIRST_SAVINGS("First Steps", "Logged your first savings entry", 50),
    FIRST_TARGET_MET("Target Hit", "Met a savings target for the first time", 75),
    WEEK_STREAK_4("On a Roll", "Hit your weekly target 4 weeks in a row", 150),
    WEEK_STREAK_8("Unstoppable", "Hit your weekly target 8 weeks in a row", 300),
    GOAL_REACHED("Goal Crusher", "Saved your full goal amount", 500);

    private final String title;
    private final String description;
    private final int points;

    AchievementType(String title, String description, int points) {
        this.title = title;
        this.description = description;
        this.points = points;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPoints() { return points; }
}
