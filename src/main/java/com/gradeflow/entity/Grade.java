package com.gradeflow.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Grade Entity
 *
 * Represents a student's grade record submitted by a teacher.
 *
 * Each Grade:
 * - Belongs to one Student
 * - Is created by one Teacher
 * - Represents a specific subject and period
 * - Stores dynamic scoring columns (based on grading template)
 *
 * This entity supports flexible grading systems where different schools
 * can define their own grading structure (e.g., quizzes, exams, assignments).
 */
@Entity
@Table(name = "grades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    /**
     * Primary identifier for the grade record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student this grade belongs to.
     *
     * Many grades can belong to one student.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * The teacher who submitted this grade.
     *
     * Many grades can be submitted by one teacher.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    /**
     * Subject for which this grade was recorded.
     * Example: Mathematics, English, Science
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Academic period for the grade.
     * Example: "1st Period", "2nd Period", "Exam"
     */
    @NotBlank()
    private String period;

    /**
     * Dynamic scoring system stored as key-value pairs.
     *
     * Example:
     * {
     *   "Class Participation": 18.0,
     *   "Quiz": 20.0,
     *   "Exam": 60.0
     * }
     *
     * Stored in a separate table (grade_scores) using JPA ElementCollection.
     */
    @ElementCollection
    @CollectionTable(
            name = "grade_scores",
            joinColumns = @JoinColumn(name = "grade_id")
    )
    @MapKeyColumn(name = "col_name")
    @Column(name = "score")
    private Map<String, Double> scores;

    /**
     * Final computed total score.
     *
     * This is calculated at submission time and stored for performance
     * (avoids recalculation during queries).
     */
    private Double total;

    /**
     * Optional teacher remarks about student performance.
     */
    private String remarks;
}