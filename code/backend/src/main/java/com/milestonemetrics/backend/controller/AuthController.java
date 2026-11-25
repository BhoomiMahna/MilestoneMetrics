package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.AuthResponse;
import com.milestonemetrics.backend.dto.LoginRequest;
import com.milestonemetrics.backend.dto.MessageResponse;
import com.milestonemetrics.backend.dto.SignupRequest;
import com.milestonemetrics.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
