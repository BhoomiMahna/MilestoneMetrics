package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.RecommendationResponse;
import com.milestonemetrics.backend.dto.SideHustleProfileRequest;
import com.milestonemetrics.backend.dto.SideHustleProfileResponse;
import com.milestonemetrics.backend.service.SideHustleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sidehustle")
public class SideHustleController {

    private final SideHustleService sideHustleService;

    public SideHustleController(SideHustleService sideHustleService) {
        this.sideHustleService = sideHustleService;
    }

    /** The user's current side-hustle inputs (skills / interests / hours / desired income). */
    @GetMapping("/profile")
    public ResponseEntity<SideHustleProfileResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(sideHustleService.getProfile(principal.getName()));
    }

    /** Update skills / interests / available time / desired income. */
    @PutMapping("/profile")
    public ResponseEntity<SideHustleProfileResponse> updateProfile(
            @Valid @RequestBody SideHustleProfileRequest request, Principal principal) {
        return ResponseEntity.ok(sideHustleService.updateProfile(principal.getName(), request));
    }

    /** Ranked, personalized side-hustle recommendations for the user. */
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(Principal principal) {
        return ResponseEntity.ok(sideHustleService.getRecommendations(principal.getName()));
    }

    /** Bookmark an opportunity. */
    @PostMapping("/bookmarks/{opportunityId}")
    public ResponseEntity<Void> addBookmark(@PathVariable Long opportunityId, Principal principal) {
        sideHustleService.addBookmark(principal.getName(), opportunityId);
        return ResponseEntity.ok().build();
    }

    /** Remove a bookmark. */
    @DeleteMapping("/bookmarks/{opportunityId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long opportunityId, Principal principal) {
        sideHustleService.removeBookmark(principal.getName(), opportunityId);
        return ResponseEntity.noContent().build();
    }
}
