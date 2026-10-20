package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A badge/achievement a user has earned. One row per (user, type) — the unique
 * constraint guarantees points and badges are never awarded twice for the same
 * achievement, even if savings records are edited and re-evaluated.
 */
@Entity
@Table(name = "user_achievements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "type"}))
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AchievementType type;

    @Column(name = "points_awarded", nullable = false)
    private int pointsAwarded;

    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt = LocalDateTime.now();

    public UserAchievement() {}

    public UserAchievement(User user, AchievementType type) {
        this.user = user;
        this.type = type;
        this.pointsAwarded = type.getPoints();
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }

    public LocalDateTime getAwardedAt() { return awardedAt; }
    public void setAwardedAt(LocalDateTime awardedAt) { this.awardedAt = awardedAt; }
}
