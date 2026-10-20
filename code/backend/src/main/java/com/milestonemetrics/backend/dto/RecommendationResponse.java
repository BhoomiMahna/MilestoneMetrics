package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.Difficulty;

import java.util.List;

/**
 * A single ranked side-hustle recommendation with everything the frontend card
 * needs, plus the match score and an explainable "why recommended" reason.
 */
public class RecommendationResponse {

    private Long id;
    private String name;
    private String description;
    private List<String> requiredSkills;
    private List<String> interestTags;
    private Double minMonthlyEarning;
    private Double maxMonthlyEarning;
    private Integer minHoursPerWeek;
    private Integer maxHoursPerWeek;
    private Difficulty difficulty;
    private Boolean online;
    private String platformName;
    private String platformUrl;

    private Integer matchScore;          // 0..100
    private List<String> matchedSkills;  // skills the user has that this needs
    private String whyRecommended;       // human-readable explanation
    private Boolean bookmarked;

    public RecommendationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getInterestTags() { return interestTags; }
    public void setInterestTags(List<String> interestTags) { this.interestTags = interestTags; }

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

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public String getWhyRecommended() { return whyRecommended; }
    public void setWhyRecommended(String whyRecommended) { this.whyRecommended = whyRecommended; }

    public Boolean getBookmarked() { return bookmarked; }
    public void setBookmarked(Boolean bookmarked) { this.bookmarked = bookmarked; }
}
