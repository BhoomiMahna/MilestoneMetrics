package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.RecommendationResponse;
import com.milestonemetrics.backend.dto.SideHustleProfileRequest;
import com.milestonemetrics.backend.dto.SideHustleProfileResponse;
import com.milestonemetrics.backend.entity.FinancialPlan;
import com.milestonemetrics.backend.entity.SideHustleBookmark;
import com.milestonemetrics.backend.entity.SideHustleOpportunity;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.SideHustleBookmarkRepository;
import com.milestonemetrics.backend.repository.SideHustleOpportunityRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SideHustleService {

    private final SideHustleOpportunityRepository opportunityRepository;
    private final SideHustleBookmarkRepository bookmarkRepository;
    private final FinancialPlanRepository planRepository;
    private final UserRepository userRepository;

    // Ranking weights (sum = 100).
    private static final double W_SKILL = 45.0;
    private static final double W_EARNING = 25.0;
    private static final double W_TIME = 20.0;
    private static final double W_INTEREST = 10.0;

    public SideHustleService(SideHustleOpportunityRepository opportunityRepository,
                             SideHustleBookmarkRepository bookmarkRepository,
                             FinancialPlanRepository planRepository,
                             UserRepository userRepository) {
        this.opportunityRepository = opportunityRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    // ---------------- profile ----------------

    public SideHustleProfileResponse getProfile(String email) {
        FinancialPlan plan = requirePlan(email);
        return toProfileResponse(plan);
    }

    public SideHustleProfileResponse updateProfile(String email, SideHustleProfileRequest request) {
        FinancialPlan plan = requirePlan(email);
        if (request.getSkills() != null) {
            plan.setSkills(joinCsv(request.getSkills()));
        }
        if (request.getInterests() != null) {
            plan.setInterests(joinCsv(request.getInterests()));
        }
        if (request.getAvailableHoursPerWeek() != null) {
            plan.setAvailableHoursPerWeek(request.getAvailableHoursPerWeek());
        }
        if (request.getDesiredMonthlyIncome() != null) {
            plan.setDesiredMonthlyIncome(request.getDesiredMonthlyIncome());
        }
        plan.setUpdatedAt(LocalDateTime.now());
        planRepository.save(plan);
        return toProfileResponse(plan);
    }

    // ---------------- recommendations ----------------

    public List<RecommendationResponse> getRecommendations(String email) {
        User user = requireUser(email);
        FinancialPlan plan = planRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complete the questionnaire first to get side-hustle recommendations"));

        List<String> userSkills = parseCsv(plan.getSkills());
        List<String> userInterests = parseCsv(plan.getInterests());
        Set<String> skillSet = lowerSet(userSkills);
        Set<String> interestSet = lowerSet(userInterests);
        int availableHours = plan.getAvailableHoursPerWeek() != null ? plan.getAvailableHoursPerWeek() : 0;
        double desiredIncome = resolveDesiredIncome(plan);

        Set<Long> bookmarkedIds = bookmarkRepository.findByUser(user).stream()
                .map(b -> b.getOpportunity().getId())
                .collect(Collectors.toSet());

        List<RecommendationResponse> results = new ArrayList<>();
        for (SideHustleOpportunity opp : opportunityRepository.findAll()) {
            results.add(rank(opp, skillSet, interestSet, availableHours, desiredIncome, bookmarkedIds));
        }
        results.sort(Comparator.comparingInt(RecommendationResponse::getMatchScore).reversed());
        return results;
    }

    private RecommendationResponse rank(SideHustleOpportunity opp,
                                        Set<String> skillSet,
                                        Set<String> interestSet,
                                        int availableHours,
                                        double desiredIncome,
                                        Set<Long> bookmarkedIds) {
        List<String> required = parseCsv(opp.getRequiredSkills());
        List<String> tags = parseCsv(opp.getInterestTags());

        // Skill match
        List<String> matchedSkills = required.stream()
                .filter(s -> skillSet.contains(s.toLowerCase().trim()))
                .collect(Collectors.toList());
        double skillFit = required.isEmpty()
                ? 0.5
                : (double) matchedSkills.size() / required.size();

        // Interest match
        long matchedInterests = tags.stream()
                .filter(t -> interestSet.contains(t.toLowerCase().trim()))
                .count();
        List<String> matchedInterestNames = tags.stream()
                .filter(t -> interestSet.contains(t.toLowerCase().trim()))
                .collect(Collectors.toList());
        double interestFit = tags.isEmpty() ? 0.0 : Math.min(1.0, (double) matchedInterests / tags.size());

        // Time fit
        double timeFit = computeTimeFit(availableHours, opp.getMinHoursPerWeek(), opp.getMaxHoursPerWeek());

        // Earning fit
        double avgEarning = averageEarning(opp);
        double earningFit = desiredIncome <= 0 ? 1.0 : Math.min(1.0, avgEarning / desiredIncome);

        int score = (int) Math.round(
                W_SKILL * skillFit + W_EARNING * earningFit + W_TIME * timeFit + W_INTEREST * interestFit);

        RecommendationResponse r = new RecommendationResponse();
        r.setId(opp.getId());
        r.setName(opp.getName());
        r.setDescription(opp.getDescription());
        r.setRequiredSkills(required);
        r.setInterestTags(tags);
        r.setMinMonthlyEarning(opp.getMinMonthlyEarning());
        r.setMaxMonthlyEarning(opp.getMaxMonthlyEarning());
        r.setMinHoursPerWeek(opp.getMinHoursPerWeek());
        r.setMaxHoursPerWeek(opp.getMaxHoursPerWeek());
        r.setDifficulty(opp.getDifficulty());
        r.setOnline(opp.getOnline());
        r.setPlatformName(opp.getPlatformName());
        r.setPlatformUrl(opp.getPlatformUrl());
        r.setMatchScore(Math.max(0, Math.min(100, score)));
        r.setMatchedSkills(matchedSkills);
        r.setBookmarked(bookmarkedIds.contains(opp.getId()));
        r.setWhyRecommended(buildReason(
                matchedSkills, matchedInterestNames, availableHours, timeFit, opp, desiredIncome));
        return r;
    }

    private double computeTimeFit(int availableHours, Integer min, Integer max) {
        if (availableHours <= 0) return 0.5;      // unknown availability -> neutral
        int lo = min != null ? min : 0;
        int hi = max != null ? max : Integer.MAX_VALUE;
        if (availableHours >= lo) return 1.0;      // enough (or more) time
        return lo > 0 ? (double) availableHours / lo : 1.0; // partial
    }

    private double averageEarning(SideHustleOpportunity opp) {
        double min = opp.getMinMonthlyEarning() != null ? opp.getMinMonthlyEarning() : 0.0;
        double max = opp.getMaxMonthlyEarning() != null ? opp.getMaxMonthlyEarning() : min;
        return (min + max) / 2.0;
    }

    private String buildReason(List<String> matchedSkills,
                               List<String> matchedInterests,
                               int availableHours,
                               double timeFit,
                               SideHustleOpportunity opp,
                               double desiredIncome) {
        List<String> parts = new ArrayList<>();
        if (!matchedSkills.isEmpty()) {
            parts.add("Matches your skills: " + String.join(", ", matchedSkills));
        }
        if (availableHours > 0 && timeFit >= 1.0 && opp.getMaxHoursPerWeek() != null) {
            parts.add("fits your ~" + availableHours + " hrs/week");
        } else if (availableHours > 0 && timeFit < 1.0) {
            parts.add("needs a bit more time than your ~" + availableHours + " hrs/week");
        }
        if (opp.getMinMonthlyEarning() != null && opp.getMaxMonthlyEarning() != null) {
            String earn = "can earn " + inr(opp.getMinMonthlyEarning()) + "–" + inr(opp.getMaxMonthlyEarning()) + "/month";
            if (desiredIncome > 0) {
                earn += " toward your " + inr(desiredIncome) + "/month goal";
            }
            parts.add(earn);
        }
        if (!matchedInterests.isEmpty()) {
            parts.add("aligned with your interest in " + String.join(", ", matchedInterests));
        }
        if (parts.isEmpty()) {
            return "A solid option to start earning on the side. Add your skills and interests for sharper matches.";
        }
        // Capitalize first letter.
        String joined = String.join(" · ", parts);
        return Character.toUpperCase(joined.charAt(0)) + joined.substring(1);
    }

    // ---------------- bookmarks ----------------

    public void addBookmark(String email, Long opportunityId) {
        User user = requireUser(email);
        SideHustleOpportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        if (!bookmarkRepository.existsByUserAndOpportunityId(user, opportunityId)) {
            bookmarkRepository.save(new SideHustleBookmark(user, opp));
        }
    }

    public void removeBookmark(String email, Long opportunityId) {
        User user = requireUser(email);
        bookmarkRepository.findByUserAndOpportunityId(user, opportunityId)
                .ifPresent(bookmarkRepository::delete);
    }

    // ---------------- helpers ----------------

    private double resolveDesiredIncome(FinancialPlan plan) {
        if (plan.getDesiredMonthlyIncome() != null && plan.getDesiredMonthlyIncome() > 0) {
            return plan.getDesiredMonthlyIncome();
        }
        // Fall back to the monthly savings target from the personalized plan.
        return plan.getMonthlyTarget() != null ? plan.getMonthlyTarget() : 0.0;
    }

    private SideHustleProfileResponse toProfileResponse(FinancialPlan plan) {
        return new SideHustleProfileResponse(
                parseCsv(plan.getSkills()),
                parseCsv(plan.getInterests()),
                plan.getAvailableHoursPerWeek(),
                resolveDesiredIncome(plan));
    }

    private List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return new ArrayList<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String joinCsv(List<String> values) {
        return values.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    private Set<String> lowerSet(List<String> values) {
        return values.stream().map(s -> s.toLowerCase().trim()).collect(Collectors.toSet());
    }

    private String inr(double amount) {
        return "₹" + String.format("%,.0f", amount);
    }

    private FinancialPlan requirePlan(String email) {
        User user = requireUser(email);
        return planRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Complete the questionnaire first"));
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
