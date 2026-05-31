package com.gradeflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SponsorTeacher Entity
 *
 * This is a JOIN TABLE entity that maps the relationship between:
 * - Sponsor (class/grade manager)
 * - Teacher (subject instructor)
 *
 * Business Meaning:
 * A sponsor (e.g. Grade 5A class teacher) can have multiple subject teachers.
 * A teacher can also be assigned to multiple sponsors/classes.
 *
 * This represents a many-to-many relationship with additional control.
 *
 * The table enforces uniqueness so that a teacher cannot be assigned
 * to the same sponsor more than once.
 */
@Entity
@Table(
        name = "sponsor_teachers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sponsor_id", "teacher_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorTeacher {

    /**
     * Primary identifier for the assignment record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sponsor (class/grade owner).
     *
     * This is the person responsible for students in a class/grade.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private User sponsor;

    /**
     * Teacher assigned to the sponsor.
     *
     * Responsible for grading or teaching a subject within that sponsor's class.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
}