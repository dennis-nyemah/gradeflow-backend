package com.gradeflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Student Entity
 *
 * Represents a student enrolled under a sponsor (class/grade manager).
 *
 * Each student:
 * - Belongs to exactly one Sponsor
 * - Can have multiple grades (from different teachers/subjects)
 *
 * This entity is a core part of the academic data model in GradeFlow.
 */
@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    /**
     * Primary identifier for the student record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the student.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Optional external student ID (school registration number).
     * Useful for integration with real school systems.
     */
    private String studentId;

    /**
     * Class or grade level the student belongs to.
     * Example: "Grade 5A", "Grade 10B"
     */
    private String className;

    /**
     * Academic year the student is enrolled in.
     * Example: "2025-2026"
     */
    private String academicYear;

    /**
     * Sponsor responsible for this student.
     *
     * Many students belong to one sponsor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private User sponsor;
}