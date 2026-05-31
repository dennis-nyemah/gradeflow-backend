package com.gradeflow.repository;

import com.gradeflow.entity.Grade;
import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Grade Repository
 *
 * Handles database access for Grade entities.
 *
 * This repository supports:
 * - Teacher grade submission tracking
 * - Sponsor grade sheet compilation
 * - Upsert logic (update existing grade or create new)
 * - Bulk grade retrieval for reporting
 *
 * This is a core repository in the GradeFlow grading engine.
 */
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * Fetch all grades submitted by a specific teacher.
     *
     * Used in:
     * - Teacher dashboard ("Submitted")
     */
    List<Grade> findByTeacher(User teacher);

    /**
     * Fetch all grades for a specific student.
     *
     * Used in:
     * - Sponsor grade sheet view
     * - Student academic record compilation
     */
    List<Grade> findByStudent(Student student);

    /**
     * Find a specific grade entry using composite lookup:
     * student + teacher + subject + period.
     *
     * This supports "upsert" behavior:
     * - If exists → update grade
     * - If not → create new grade
     */
    Optional<Grade> findByStudentAndTeacherAndSubjectAndPeriod(
            Student student, User teacher, String subject, String period);

    /**
     * Fetch all grades for a student under a specific teacher and subject.
     *
     * Used to:
     * - check if a teacher already graded a student for a subject (any period)
     */
    List<Grade> findByStudentAndTeacherAndSubject(Student student, User teacher, String subject);

    /**
     * Fetch all grades for a list of students.
     *
     * Used in:
     * - Sponsor dashboard
     * - Grade sheet status reporting
     *
     * This is useful for bulk analytics and reporting queries.
     */
    List<Grade> findByStudentIn(List<Student> students);

}