package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.AchievementType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Everything the Rewards & Streaks dashboard section needs: total points, the
 * weekly streak figures, the full badge list (locked + unlocked) and the
 * progress toward the next achievement.
 */
public class GamificationSummaryResponse {

    private int totalPoints;
    private int currentWeeklyStreak;
    private int longestWeeklyStreak;
    private int qualifyingWeeks;   // total weeks where target was met
    private List<AchievementView> achievements;
    private NextAchievement nextAchievement; // null when everything is unlocked

    public GamificationSummaryResponse(int totalPoints, int currentWeeklyStreak,
                                       int longestWeeklyStreak, int qualifyingWeeks,
                                       List<AchievementView> achievements,
                                       NextAchievement nextAchievement) {
        this.totalPoints = totalPoints;
        this.currentWeeklyStreak = currentWeeklyStreak;
        this.longestWeeklyStreak = longestWeeklyStreak;
        this.qualifyingWeeks = qualifyingWeeks;
        this.achievements = achievements;
        this.nextAchievement = nextAchievement;
    }

    public int getTotalPoints() { return totalPoints; }
    public int getCurrentWeeklyStreak() { return currentWeeklyStreak; }
    public int getLongestWeeklyStreak() { return longestWeeklyStreak; }
    public int getQualifyingWeeks() { return qualifyingWeeks; }
    public List<AchievementView> getAchievements() { return achievements; }
    public NextAchievement getNextAchievement() { return nextAchievement; }

    public static class AchievementView {
        private String code;
        private String title;
        private String description;
        private int points;
        private boolean unlocked;
        private LocalDateTime awardedAt;

        public AchievementView(AchievementType type, boolean unlocked, LocalDateTime awardedAt) {
            this.code = type.name();
            this.title = type.getTitle();
            this.description = type.getDescription();
            this.points = type.getPoints();
            this.unlocked = unlocked;
            this.awardedAt = awardedAt;
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
        public boolean isUnlocked() { return unlocked; }
        public LocalDateTime getAwardedAt() { return awardedAt; }
    }

    public static class NextAchievement {
        private String code;
        private String title;
        private String description;
        private double progressCurrent;
        private double progressTarget;
        private double progressPercent; // 0..100

        public NextAchievement(AchievementType type, double progressCurrent, double progressTarget) {
            this.code = type.name();
            this.title = type.getTitle();
            this.description = type.getDescription();
            this.progressCurrent = progressCurrent;
            this.progressTarget = progressTarget;
            this.progressPercent = progressTarget > 0
                    ? Math.min(Math.round((progressCurrent / progressTarget) * 1000.0) / 10.0, 100.0)
                    : 0.0;
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public double getProgressCurrent() { return progressCurrent; }
        public double getProgressTarget() { return progressTarget; }
        public double getProgressPercent() { return progressPercent; }
    }
}
