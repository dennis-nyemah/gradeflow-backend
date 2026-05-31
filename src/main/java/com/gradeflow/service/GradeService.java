package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.Grade;
import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.repository.GradeRepository;
import com.gradeflow.repository.SponsorTeacherRepository;
import com.gradeflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Grade Service
 *
 * Core business logic service for handling:
 * - Grade submission (create/update)
 * - Teacher grade retrieval
 * - Sponsor grade sheet compilation
 * - Student-grade analytics and reporting
 *
 * This is the central grading engine of the GradeFlow system.
 */
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SponsorTeacherRepository sponsorTeacherRepository;
    private final TemplateService templateService;

    // ─────────────────────────────────────────────
    // Submit or update grade (UPSERT operation)
    // ─────────────────────────────────────────────

    /**
     * Submits a grade or updates an existing one.
     *
     * Logic:
     * - Find student
     * - Check if grade already exists (student + teacher + subject + period)
     * - If exists → update
     * - If not → create new
     * - Compute total score dynamically
     */
    @Transactional
    public Dto.GradeResponse submitGrade(Dto.GradeRequest request, User teacher) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String subject = request.getSubject();
        String period  = request.getPeriod();

        // Upsert logic (update or create)
        Grade grade = gradeRepository
                .findByStudentAndTeacherAndSubjectAndPeriod(student, teacher, subject, period)
                .orElseGet(() -> Grade.builder()
                        .student(student)
                        .teacher(teacher)
                        .subject(subject)
                        .period(period)
                        .build());

        // Set dynamic scores
        Map<String, Double> scores = parseScores(request.getScores());
        grade.setScores(scores);

        // Compute total
        grade.setTotal(computeTotal(scores));

        return toResponse(gradeRepository.save(grade));
    }

    // ─────────────────────────────────────────────
    // Teacher: fetch all submitted grades
    // ─────────────────────────────────────────────

    /**
     * Returns all grades submitted by a teacher.
     */
    public List<Dto.GradeResponse> getGradesByTeacher(User teacher) {
        return gradeRepository.findByTeacher(teacher)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Teacher: students under assigned sponsors
    // ─────────────────────────────────────────────

    /**
     * Returns students from all sponsors this teacher is assigned to.
     */
    public List<Dto.StudentResponse> getStudentsForTeacher(User teacher) {
        List<User> sponsors = sponsorTeacherRepository.findByTeacher(teacher)
                .stream()
                .map(st -> st.getSponsor())
                .collect(Collectors.toList());
        return studentRepository.findBySponsorInOrderByFullNameAsc(sponsors)
                .stream()
                .map(s -> Dto.StudentResponse.builder()
                        .id(s.getId())
                        .fullName(s.getFullName())
                        .studentId(s.getStudentId())
                        .className(s.getClassName())
                        .academicYear(s.getAcademicYear())
                        .build())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Sponsor: full grade sheet for a student
    // ─────────────────────────────────────────────

    /**
     * Builds a complete grade sheet for a student.
     *
     * Includes:
     * - Student info
     * - All grades
     * - Active grading template (for UI rendering)
     */
    public Dto.CompiledGradeSheetResponse getCompiledGradeSheet(Long studentId, User sponsor) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Security check: ensure sponsor owns student
        if (!student.getSponsor().getId().equals(sponsor.getId())) {
            throw new RuntimeException("Access denied");
        }

        List<Grade> grades = gradeRepository.findByStudent(student);

        List<Dto.GradeResponse> gradeResponses = grades.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        // Load active template for rendering grade sheet
        GradeTemplate activeTemplate = templateService.getLatestActiveTemplateForUser(student.getSponsor());
        if (activeTemplate == null) {
            activeTemplate = templateService.getLatestActiveTemplate();
        }

        return Dto.CompiledGradeSheetResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .className(student.getClassName())
                .academicYear(student.getAcademicYear())
                .template(activeTemplate != null ? templateService.toResponse(activeTemplate) : null)
                .grades(gradeResponses)
                .build();
    }

    // ─────────────────────────────────────────────
    // Sponsor: grade completion status
    // ─────────────────────────────────────────────

    /**
     * Returns summary of grading progress per student.
     */
    public List<Dto.GradeSheetStatusResponse> getAllGradeSheetStatus(User sponsor) {
        List<Student> students = studentRepository.findBySponsorOrderByFullNameAsc(sponsor);

        // Batch-fetch all grades for this sponsor's students in one query
        List<Grade> allGrades = gradeRepository.findByStudentIn(students);
        Map<Long, List<Grade>> gradesByStudent = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getStudent().getId()));

        return students.stream().map(s -> {
            List<Grade> grades = gradesByStudent.getOrDefault(s.getId(), List.of());
            List<String> submittedSubjects = grades.stream()
                    .map(Grade::getSubject)
                    .distinct()
                    .collect(Collectors.toList());
            return Dto.GradeSheetStatusResponse.builder()
                    .studentId(s.getId())
                    .studentName(s.getFullName())
                    .className(s.getClassName())
                    .submittedSubjects(submittedSubjects)
                    .totalGrades(grades.size())
                    .build();
        }).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Map<String, Double> parseScores(Map<String, Object> raw) {
        if (raw == null) return Map.of();
        return raw.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().toString().isBlank())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Double.parseDouble(e.getValue().toString())
                ));
    }

    private Double computeTotal(Map<String, Double> scores) {
        if (scores == null || scores.isEmpty()) return null;
        return scores.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private Dto.GradeResponse toResponse(Grade g) {
        return Dto.GradeResponse.builder()
                .id(g.getId())
                .studentId(g.getStudent().getId())
                .studentName(g.getStudent().getFullName())
                .subject(g.getSubject())
                .period(g.getPeriod())
                .scores(g.getScores() != null
                        ? g.getScores().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (Object) e.getValue()))
                        : Map.of())
                .total(g.getTotal())
                .build();
    }
}