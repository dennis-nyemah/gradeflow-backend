package com.gradeflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GradeTemplate Entity
 *
 * Represents the grading structure configuration used by a sponsor/class.
 *
 * A GradeTemplate defines:
 * - School information (name, address, motto)
 * - Academic context (class, academic year)
 * - Subject structure
 * - Grading column structure (e.g., 1st, 2nd, Final Exam)
 * - Grading scale rules
 *
 * Only one template can be active at a time per sponsor context (business rule).
 *
 * This entity allows GradeFlow to support customizable grading systems
 * across different schools and academic setups.
 */
@Entity
@Table(name = "grade_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeTemplate {

    /**
     * Primary identifier for the template.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the school using this template.
     */
    @Column(nullable = false)
    private String schoolName;

    /**
     * Physical address of the school.
     */
    private String schoolAddress;

    /**
     * School motto or slogan.
     */
    private String schoolMotto;

    /**
     * Class or grade level this template applies to.
     * Example: "Grade 10A"
     */
    private String className;

    /**
     * Academic year for this grading template.
     * Example: "2025-2026"
     */
    private String academicYear;

    /**
     * List of subjects included in this template.
     *
     * Stored as a raw string.
     */
    @Column(columnDefinition = "TEXT")
    private String subjects;

    /**
     * Definition of grading columns.
     *
     * Example:
     * "Classwork, Quiz, HomeWork, Exam"
     */
    private String gradingColumns;

    /**
     * Grading scale rules.
     *
     * Example:
     * A=90-100 (Excellent), B=80-89 (Very Good), C=70-79 (Good), F=0-69 (Fail)
     */
    private String gradingScale;

    /**
     * Indicates whether this template is currently active.
     *
     * Business rule:
     * Only one template should be active per context.
     */
    private boolean active;

    /**
     * The IT/Admin user who created this template.
     *
     * Many templates can be created by one admin.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}