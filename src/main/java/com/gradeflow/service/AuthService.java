package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import com.gradeflow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 *
 * Handles user login and JWT token generation.
 *
 * Responsibilities:
 * - Authenticate user credentials using Spring Security
 * - Retrieve user from database
 * - Generate JWT token upon successful login
 * - Build authentication response DTO for frontend
 *
 * This service is the entry point for the authentication flow.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Handles user login process.
     *
     * Flow:
     * 1. Validate username and password via AuthenticationManager
     * 2. Retrieve user from database
     * 3. Generate JWT token
     * 4. Return user info + token to frontend
     *
     * @param request login credentials
     * @return AuthResponse containing JWT + user details
     */
    public Dto.AuthResponse login(Dto.LoginRequest request) {

        // Step 1: Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Step 2: Fetch user from database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 3: Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Step 4: Build response DTO
        return buildAuthResponse(user, token);
    }

    /**
     * Builds authentication response object for frontend.
     *
     * Contains:
     * - JWT token
     * - User identity
     * - Role information
     * - Optional profile fields (subject, grade level)
     *
     * @param user authenticated user
     * @param token generated JWT token
     * @return structured AuthResponse DTO
     */
    private Dto.AuthResponse buildAuthResponse(User user, String token) {
        return Dto.AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole().name())
                .subject(user.getSubject())
                .gradeLevel(user.getGradeLevel())
                .build();
    }
}