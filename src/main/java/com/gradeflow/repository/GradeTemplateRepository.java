package com.gradeflow.repository;

import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * GradeTemplate Repository
 *
 * Handles database operations for GradeTemplate entities.
 *
 * This repository supports:
 * - Fetching active grading templates
 * - Managing IT-created templates
 * - Supporting "latest active template" logic
 *
 * Grade templates define the grading structure for a class/school,
 * making this repository critical for dynamic grading behavior.
 */
public interface GradeTemplateRepository extends JpaRepository<GradeTemplate, Long> {

    /**
     * Fetch the latest ACTIVE template created by a specific user.
     *
     * Used when:
     * - Sponsor views their current grading structure
     * - Ensuring only active templates are used
     *
     * Order by ID DESC ensures newest template is returned.
     */
    Optional<GradeTemplate> findFirstByCreatedByAndActiveTrueOrderByIdDesc(User createdBy);

    /**
     * Fetch all templates created by a specific user.
     */
    List<GradeTemplate> findByCreatedByOrderByIdDesc(User createdBy);

    /**
     * Fetch the latest globally ACTIVE template.
     *
     * Used in:
     * - Teacher dashboard header
     * - Default grading context when no sponsor-specific template is set
     *
     * This acts as a fallback or system-wide active configuration.
     */
    Optional<GradeTemplate> findFirstByActiveTrueOrderByIdDesc();
}