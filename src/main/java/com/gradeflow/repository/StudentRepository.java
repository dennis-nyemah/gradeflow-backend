package com.gradeflow.repository;

import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Student Repository
 *
 * Handles database operations for Student entities.
 *
 * This repository supports:
 * - Sponsor-specific student retrieval
 * - Teacher-wide student access (via assigned sponsors)
 * - Ordered student listings for UI consistency
 *
 * It is a key part of the academic data layer in GradeFlow.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Get all students belonging to a specific sponsor (class/grade).
     *
     * Used in:
     * - Sponsor dashboard
     * - Student List
     *
     * Results are ordered alphabetically for UI display consistency.
     */
    List<Student> findBySponsorOrderByFullNameAsc(User sponsor);

    /**
     * Get all students belonging to multiple sponsors.
     *
     * Used in:
     * - Teacher dashboard
     * - Grade entry screens (teachers see all assigned classes)
     *
     * This enables teachers to access students across multiple classes.
     */
    List<Student> findBySponsorInOrderByFullNameAsc(List<User> sponsors);
}