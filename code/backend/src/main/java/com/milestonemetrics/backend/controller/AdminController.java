package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.*;
import com.milestonemetrics.backend.entity.Coupon;
import com.milestonemetrics.backend.entity.SideHustleOpportunity;
import com.milestonemetrics.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only API. Protected by Spring Security: /api/admin/** requires ROLE_ADMIN
 * (see SecurityConfig), so a normal user's JWT cannot reach any of these.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ----- users -----
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> users() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<AdminUserResponse> setRole(@PathVariable Long id,
                                                     @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(adminService.setUserRole(id, request.getRole()));
    }

    // ----- analytics -----
    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsResponse> analytics() {
        return ResponseEntity.ok(adminService.analytics());
    }

    // ----- coupons -----
    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> coupons() {
        return ResponseEntity.ok(adminService.listCoupons());
    }

    @PostMapping("/coupons")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponAdminRequest request) {
        return ResponseEntity.ok(adminService.createCoupon(request));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id,
                                               @Valid @RequestBody CouponAdminRequest request) {
        return ResponseEntity.ok(adminService.updateCoupon(id, request));
    }

    @PutMapping("/coupons/{id}/active")
    public ResponseEntity<Coupon> setCouponActive(@PathVariable Long id,
                                                  @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.setCouponActive(id, active));
    }

    // ----- side-hustle opportunities -----
    @GetMapping("/opportunities")
    public ResponseEntity<List<SideHustleOpportunity>> opportunities() {
        return ResponseEntity.ok(adminService.listOpportunities());
    }

    @PostMapping("/opportunities")
    public ResponseEntity<SideHustleOpportunity> createOpportunity(
            @Valid @RequestBody OpportunityAdminRequest request) {
        return ResponseEntity.ok(adminService.createOpportunity(request));
    }

    @PutMapping("/opportunities/{id}")
    public ResponseEntity<SideHustleOpportunity> updateOpportunity(
            @PathVariable Long id, @Valid @RequestBody OpportunityAdminRequest request) {
        return ResponseEntity.ok(adminService.updateOpportunity(id, request));
    }

    @DeleteMapping("/opportunities/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        adminService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
}
