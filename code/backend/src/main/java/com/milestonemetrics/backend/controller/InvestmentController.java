package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.InvestmentRecommendationResponse;
import com.milestonemetrics.backend.service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    /** Educational, rule-based investment information tailored to the user's plan. */
    @GetMapping("/recommendations")
    public ResponseEntity<InvestmentRecommendationResponse> recommendations(Principal principal) {
        return ResponseEntity.ok(investmentService.getRecommendations(principal.getName()));
    }
}
