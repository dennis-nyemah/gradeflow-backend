package com.gradeflow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility Class
 *
 * Responsible for:
 * - Generating JWT tokens
 * - Parsing JWT tokens
 * - Validating token integrity and expiration
 *
 * This class is the core of the authentication system.
 *
 * It ensures stateless authentication by encoding user identity
 * inside signed tokens.
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Initializes JWT signing key and expiration time from application properties.
     *
     * @param secret JWT signing secret
     * @param expirationMs token validity duration in milliseconds
     */
    public JwtUtil(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_EXPIRATION_MS}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT token for a given username.
     *
     * Token includes:
     * - subject (username)
     * - issued time
     * - expiration time
     * - cryptographic signature
     *
     * @param username authenticated user's username
     * @return signed JWT token
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts username (subject) from JWT token.
     *
     * @param token JWT string
     * @return username stored in token
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates JWT token integrity and expiration.
     *
     * Returns:
     * - true → valid token
     * - false → invalid, expired, or tampered token
     *
     * @param token JWT string
     * @return boolean validity status
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parses JWT claims using signing key.
     *
     * This method:
     * - verifies signature
     * - checks expiration
     * - extracts payload data
     *
     * @param token JWT string
     * @return Claims object containing token data
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}