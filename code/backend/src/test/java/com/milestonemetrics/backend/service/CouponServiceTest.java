package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.RedeemResponse;
import com.milestonemetrics.backend.entity.Coupon;
import com.milestonemetrics.backend.entity.CouponRedemption;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.CouponRedemptionRepository;
import com.milestonemetrics.backend.repository.CouponRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests (no Spring/DB) for coupon unlock eligibility and redemption rules. */
class CouponServiceTest {

    private CouponRepository couponRepo;
    private CouponRedemptionRepository redemptionRepo;
    private UserRepository userRepo;
    private GamificationService gamification;
    private NotificationService notifications;
    private CouponService service;

    private final User user = new User("Jo", "jo@test.com", "x");

    @BeforeEach
    void setup() {
        couponRepo = mock(CouponRepository.class);
        redemptionRepo = mock(CouponRedemptionRepository.class);
        userRepo = mock(UserRepository.class);
        gamification = mock(GamificationService.class);
        notifications = mock(NotificationService.class);
        service = new CouponService(couponRepo, redemptionRepo, userRepo, gamification, notifications);
        when(userRepo.findByEmail("jo@test.com")).thenReturn(Optional.of(user));
    }

    private Coupon coupon(int pointsRequired) {
        Coupon c = new Coupon();
        c.setTitle("Demo");
        c.setDescription("d");
        c.setRewardLabel("₹100 off");
        c.setPointsRequired(pointsRequired);
        c.setActive(true);
        c.setDemoCode("MM-DEMO-1");
        return c;
    }

    @Test
    void redeemFailsWhenNotEnoughPoints() {
        when(couponRepo.findById(1L)).thenReturn(Optional.of(coupon(100)));
        when(redemptionRepo.existsByUserAndCouponId(user, 1L)).thenReturn(false);
        when(gamification.currentPoints(user)).thenReturn(50);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.redeem("jo@test.com", 1L));
        assertEquals(403, ex.getStatusCode().value());
        verify(redemptionRepo, never()).save(any());
    }

    @Test
    void redeemFailsWhenAlreadyRedeemed() {
        when(couponRepo.findById(1L)).thenReturn(Optional.of(coupon(100)));
        when(redemptionRepo.existsByUserAndCouponId(user, 1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.redeem("jo@test.com", 1L));
        assertEquals(409, ex.getStatusCode().value());
        verify(redemptionRepo, never()).save(any());
    }

    @Test
    void redeemSucceedsWithEnoughPoints() {
        Coupon c = coupon(100);
        when(couponRepo.findById(1L)).thenReturn(Optional.of(c));
        when(redemptionRepo.existsByUserAndCouponId(user, 1L)).thenReturn(false);
        when(gamification.currentPoints(user)).thenReturn(150);
        when(redemptionRepo.save(any(CouponRedemption.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RedeemResponse r = service.redeem("jo@test.com", 1L);
        assertEquals("MM-DEMO-1", r.getCodeIssued());
        verify(redemptionRepo, times(1)).save(any(CouponRedemption.class));
    }
}
