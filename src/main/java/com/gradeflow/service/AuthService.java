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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Dto.AuthResponse login(Dto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());
        return buildAuthResponse(user, token);
    }

    public Dto.AuthResponse register(Dto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (request.getRole() == User.Role.TEACHER && (request.getSubject() == null || request.getSubject().isBlank())) {
            throw new IllegalArgumentException("Subject is required for teacher accounts");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .subject(request.getSubject())
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return buildAuthResponse(user, token);
    }

    private Dto.AuthResponse buildAuthResponse(User user, String token) {
        return Dto.AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole().name())
                .subject(user.getSubject())
                .build();
    }
}