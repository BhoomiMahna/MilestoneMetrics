package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.AdminAnalyticsResponse;
import com.milestonemetrics.backend.dto.AdminUserResponse;
import com.milestonemetrics.backend.dto.CouponAdminRequest;
import com.milestonemetrics.backend.dto.OpportunityAdminRequest;
import com.milestonemetrics.backend.entity.*;
import com.milestonemetrics.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Admin-only operations. Every method here is reachable only through
 * /api/admin/** which Spring Security restricts to ROLE_ADMIN, so a normal user
 * cannot invoke these even by crafting requests directly.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final FinancialPlanRepository planRepository;
    private final SavingsRecordRepository savingsRepository;
    private final UserAchievementRepository achievementRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final SideHustleOpportunityRepository opportunityRepository;

    public AdminService(UserRepository userRepository,
                        FinancialPlanRepository planRepository,
                        SavingsRecordRepository savingsRepository,
                        UserAchievementRepository achievementRepository,
                        CouponRepository couponRepository,
                        CouponRedemptionRepository redemptionRepository,
                        SideHustleOpportunityRepository opportunityRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.savingsRepository = savingsRepository;
        this.achievementRepository = achievementRepository;
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
        this.opportunityRepository = opportunityRepository;
    }

    // ---------------- users ----------------

    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll().stream().map(AdminUserResponse::new).toList();
    }

    public AdminUserResponse setUserRole(Long userId, String roleStr) {
        Role role;
        try {
            role = Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be USER or ADMIN");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getRole() == Role.ADMIN && role == Role.USER
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove the last administrator");
        }
        user.setRole(role);
        userRepository.save(user);
        return new AdminUserResponse(user);
    }

    // ---------------- analytics ----------------

    public AdminAnalyticsResponse analytics() {
        return new AdminAnalyticsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.ADMIN),
                planRepository.count(),
                savingsRepository.count(),
                achievementRepository.countByType(AchievementType.GOAL_REACHED),
                achievementRepository.count(),
                couponRepository.count(),
                couponRepository.countByActiveTrue(),
                redemptionRepository.count());
    }

    // ---------------- coupons ----------------

    public List<Coupon> listCoupons() {
        return couponRepository.findAllByOrderByPointsRequiredAsc();
    }

    public Coupon createCoupon(CouponAdminRequest req) {
        Coupon c = new Coupon();
        applyCoupon(c, req);
        c.setDemo(true);
        if (req.getActive() == null) c.setActive(true);
        return couponRepository.save(c);
    }

    public Coupon updateCoupon(Long id, CouponAdminRequest req) {
        Coupon c = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        applyCoupon(c, req);
        return couponRepository.save(c);
    }

    public Coupon setCouponActive(Long id, boolean active) {
        Coupon c = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        c.setActive(active);
        return couponRepository.save(c);
    }

    private void applyCoupon(Coupon c, CouponAdminRequest req) {
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setRewardLabel(req.getRewardLabel());
        c.setCategory(req.getCategory());
        c.setPointsRequired(req.getPointsRequired());
        c.setExpiryDate(req.getExpiryDate());
        if (req.getActive() != null) c.setActive(req.getActive());
        if (req.getDemoCode() != null) c.setDemoCode(req.getDemoCode());
    }

    // ---------------- side-hustle opportunities ----------------

    public List<SideHustleOpportunity> listOpportunities() {
        return opportunityRepository.findAll();
    }

    public SideHustleOpportunity createOpportunity(OpportunityAdminRequest req) {
        SideHustleOpportunity o = new SideHustleOpportunity();
        applyOpportunity(o, req);
        return opportunityRepository.save(o);
    }

    public SideHustleOpportunity updateOpportunity(Long id, OpportunityAdminRequest req) {
        SideHustleOpportunity o = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        applyOpportunity(o, req);
        return opportunityRepository.save(o);
    }

    public void deleteOpportunity(Long id) {
        if (!opportunityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }
        opportunityRepository.deleteById(id);
    }

    private void applyOpportunity(SideHustleOpportunity o, OpportunityAdminRequest req) {
        o.setName(req.getName());
        o.setDescription(req.getDescription());
        o.setRequiredSkills(req.getRequiredSkills());
        o.setInterestTags(req.getInterestTags());
        o.setMinMonthlyEarning(req.getMinMonthlyEarning());
        o.setMaxMonthlyEarning(req.getMaxMonthlyEarning());
        o.setMinHoursPerWeek(req.getMinHoursPerWeek());
        o.setMaxHoursPerWeek(req.getMaxHoursPerWeek());
        o.setDifficulty(req.getDifficulty());
        o.setOnline(req.getOnline());
        o.setPlatformName(req.getPlatformName());
        o.setPlatformUrl(req.getPlatformUrl());
    }
}
