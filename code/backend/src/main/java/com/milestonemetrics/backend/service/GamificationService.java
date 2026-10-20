package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.GamificationSummaryResponse;
import com.milestonemetrics.backend.dto.GamificationSummaryResponse.AchievementView;
import com.milestonemetrics.backend.dto.GamificationSummaryResponse.NextAchievement;
import com.milestonemetrics.backend.entity.*;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.SavingsRecordRepository;
import com.milestonemetrics.backend.repository.UserAchievementRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

/**
 * Gamification engine. Streaks and achievements are derived purely from the
 * user's real {@link SavingsRecord}s — never fabricated. Achievements are
 * awarded idempotently (unique (user, type) constraint), so re-evaluating after
 * an edit never double-awards points or badges.
 *
 * Qualifying period: a weekly SavingsRecord whose actualAmount >= its snapshot
 * targetAmount (target > 0). A streak is a run of consecutive weeks (period
 * starts exactly 7 days apart) that each qualify; any gap or a non-qualifying
 * week breaks it.
 */
@Service
public class GamificationService {

    private final UserRepository userRepository;
    private final SavingsRecordRepository savingsRepository;
    private final FinancialPlanRepository planRepository;
    private final UserAchievementRepository achievementRepository;
    private final NotificationService notificationService;

    public GamificationService(UserRepository userRepository,
                               SavingsRecordRepository savingsRepository,
                               FinancialPlanRepository planRepository,
                               UserAchievementRepository achievementRepository,
                               NotificationService notificationService) {
        this.userRepository = userRepository;
        this.savingsRepository = savingsRepository;
        this.planRepository = planRepository;
        this.achievementRepository = achievementRepository;
        this.notificationService = notificationService;
    }

    private static boolean qualifies(SavingsRecord r) {
        return r.getActualAmount() != null && r.getTargetAmount() != null
                && r.getTargetAmount() > 0 && r.getActualAmount() >= r.getTargetAmount();
    }

    // ---------------- streaks ----------------

    /** [currentStreak, longestStreak, qualifyingWeeks] over weekly records. */
    int[] weeklyStreaks(User user) {
        List<SavingsRecord> weekly =
                savingsRepository.findByUserAndPeriodTypeOrderByPeriodStartDesc(user, PeriodType.WEEK);
        List<LocalDate> starts = weekly.stream()
                .filter(GamificationService::qualifies)
                .map(SavingsRecord::getPeriodStart)
                .sorted()
                .toList();
        return streaksFromStarts(starts, 7);
    }

    /**
     * Pure streak math (unit-testable). Given the ascending, distinct start dates
     * of qualifying periods and the step between consecutive periods (7 for
     * weeks), returns [currentStreak, longestStreak, qualifyingCount]. A gap
     * (two starts not exactly {@code stepDays} apart) breaks the streak.
     */
    static int[] streaksFromStarts(List<LocalDate> starts, int stepDays) {
        int qualifying = starts.size();
        if (qualifying == 0) return new int[]{0, 0, 0};

        int longest = 1, run = 1;
        for (int i = 1; i < starts.size(); i++) {
            if (starts.get(i).equals(starts.get(i - 1).plusDays(stepDays))) {
                run++;
            } else {
                run = 1;
            }
            longest = Math.max(longest, run);
        }
        // current streak = consecutive run ending at the latest qualifying period
        int current = 1;
        for (int i = starts.size() - 1; i > 0; i--) {
            if (starts.get(i).equals(starts.get(i - 1).plusDays(stepDays))) {
                current++;
            } else {
                break;
            }
        }
        return new int[]{current, longest, qualifying};
    }

    /** Max, over each period type, of the summed actual savings for that type. */
    private double totalSavedMax(User user) {
        Map<PeriodType, Double> byType = new EnumMap<>(PeriodType.class);
        for (SavingsRecord r : savingsRepository.findByUserOrderByPeriodStartDesc(user)) {
            double a = r.getActualAmount() != null ? r.getActualAmount() : 0.0;
            byType.merge(r.getPeriodType(), a, Double::sum);
        }
        return byType.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    // ---------------- evaluation (award badges + notify) ----------------

    /**
     * Award any achievements the user newly qualifies for. Safe to call on every
     * savings save and on summary fetch. Returns the list of newly awarded types.
     */
    public List<AchievementType> evaluate(User user) {
        List<SavingsRecord> all = savingsRepository.findByUserOrderByPeriodStartDesc(user);
        FinancialPlan plan = planRepository.findByUser(user).orElse(null);

        int[] s = weeklyStreaks(user);
        int longest = s[1];
        boolean anyRecord = !all.isEmpty();
        boolean anyTargetMet = all.stream().anyMatch(GamificationService::qualifies);
        double goal = (plan != null && plan.getGoalAmount() != null) ? plan.getGoalAmount() : 0.0;
        boolean goalReached = goal > 0 && totalSavedMax(user) >= goal;

        List<AchievementType> newly = new ArrayList<>();
        if (anyRecord) award(user, AchievementType.FIRST_SAVINGS, newly);
        if (anyTargetMet) award(user, AchievementType.FIRST_TARGET_MET, newly);
        if (longest >= 4) award(user, AchievementType.WEEK_STREAK_4, newly);
        if (longest >= 8) award(user, AchievementType.WEEK_STREAK_8, newly);
        if (goalReached) award(user, AchievementType.GOAL_REACHED, newly);
        return newly;
    }

    private void award(User user, AchievementType type, List<AchievementType> newly) {
        if (achievementRepository.existsByUserAndType(user, type)) return;
        try {
            achievementRepository.save(new UserAchievement(user, type));
        } catch (DataIntegrityViolationException e) {
            return; // lost a race on the unique constraint — already awarded
        }
        newly.add(type);

        if (type == AchievementType.GOAL_REACHED) {
            notificationService.createIfAbsent(user, NotificationType.GOAL_COMPLETED,
                    "Goal reached! 🎯",
                    "You've saved your full goal amount. +" + type.getPoints() + " points.",
                    "ACH:" + type.name());
        } else {
            notificationService.createIfAbsent(user, NotificationType.ACHIEVEMENT,
                    "Badge unlocked: " + type.getTitle() + " 🏆",
                    type.getDescription() + ". +" + type.getPoints() + " points.",
                    "ACH:" + type.name());
        }
    }

    // ---------------- summary ----------------

    public GamificationSummaryResponse getSummary(String email) {
        User user = requireUser(email);
        evaluate(user); // self-heal: award anything already earned

        List<UserAchievement> earned = achievementRepository.findByUser(user);
        Map<AchievementType, UserAchievement> earnedByType = new EnumMap<>(AchievementType.class);
        int totalPoints = 0;
        for (UserAchievement ua : earned) {
            earnedByType.put(ua.getType(), ua);
            totalPoints += ua.getPointsAwarded();
        }

        int[] s = weeklyStreaks(user);
        int current = s[0], longest = s[1], qualifying = s[2];

        List<AchievementView> views = new ArrayList<>();
        for (AchievementType t : AchievementType.values()) {
            UserAchievement ua = earnedByType.get(t);
            views.add(new AchievementView(t, ua != null, ua != null ? ua.getAwardedAt() : null));
        }

        NextAchievement next = computeNext(user, earnedByType.keySet(), longest);

        return new GamificationSummaryResponse(totalPoints, current, longest, qualifying, views, next);
    }

    private NextAchievement computeNext(User user, Set<AchievementType> have, int longestWeekly) {
        if (!have.contains(AchievementType.WEEK_STREAK_4)) {
            return new NextAchievement(AchievementType.WEEK_STREAK_4, longestWeekly, 4);
        }
        if (!have.contains(AchievementType.WEEK_STREAK_8)) {
            return new NextAchievement(AchievementType.WEEK_STREAK_8, longestWeekly, 8);
        }
        if (!have.contains(AchievementType.GOAL_REACHED)) {
            FinancialPlan plan = planRepository.findByUser(user).orElse(null);
            double goal = (plan != null && plan.getGoalAmount() != null) ? plan.getGoalAmount() : 0.0;
            if (goal > 0) {
                return new NextAchievement(AchievementType.GOAL_REACHED, totalSavedMax(user), goal);
            }
        }
        return null;
    }

    /** Total points the user currently holds (sum of awarded achievements). */
    public int currentPoints(User user) {
        return achievementRepository.findByUser(user).stream()
                .mapToInt(UserAchievement::getPointsAwarded).sum();
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
