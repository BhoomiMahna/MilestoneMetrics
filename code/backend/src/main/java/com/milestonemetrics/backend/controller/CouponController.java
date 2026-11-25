package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.CouponListResponse;
import com.milestonemetrics.backend.dto.CouponResponse;
import com.milestonemetrics.backend.dto.RedeemResponse;
import com.milestonemetrics.backend.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /** Catalog with per-user status (LOCKED / AVAILABLE / REDEEMED / EXPIRED) + points. */
    @GetMapping
    public ResponseEntity<CouponListResponse> list(Principal principal) {
        return ResponseEntity.ok(couponService.listForUser(principal.getName()));
    }

    @PostMapping("/{id}/redeem")
    public ResponseEntity<RedeemResponse> redeem(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(couponService.redeem(principal.getName(), id));
    }

    @GetMapping("/redemptions")
    public ResponseEntity<List<CouponResponse>> history(Principal principal) {
        return ResponseEntity.ok(couponService.redemptionHistory(principal.getName()));
    }
}
