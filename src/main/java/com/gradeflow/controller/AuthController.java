package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
import com.gradeflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 *
 * Handles user authentication-related endpoints such as login.
 *
 * This controller is the entry point for authentication requests
 * from the frontend.
 *
 * Base URL: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * Flow:
     * 1. Receives login request (username + password)
     * 2. Delegates authentication to AuthService
     * 3. Returns JWT token + user info in response
     *
     * @param request contains login credentials (validated)
     * @return AuthResponse containing JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<Dto.AuthResponse> login(@Valid @RequestBody Dto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}