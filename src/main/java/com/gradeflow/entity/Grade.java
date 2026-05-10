package com.gradeflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "grades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "teacher_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // The subject this grade belongs to (copied from teacher.subject at submission time)
    @Column(nullable = false)
    private String subject;

    // Grading columns — all nullable since a teacher may only use a subset
    private Double firstTest;
    private Double secondTest;
    private Double midTerm;
    private Double homework;
    private Double exam;

    // Computed and stored at submission time
    private Double total;

    private String remarks;
}