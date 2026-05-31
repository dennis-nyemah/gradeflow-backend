package com.gradeflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 *
 * This filter intercepts every incoming HTTP request and checks for a JWT token.
 *
 * Responsibilities:
 * - Extract JWT from Authorization header
 * - Validate token integrity and expiration
 * - Load user details from database
 * - Set authentication in Spring Security context
 *
 * This is the core security layer of the application.
 *
 * If token is invalid → request is rejected immediately (401)
 * If token is valid → user is authenticated and request continues
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Core filter logic executed for every request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT token from Authorization header
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {

            // Token was provided — validate it
            if (!jwtUtil.isValid(token)) {

                // Invalid or expired token → stop request immediately
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"Token expired or invalid. Please log in again.\"}");
                return;
            }

            // Extract username from token
            String username = jwtUtil.extractUsername(token);

            // Load full user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create authentication object
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Attach request details (IP, session info, etc.)
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Store authentication in Spring Security context
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Continue request chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Authorization header.
     *
     * Expected format:
     * Authorization: Bearer <token>
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}