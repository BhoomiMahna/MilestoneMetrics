package com.milestonemetrics.backend.entity;

import jakarta.persistence.*;

/**
 * A side-hustle / income opportunity in the platform's catalog. This is the
 * database-backed stand-in for the "Side-Hustle Platforms (API / Scraping)"
 * external integration shown in the system design — the recommendation engine
 * ranks these rows against a user's profile.
 *
 * {@code requiredSkills} and {@code interestTags} are stored as comma-separated
 * values so they can be matched against the user's comma-separated skills /
 * interests without an extra join table.
 */
@Entity
@Table(name = "side_hustle_opportunities")
public class SideHustleOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "required_skills", length = 400)
    private String requiredSkills;

    @Column(name = "interest_tags", length = 400)
    private String interestTags;

    @Column(name = "min_monthly_earning")
    private Double minMonthlyEarning;

    @Column(name = "max_monthly_earning")
    private Double maxMonthlyEarning;

    @Column(name = "min_hours_per_week")
    private Integer minHoursPerWeek;

    @Column(name = "max_hours_per_week")
    private Integer maxHoursPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Column(name = "is_online")
    private Boolean online;

    @Column(name = "platform_name", length = 120)
    private String platformName;

    @Column(name = "platform_url", length = 300)
    private String platformUrl;

    public SideHustleOpportunity() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getInterestTags() { return interestTags; }
    public void setInterestTags(String interestTags) { this.interestTags = interestTags; }

    public Double getMinMonthlyEarning() { return minMonthlyEarning; }
    public void setMinMonthlyEarning(Double minMonthlyEarning) { this.minMonthlyEarning = minMonthlyEarning; }

    public Double getMaxMonthlyEarning() { return maxMonthlyEarning; }
    public void setMaxMonthlyEarning(Double maxMonthlyEarning) { this.maxMonthlyEarning = maxMonthlyEarning; }

    public Integer getMinHoursPerWeek() { return minHoursPerWeek; }
    public void setMinHoursPerWeek(Integer minHoursPerWeek) { this.minHoursPerWeek = minHoursPerWeek; }

    public Integer getMaxHoursPerWeek() { return maxHoursPerWeek; }
    public void setMaxHoursPerWeek(Integer maxHoursPerWeek) { this.maxHoursPerWeek = maxHoursPerWeek; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getPlatformUrl() { return platformUrl; }
    public void setPlatformUrl(String platformUrl) { this.platformUrl = platformUrl; }
}
