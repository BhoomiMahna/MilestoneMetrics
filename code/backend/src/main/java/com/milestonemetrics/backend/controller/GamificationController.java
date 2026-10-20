package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.GamificationSummaryResponse;
import com.milestonemetrics.backend.service.GamificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    private final GamificationService gamificationService;

    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    /** Points, weekly streaks, badges and next-achievement progress for the user. */
    @GetMapping("/summary")
    public ResponseEntity<GamificationSummaryResponse> summary(Principal principal) {
        return ResponseEntity.ok(gamificationService.getSummary(principal.getName()));
    }
}
