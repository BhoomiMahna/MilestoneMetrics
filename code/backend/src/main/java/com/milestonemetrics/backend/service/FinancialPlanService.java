package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.PlanRequest;
import com.milestonemetrics.backend.dto.PlanResponse;
import com.milestonemetrics.backend.entity.FinancialPlan;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FinancialPlanService {

    private final FinancialPlanRepository planRepository;
    private final UserRepository userRepository;

    public FinancialPlanService(FinancialPlanRepository planRepository,
                                UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create or update the current user's financial plan from their
     * questionnaire + goal input, computing the personalized per-period targets.
     */
    public PlanResponse savePlan(String email, PlanRequest request) {
        User user = requireUser(email);

        FinancialPlan plan = planRepository.findByUser(user).orElseGet(FinancialPlan::new);
        plan.setUser(user);
        plan.setMonthlyIncome(request.getMonthlyIncome());
        plan.setMonthlySpend(request.getMonthlySpend());
        plan.setCurrentInvestments(request.getCurrentInvestments());
        plan.setAgeBand(request.getAgeBand());
        plan.setGoalAmount(request.getGoalAmount());
        plan.setTimelineYears(request.getTimelineYears());

        double goal = request.getGoalAmount();
        double years = request.getTimelineYears();

        long weeks = Math.max(Math.round(years * 52.0), 1);
        long months = Math.max(Math.round(years * 12.0), 1);
        long wholeYears = Math.max(Math.round(years), 1);

        plan.setWeeklyTarget(Math.ceil(goal / weeks));
        plan.setMonthlyTarget(Math.ceil(goal / months));
        plan.setYearlyTarget(Math.ceil(goal / wholeYears));

        if (plan.getId() != null) {
            plan.setUpdatedAt(LocalDateTime.now());
        }
        planRepository.save(plan);
        return new PlanResponse(plan);
    }

    /**
     * The current user's plan, or empty if they have not built one yet.
     */
    public Optional<PlanResponse> getPlan(String email) {
        User user = requireUser(email);
        return planRepository.findByUser(user).map(PlanResponse::new);
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
