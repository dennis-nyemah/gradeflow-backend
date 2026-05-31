package com.gradeflow.dto;

import com.gradeflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Central Data Transfer Object (DTO) container for GradeFlow.
 *
 * This class defines all request and response models used across:
 * - Authentication (Auth)
 * - IT administration
 * - Sponsor operations
 * - Teacher operations
 * - Templates
 * - Students
 * - Grades
 *
 * DTOs ensure:
 * - Separation between API layer and database entities
 * - Stable API contracts for frontend (React/Vercel)
 * - Security (no direct entity exposure)
 */
public class Dto {

    // ─────────────────────────────────────────
    // AUTHENTICATION
    // ─────────────────────────────────────────

    /**
     * Login request payload.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    /**
     * Registration request.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank private String fullName;
        @NotBlank private String username;
        @NotBlank private String password;
        @NotNull  private User.Role role;

        private String subject;    // Required for TEACHER
        private String gradeLevel; // Required for SPONSOR
    }

    /**
     * Authentication response containing JWT token and user info.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private Long id;
        private String fullName;
        private String username;
        private String role;
        private String subject;
        private String gradeLevel;
    }

    // ─────────────────────────────────────────
    // IT ADMIN CREATION
    // ─────────────────────────────────────────

    /**
     * Request to create a sponsor.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateSponsorRequest {
        @NotBlank private String fullName;
        @NotBlank private String username;
        @NotBlank private String password;
        @NotBlank private String gradeLevel; // "Grade 1" … "Grade 12"
    }

    /**
     * Request to create a teacher.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateTeacherRequest {
        @NotBlank private String fullName;
        @NotBlank private String username;
        @NotBlank private String password;
        @NotBlank private String subject;
    }

    /**
     * Sponsor creation response object.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SponsorResponse {
        private Long id;
        private String fullName;
        private String username;
        private String gradeLevel;
    }

    /**
     * Teacher creation response object.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TeacherResponse {
        private Long id;
        private String fullName;
        private String username;
        private String subject;
    }

    /**
     * Assign or unassign teacher to sponsor.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class AssignTeacherRequest {
        @NotNull private Long sponsorId;
        @NotNull private Long teacherId;
    }

    /**
     * Simplified teacher view shown to sponsors.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SponsorTeacherView {
        private String fullName;
        private String subject;
    }

    // ─────────────────────────────────────────
    // TEMPLATE SYSTEM
    // ─────────────────────────────────────────

    /**
     * Request to create a grading template.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TemplateRequest {
        @NotBlank private String schoolName;
        private String schoolAddress;
        private String schoolMotto;
        @NotBlank private String academicYear;
        private String className;
        @NotBlank private String subjects;
        @NotBlank private String gradingColumns;
        private String gradingScale;
    }

    /**
     * Template creation response returned to frontend.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TemplateResponse {
        private Long id;
        private String schoolName;
        private String schoolAddress;
        private String schoolMotto;
        private String academicYear;
        private String className;
        private String subjects;
        private String gradingColumns;
        private String gradingScale;
        private boolean active;
    }

    // ─────────────────────────────────────────
    // STUDENTS
    // ─────────────────────────────────────────

    /**
     * Student creation request.
     */
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StudentRequest {
        @NotBlank private String fullName;
        private String studentId;
        private String className;
        private String academicYear;
    }

    /**
     * Student creation response.
     */
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudentResponse {
        private Long id;
        private String fullName;
        private String studentId;
        private String className;
        private String academicYear;
    }

    // ─────────────────────────────────────────
    // GRADES
    // ─────────────────────────────────────────

    /**
     * Grade submission request.
     *
     * Uses dynamic scoring system based on template columns.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeRequest {
        @NotNull private Long studentId;
        @NotBlank private String subject;
        private String period;

        /**
         * Dynamic grading scores:
         * Example:
         * {
         *   "Class Part": 18,
         *   "Quiz": 20,
         *   "Exam": 60,
         *   "Test": 40
         * }
         */
        private Map<String, Object> scores;
    }

    /**
     * Grade response returned after submission.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeResponse {
        private Long   id;
        private Long   studentId;
        private String studentName;
        private String subject;
        private String period;
        private Map<String, Object> scores; // column → score value
        private Double total;
    }

    /**
     * Grade sheet response status per student.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeSheetStatusResponse {
        private Long         studentId;
        private String       studentName;
        private String       className;
        private List<String> submittedSubjects;
        private int          totalGrades;
    }

    /**
     * Fully compiled grade sheet for a student.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompiledGradeSheetResponse {
        private Long   studentId;
        private String studentName;
        private String className;
        private String academicYear;
        private TemplateResponse template;
        private List<GradeResponse> grades;
    }

    // ─────────────────────────────────────────
    // ERROR HANDLING
    // ─────────────────────────────────────────

    /**
     * Standard error response returned by API.
     */
    @Data @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}