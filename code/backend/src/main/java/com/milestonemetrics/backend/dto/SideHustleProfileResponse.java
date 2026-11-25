package com.milestonemetrics.backend.dto;

import java.util.List;

/**
 * The user's current side-hustle profile, echoed back to the frontend so it can
 * pre-fill the "update skills / interests / available time" form.
 */
public class SideHustleProfileResponse {

    private List<String> skills;
    private List<String> interests;
    private Integer availableHoursPerWeek;
    private Double desiredMonthlyIncome;

    public SideHustleProfileResponse(List<String> skills,
                                     List<String> interests,
                                     Integer availableHoursPerWeek,
                                     Double desiredMonthlyIncome) {
        this.skills = skills;
        this.interests = interests;
        this.availableHoursPerWeek = availableHoursPerWeek;
        this.desiredMonthlyIncome = desiredMonthlyIncome;
    }

    public List<String> getSkills() { return skills; }
    public List<String> getInterests() { return interests; }
    public Integer getAvailableHoursPerWeek() { return availableHoursPerWeek; }
    public Double getDesiredMonthlyIncome() { return desiredMonthlyIncome; }
}
