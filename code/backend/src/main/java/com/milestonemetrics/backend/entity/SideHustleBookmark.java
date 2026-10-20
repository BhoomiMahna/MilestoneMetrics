package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A side-hustle opportunity a user has saved / bookmarked. One row per
 * (user, opportunity).
 */
@Entity
@Table(name = "side_hustle_bookmarks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "opportunity_id"}))
public class SideHustleBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private SideHustleOpportunity opportunity;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SideHustleBookmark() {}

    public SideHustleBookmark(User user, SideHustleOpportunity opportunity) {
        this.user = user;
        this.opportunity = opportunity;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SideHustleOpportunity getOpportunity() { return opportunity; }
    public void setOpportunity(SideHustleOpportunity opportunity) { this.opportunity = opportunity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
