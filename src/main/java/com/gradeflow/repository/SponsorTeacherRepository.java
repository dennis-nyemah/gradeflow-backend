package com.gradeflow.repository;

import com.gradeflow.entity.SponsorTeacher;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * SponsorTeacher Repository
 *
 * Handles database operations for SponsorTeacher relationship entities.
 *
 * This repository manages the assignment mapping between:
 * - Sponsors (class/grade managers)
 * - Teachers (subject instructors)
 *
 * It supports:
 * - Fetching all teachers for a sponsor
 * - Fetching all sponsors for a teacher
 * - Preventing duplicate assignments
 * - Removing teacher assignments
 *
 * This is a core part of the GradeFlow role assignment system.
 */
public interface SponsorTeacherRepository extends JpaRepository<SponsorTeacher, Long> {

    /**
     * Get all teacher assigned for a specific sponsor.
     *
     * Used in:
     * - Sponsor dashboard (view assigned teachers)
     */
    List<SponsorTeacher> findBySponsor(User sponsor);

    /**
     * Get all sponsor assigned for a specific teacher.
     *
     * Used in:
     * - Teacher dashboard (view assigned classes)
     */
    List<SponsorTeacher> findByTeacher(User teacher);

    /**
     * Check if a teacher is already assigned to a sponsor.
     *
     * Used to prevent duplicate assignments.
     */
    boolean existsBySponsorAndTeacher(User sponsor, User teacher);

    /**
     * Remove a teacher assigned to a sponsor.
     *
     * Used when:
     * - IT admin unassigns a teacher
     */
    void deleteBySponsorAndTeacher(User sponsor, User teacher);
}