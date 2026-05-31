package com.gradeflow.repository;

import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 *
 * Handles database operations for User entities.
 *
 * This repository is the foundation of authentication and authorization
 * in GradeFlow.
 *
 * It supports:
 * - Login authentication (find by username)
 * - User registration validation (check duplicates)
 * - Role-based user retrieval (IT, SPONSOR, TEACHER)
 *
 * This is a core security and identity repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     *
     * Used during:
     * - Login authentication
     * - JWT token validation
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     *
     * Used during:
     * - User registration
     * - Preventing duplicate accounts
     */
    boolean existsByUsername(String username);

    /**
     * Retrieve all users by role.
     *
     * Used in:
     * - IT dashboard (view teachers, sponsors)
     * - Sponsor dashboard (view assigned teachers)
     */
    List<User> findByRole(User.Role role);
}