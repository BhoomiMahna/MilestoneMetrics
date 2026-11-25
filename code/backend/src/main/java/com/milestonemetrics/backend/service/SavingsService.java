package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.ProgressResponse;
import com.milestonemetrics.backend.dto.SavingsRecordResponse;
import com.milestonemetrics.backend.dto.SavingsRequest;
import com.milestonemetrics.backend.entity.FinancialPlan;
import com.milestonemetrics.backend.entity.NotificationType;
import com.milestonemetrics.backend.entity.PeriodType;
import com.milestonemetrics.backend.entity.SavingsRecord;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.SavingsRecordRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsService {

    private final SavingsRecordRepository savingsRepository;
    private final FinancialPlanRepository planRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;

    public SavingsService(SavingsRecordRepository savingsRepository,
                          FinancialPlanRepository planRepository,
                          UserRepository userRepository,
                          GamificationService gamificationService,
                          NotificationService notificationService) {
        this.savingsRepository = savingsRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
    }

    /**
     * Create or update the current user's actual-savings entry for a period.
     * The planned target for that period is snapshotted from the user's plan.
     */
    public SavingsRecordResponse saveSavings(String email, SavingsRequest request) {
        User user = requireUser(email);
        FinancialPlan plan = planRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Build your financial plan before logging savings"));

        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Period end cannot be before period start");
        }

        SavingsRecord record = savingsRepository
                .findByUserAndPeriodTypeAndPeriodStart(
                        user, request.getPeriodType(), request.getPeriodStart())
                .orElseGet(SavingsRecord::new);

        boolean isNew = record.getId() == null;
        record.setUser(user);
        record.setPeriodType(request.getPeriodType());
        record.setPeriodStart(request.getPeriodStart());
        record.setPeriodEnd(request.getPeriodEnd());
        record.setPeriodLabel(request.getPeriodLabel());
        record.setActualAmount(request.getActualAmount());
        record.setTargetAmount(targetFor(plan, request.getPeriodType()));
        if (!isNew) {
            record.setUpdatedAt(LocalDateTime.now());
        }

        savingsRepository.save(record);

        // Behind-target alert (in-app only), deduped per period.
        if (record.getTargetAmount() != null && record.getTargetAmount() > 0
                && record.getActualAmount() != null
                && record.getActualAmount() < record.getTargetAmount()) {
            double shortBy = record.getTargetAmount() - record.getActualAmount();
            notificationService.createIfAbsent(user, NotificationType.BEHIND_TARGET,
                    "Behind target for " + record.getPeriodLabel(),
                    String.format("You saved ₹%,.0f of ₹%,.0f — ₹%,.0f to go.",
                            record.getActualAmount(), record.getTargetAmount(), shortBy),
                    "BEHIND:" + record.getPeriodType() + ":" + record.getPeriodStart());
        }

        // Re-evaluate streaks / badges (idempotent; emits achievement notifications).
        gamificationService.evaluate(user);

        return new SavingsRecordResponse(record);
    }

    /**
     * Aggregated progress for one period view, comparing the user's actual
     * savings against their planned targets.
     */
    public ProgressResponse getProgress(String email, PeriodType periodType) {
        User user = requireUser(email);
        FinancialPlan plan = planRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No financial plan found. Complete the questionnaire first."));

        List<SavingsRecord> records = savingsRepository
                .findByUserAndPeriodTypeOrderByPeriodStartDesc(user, periodType);

        double targetPerPeriod = targetFor(plan, periodType);
        double totalActual = 0.0;
        double totalTarget = 0.0;
        List<SavingsRecordResponse> recordDtos = records.stream()
                .map(SavingsRecordResponse::new)
                .toList();
        for (SavingsRecord r : records) {
            totalActual += r.getActualAmount() != null ? r.getActualAmount() : 0.0;
            totalTarget += r.getTargetAmount() != null ? r.getTargetAmount() : 0.0;
        }

        double goal = plan.getGoalAmount() != null ? plan.getGoalAmount() : 0.0;
        double remainingToGoal = Math.max(goal - totalActual, 0.0);
        double goalPercent = goal > 0
                ? Math.min(Math.round((totalActual / goal) * 1000.0) / 10.0, 100.0)
                : 0.0;

        return new ProgressResponse(
                periodType,
                goal,
                targetPerPeriod,
                totalTarget,
                totalActual,
                remainingToGoal,
                goalPercent,
                recordDtos);
    }

    private double targetFor(FinancialPlan plan, PeriodType periodType) {
        Double target = switch (periodType) {
            case WEEK -> plan.getWeeklyTarget();
            case MONTH -> plan.getMonthlyTarget();
            case YEAR -> plan.getYearlyTarget();
        };
        return target != null ? target : 0.0;
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
