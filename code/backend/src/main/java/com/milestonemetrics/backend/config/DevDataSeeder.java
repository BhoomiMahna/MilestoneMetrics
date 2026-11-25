package com.milestonemetrics.backend.config;

import com.milestonemetrics.backend.dto.BillReminderRequest;
import com.milestonemetrics.backend.dto.PlanRequest;
import com.milestonemetrics.backend.dto.SavingsRequest;
import com.milestonemetrics.backend.entity.Coupon;
import com.milestonemetrics.backend.entity.PeriodType;
import com.milestonemetrics.backend.entity.Role;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.CouponRepository;
import com.milestonemetrics.backend.repository.FinancialPlanRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import com.milestonemetrics.backend.service.CouponService;
import com.milestonemetrics.backend.service.FinancialPlanService;
import com.milestonemetrics.backend.service.NotificationService;
import com.milestonemetrics.backend.service.SavingsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DEV / PREVIEW ONLY — active only under the "dev" Spring profile (see
 * application-dev.properties). It populates the in-memory H2 database with
 * clearly-labelled DEMO data so every new feature page can be inspected
 * visually without a live database and without manual data entry.
 *
 * <p>Nothing here runs in the default (production) profile, so it can never
 * touch the real Supabase data. All accounts and figures below are obviously
 * fake sample data ("Demo", "@milestonemetrics.local"), never real user data.
 * Authentication is NOT weakened: these demo users have real BCrypt-hashed
 * passwords and log in through the normal JWT flow.
 */
@Configuration
@Profile("dev")
public class DevDataSeeder {

    /** Demo credentials (printed to the console on startup). */
    static final String DEMO_EMAIL = "demo@milestonemetrics.local";
    static final String DEMO_PASSWORD = "Demo@12345";
    static final String ADMIN_EMAIL = "admin@milestonemetrics.local";
    static final String ADMIN_PASSWORD = "Admin@12345";

    private static final DateTimeFormatter LABEL_FMT = DateTimeFormatter.ofPattern("d MMM");

    @Bean
    @Order(10) // after the coupon + side-hustle catalog seeders (@Order(1))
    CommandLineRunner seedDevData(UserRepository userRepository,
                                  FinancialPlanRepository planRepository,
                                  PasswordEncoder passwordEncoder,
                                  FinancialPlanService financialPlanService,
                                  SavingsService savingsService,
                                  NotificationService notificationService,
                                  CouponService couponService,
                                  CouponRepository couponRepository) {
        return args -> {
            if (userRepository.findByEmail(DEMO_EMAIL).isPresent()) {
                return; // already seeded for this process
            }

            // --- Demo ADMIN account (role set directly; no env bootstrap needed) ---
            User admin = new User("Demo Admin", ADMIN_EMAIL, passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            // Give the admin a minimal plan so login lands on the dashboard (where the
            // Admin nav link lives) rather than the questionnaire, which has no nav bar.
            PlanRequest adminPlan = new PlanRequest();
            adminPlan.setMonthlyIncome(60000.0);
            adminPlan.setMonthlySpend(35000.0);
            adminPlan.setCurrentInvestments(50000.0);
            adminPlan.setAgeBand("26-35");
            adminPlan.setGoalAmount(120000.0);
            adminPlan.setTimelineYears(1.0);
            financialPlanService.savePlan(ADMIN_EMAIL, adminPlan);

            // A couple of extra plain users so the admin "Users" table is not empty.
            userRepository.save(new User("Sample User A", "sample.a@milestonemetrics.local",
                    passwordEncoder.encode("Sample@123")));
            userRepository.save(new User("Sample User B", "sample.b@milestonemetrics.local",
                    passwordEncoder.encode("Sample@123")));

            // --- Demo NORMAL account with a rich history ---
            User demo = new User("Demo User", DEMO_EMAIL, passwordEncoder.encode(DEMO_PASSWORD));
            userRepository.save(demo);

            // Plan: goal ₹1,04,000 over 1 year -> weekly target ₹2,000.
            PlanRequest plan = new PlanRequest();
            plan.setMonthlyIncome(40000.0);
            plan.setMonthlySpend(25000.0);
            plan.setCurrentInvestments(15000.0);
            plan.setAgeBand("18-25");
            plan.setGoalAmount(104000.0);
            plan.setTimelineYears(1.0);
            financialPlanService.savePlan(DEMO_EMAIL, plan);

            // Add side-hustle inputs so recommendations look populated.
            planRepository.findByUser(demo).ifPresent(p -> {
                p.setSkills("Python, Web Development, Writing");
                p.setInterests("Teaching, Coding");
                p.setAvailableHoursPerWeek(10);
                p.setDesiredMonthlyIncome(10000.0);
                planRepository.save(p);
            });

            // Weekly savings history: 1 behind-target week (with a gap), then 6
            // consecutive qualifying weeks -> 6-week streak, badges + points, and
            // relevant notifications (all generated through the real savings flow).
            LocalDate thisWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            logWeek(savingsService, thisWeekStart.minusWeeks(9), 900.0);   // behind target
            for (int w = 5; w >= 0; w--) {
                double actual = (w == 0) ? 2500.0 : 2100.0;                 // all >= 2000 target
                logWeek(savingsService, thisWeekStart.minusWeeks(w), actual);
            }

            // A user-created bill reminder (upcoming).
            BillReminderRequest bill = new BillReminderRequest();
            bill.setTitle("Phone recharge");
            bill.setNote("Monthly prepaid plan");
            bill.setAmount(299.0);
            bill.setDueDate(LocalDate.now().plusDays(5));
            notificationService.createBillReminder(DEMO_EMAIL, bill);

            // Redeem the cheapest affordable (non-free) coupon so the Coupons page
            // shows a REDEEMED item alongside AVAILABLE and LOCKED ones.
            couponRepository.findAll().stream()
                    .filter(Coupon::isActive)
                    .filter(c -> c.getExpiryDate() == null || !c.getExpiryDate().isBefore(LocalDate.now()))
                    .filter(c -> c.getPointsRequired() > 0)
                    .sorted((a, b) -> Integer.compare(a.getPointsRequired(), b.getPointsRequired()))
                    .findFirst()
                    .ifPresent(c -> {
                        try {
                            couponService.redeem(DEMO_EMAIL, c.getId());
                        } catch (RuntimeException ignored) {
                            // not enough points — leave it for preview as AVAILABLE/LOCKED
                        }
                    });

            // A dev-only, clearly-labelled EXPIRED demo coupon so the EXPIRED state
            // is visible on the Coupons page.
            Coupon expired = new Coupon();
            expired.setTitle("[DEMO] Expired festival voucher");
            expired.setDescription("Sample expired offer — shown only to demonstrate the EXPIRED state.");
            expired.setRewardLabel("₹150 off");
            expired.setCategory("Shopping");
            expired.setPointsRequired(50);
            expired.setExpiryDate(LocalDate.now().minusDays(3));
            expired.setActive(true);
            expired.setDemo(true);
            expired.setDemoCode("MM-DEMO-EXPIRED");
            couponRepository.save(expired);

            System.out.println("""
                    =====================================================================
                    [DevDataSeeder] Preview demo data loaded into in-memory H2 (dev only).
                      Normal user : %s  /  %s
                      Admin user  : %s  /  %s
                    These are DEMO accounts with sample data — not real users.
                    =====================================================================
                    """.formatted(DEMO_EMAIL, DEMO_PASSWORD, ADMIN_EMAIL, ADMIN_PASSWORD));
        };
    }

    private static void logWeek(SavingsService savingsService, LocalDate start, double actual) {
        LocalDate end = start.plusDays(6);
        SavingsRequest r = new SavingsRequest();
        r.setPeriodType(PeriodType.WEEK);
        r.setPeriodStart(start);
        r.setPeriodEnd(end);
        r.setPeriodLabel(start.format(LABEL_FMT) + " – " + end.format(LABEL_FMT));
        r.setActualAmount(actual);
        savingsService.saveSavings(DEMO_EMAIL, r);
    }
}
