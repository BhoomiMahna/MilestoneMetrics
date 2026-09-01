package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.QuestionnaireRequest;
import com.milestonemetrics.backend.dto.QuestionnaireResponse;
import com.milestonemetrics.backend.service.QuestionnaireService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(
            QuestionnaireService questionnaireService
    ) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping
    public ResponseEntity<QuestionnaireResponse> createQuestionnaire(
            @Valid @RequestBody QuestionnaireRequest request,
            Authentication authentication
    ) {

        QuestionnaireResponse response =
                questionnaireService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<QuestionnaireResponse> getMyQuestionnaire(
            Authentication authentication
    ) {

        QuestionnaireResponse response =
                questionnaireService.getMyQuestionnaire(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<QuestionnaireResponse> updateQuestionnaire(
            @Valid @RequestBody QuestionnaireRequest request,
            Authentication authentication
    ) {

        QuestionnaireResponse response =
                questionnaireService.update(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}