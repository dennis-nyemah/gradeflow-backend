package com.gradeflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity
 *
 * Represents all system users in GradeFlow.
 *
 * This is the central identity model for authentication and authorization.
 *
 * Supported roles:
 * - IT: System administrator (manages sponsors, teachers, templates)
 * - SPONSOR: Class/grade manager (handles students and grade sheets)
 * - TEACHER: Subject teacher (submits grades)
 *
 * This single-user model simplifies authentication and supports
 * role-based access control (RBAC).
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Unique username used for login authentication.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Encrypted password (stored using BCrypt).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Role to determine what actions the user can perform in the system.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Subject taught by TEACHER users only.
     *
     * Example: Mathematics, English, Science
     */
    private String subject;

    /**
     * Grade level managed by SPONSOR users only.
     *
     * Example: "Grade 2", "Grade 10A"
     */
    private String gradeLevel;

    /**
     * System roles supported in GradeFlow.
     */
    public enum Role {
        IT, SPONSOR, TEACHER
    }
}