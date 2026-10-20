package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;

/** Admin create/update payload for a side-hustle opportunity. */
public class OpportunityAdminRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private String requiredSkills;
    private String interestTags;
    private Double minMonthlyEarning;
    private Double maxMonthlyEarning;
    private Integer minHoursPerWeek;
    private Integer maxHoursPerWeek;
    private Difficulty difficulty;
    private Boolean online;
    private String platformName;
    private String platformUrl;

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
