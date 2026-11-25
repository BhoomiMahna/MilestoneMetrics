package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.PlanRequest;
import com.milestonemetrics.backend.dto.PlanResponse;
import com.milestonemetrics.backend.service.FinancialPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/plan")
public class FinancialPlanController {

    private final FinancialPlanService planService;

    public FinancialPlanController(FinancialPlanService planService) {
        this.planService = planService;
    }

    /** Generate / update the authenticated user's personalized financial plan. */
    @PostMapping
    public ResponseEntity<PlanResponse> savePlan(@Valid @RequestBody PlanRequest request,
                                                 Principal principal) {
        return ResponseEntity.ok(planService.savePlan(principal.getName(), request));
    }

    /** The authenticated user's plan, or 204 if they have not built one yet. */
    @GetMapping
    public ResponseEntity<PlanResponse> getPlan(Principal principal) {
        return planService.getPlan(principal.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
