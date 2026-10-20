package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.CouponListResponse;
import com.milestonemetrics.backend.dto.CouponResponse;
import com.milestonemetrics.backend.dto.RedeemResponse;
import com.milestonemetrics.backend.entity.*;
import com.milestonemetrics.backend.repository.CouponRedemptionRepository;
import com.milestonemetrics.backend.repository.CouponRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;

    public CouponService(CouponRepository couponRepository,
                         CouponRedemptionRepository redemptionRepository,
                         UserRepository userRepository,
                         GamificationService gamificationService,
                         NotificationService notificationService) {
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
    }

    public CouponListResponse listForUser(String email) {
        User user = requireUser(email);
        gamificationService.evaluate(user);           // make sure points are current
        int points = gamificationService.currentPoints(user);
        LocalDate today = LocalDate.now();

        List<CouponResponse> out = new ArrayList<>();
        for (Coupon c : couponRepository.findByActiveTrueOrderByPointsRequiredAsc()) {
            Optional<CouponRedemption> red = redemptionRepository.findByUserAndCouponId(user, c.getId());
            String status;
            String code = null;
            var redeemedAt = red.map(CouponRedemption::getRedeemedAt).orElse(null);

            if (red.isPresent()) {
                status = "REDEEMED";
                code = red.get().getCodeIssued();
            } else if (c.getExpiryDate() != null && c.getExpiryDate().isBefore(today)) {
                status = "EXPIRED";
            } else if (points >= c.getPointsRequired()) {
                status = "AVAILABLE";
                // notify the first time an offer becomes unlockable (deduped)
                notificationService.createIfAbsent(user, NotificationType.COUPON_UNLOCKED,
                        "Offer unlocked: " + c.getTitle() + " 🎁",
                        "You have enough points to redeem \"" + c.getRewardLabel() + "\".",
                        "COUPON_UNLOCKED:" + c.getId());
            } else {
                status = "LOCKED";
            }
            out.add(new CouponResponse(c, status, code, redeemedAt));
        }
        return new CouponListResponse(points, out);
    }

    public RedeemResponse redeem(String email, Long couponId) {
        User user = requireUser(email);
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found"));

        if (!coupon.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This offer is no longer available");
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This offer has expired");
        }
        if (redemptionRepository.existsByUserAndCouponId(user, couponId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already redeemed this offer");
        }
        gamificationService.evaluate(user);
        int points = gamificationService.currentPoints(user);
        if (points < coupon.getPointsRequired()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You need " + coupon.getPointsRequired() + " points to redeem this (you have " + points + ")");
        }

        String code = (coupon.getDemoCode() != null && !coupon.getDemoCode().isBlank())
                ? coupon.getDemoCode()
                : "MM-DEMO-" + couponId + "-" + Long.toString(System.nanoTime(), 36).toUpperCase();

        CouponRedemption redemption;
        try {
            redemption = redemptionRepository.save(new CouponRedemption(user, coupon, code));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already redeemed this offer");
        }

        notificationService.createIfAbsent(user, NotificationType.COUPON_REDEEMED,
                "Offer redeemed: " + coupon.getTitle(),
                "Your demo code is " + code + ". (Demonstration only — not valid with any real merchant.)",
                "COUPON_REDEEMED:" + couponId);

        return new RedeemResponse(coupon.getId(), coupon.getTitle(), code,
                redemption.getRedeemedAt(),
                "Redeemed! This is a demonstration code and is not redeemable with a real merchant.");
    }

    public List<CouponResponse> redemptionHistory(String email) {
        User user = requireUser(email);
        List<CouponResponse> out = new ArrayList<>();
        for (CouponRedemption r : redemptionRepository.findByUserOrderByRedeemedAtDesc(user)) {
            out.add(new CouponResponse(r.getCoupon(), "REDEEMED", r.getCodeIssued(), r.getRedeemedAt()));
        }
        return out;
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
