package com.milestonemetrics.backend.dto;

import java.util.List;

/**
 * Educational, rule-based investment information derived from the user's
 * questionnaire/plan. This is general financial education, NOT regulated,
 * personalized financial advice, and contains no live market data.
 */
public class InvestmentRecommendationResponse {

    private ProfileSummary profile;
    private List<String> missingInfo;
    private List<Suggestion> suggestions;
    private String disclaimer;

    public InvestmentRecommendationResponse(ProfileSummary profile, List<String> missingInfo,
                                            List<Suggestion> suggestions, String disclaimer) {
        this.profile = profile;
        this.missingInfo = missingInfo;
        this.suggestions = suggestions;
        this.disclaimer = disclaimer;
    }

    public ProfileSummary getProfile() { return profile; }
    public List<String> getMissingInfo() { return missingInfo; }
    public List<Suggestion> getSuggestions() { return suggestions; }
    public String getDisclaimer() { return disclaimer; }

    public static class ProfileSummary {
        private Double monthlyIncome;
        private Double monthlySpend;
        private Double monthlySurplus;
        private Double timelineYears;
        private String ageBand;
        private String riskProfile; // Conservative / Moderate / Growth / Unknown

        public ProfileSummary(Double monthlyIncome, Double monthlySpend, Double monthlySurplus,
                              Double timelineYears, String ageBand, String riskProfile) {
            this.monthlyIncome = monthlyIncome;
            this.monthlySpend = monthlySpend;
            this.monthlySurplus = monthlySurplus;
            this.timelineYears = timelineYears;
            this.ageBand = ageBand;
            this.riskProfile = riskProfile;
        }
        public Double getMonthlyIncome() { return monthlyIncome; }
        public Double getMonthlySpend() { return monthlySpend; }
        public Double getMonthlySurplus() { return monthlySurplus; }
        public Double getTimelineYears() { return timelineYears; }
        public String getAgeBand() { return ageBand; }
        public String getRiskProfile() { return riskProfile; }
    }

    public static class Suggestion {
        private String name;
        private String category;
        private String riskLevel;   // LOW / MEDIUM / HIGH
        private String horizonFit;  // e.g. "Any", "Short (<2y)", "Long (5y+)"
        private String whyRelevant;
        private String educationalNote;
        private List<String> exampleVehicles;

        public Suggestion(String name, String category, String riskLevel, String horizonFit,
                          String whyRelevant, String educationalNote, List<String> exampleVehicles) {
            this.name = name;
            this.category = category;
            this.riskLevel = riskLevel;
            this.horizonFit = horizonFit;
            this.whyRelevant = whyRelevant;
            this.educationalNote = educationalNote;
            this.exampleVehicles = exampleVehicles;
        }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getRiskLevel() { return riskLevel; }
        public String getHorizonFit() { return horizonFit; }
        public String getWhyRelevant() { return whyRelevant; }
        public String getEducationalNote() { return educationalNote; }
        public List<String> getExampleVehicles() { return exampleVehicles; }
    }
}
