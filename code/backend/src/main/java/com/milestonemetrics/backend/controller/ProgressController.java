package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.ProgressResponse;
import com.milestonemetrics.backend.dto.SavingsRecordResponse;
import com.milestonemetrics.backend.dto.SavingsRequest;
import com.milestonemetrics.backend.entity.PeriodType;
import com.milestonemetrics.backend.service.SavingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final SavingsService savingsService;

    public ProgressController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    /** Dashboard progress for one period view (WEEK / MONTH / YEAR). */
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(
            @RequestParam(name = "period", defaultValue = "WEEK") PeriodType period,
            Principal principal) {
        return ResponseEntity.ok(savingsService.getProgress(principal.getName(), period));
    }

    /** Create or update the user's actual savings for a period. */
    @PostMapping("/savings")
    public ResponseEntity<SavingsRecordResponse> saveSavings(
            @Valid @RequestBody SavingsRequest request,
            Principal principal) {
        return ResponseEntity.ok(savingsService.saveSavings(principal.getName(), request));
    }
}
