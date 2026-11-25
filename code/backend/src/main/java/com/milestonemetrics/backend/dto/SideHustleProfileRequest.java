package com.milestonemetrics.backend.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * The user's editable side-hustle inputs: skills, interests, available time
 * per week and how much extra monthly income they want.
 */
public class SideHustleProfileRequest {

    private List<String> skills;
    private List<String> interests;

    @PositiveOrZero(message = "Available hours cannot be negative")
    private Integer availableHoursPerWeek;

    @PositiveOrZero(message = "Desired income cannot be negative")
    private Double desiredMonthlyIncome;

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public Integer getAvailableHoursPerWeek() { return availableHoursPerWeek; }
    public void setAvailableHoursPerWeek(Integer availableHoursPerWeek) { this.availableHoursPerWeek = availableHoursPerWeek; }

    public Double getDesiredMonthlyIncome() { return desiredMonthlyIncome; }
    public void setDesiredMonthlyIncome(Double desiredMonthlyIncome) { this.desiredMonthlyIncome = desiredMonthlyIncome; }
}
