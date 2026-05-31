package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import com.gradeflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Student Service
 *
 * Handles sponsor-managed student operations including:
 * - Student registration
 * - Fetching sponsor students
 * - Student deletion with ownership validation
 *
 * This service acts as the student management layer
 * of the GradeFlow system.
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    // ─────────────────────────────────────────────
    // Student Registration
    // ─────────────────────────────────────────────

    /**
     * Registers one or more students for a sponsor.
     *
     * Supports:
     * - Single student registration
     * - Bulk student registration
     *
     * Logic:
     * - Converts DTOs into Student entities
     * - Automatically assigns sponsor ownership
     * - Uses sponsor grade level as class name when available
     * - Saves all students in a batch operation
     *
     * @param requests list containing one or more student requests
     * @param sponsor authenticated sponsor creating the students
     * @return saved student response DTOs
     */
    public List<Dto.StudentResponse> addStudentsBulk(List<Dto.StudentRequest> requests, User sponsor) {
        List<Student> students = requests.stream().map(r -> Student.builder()
                .fullName(r.getFullName())
                .studentId(r.getStudentId())

                // Use the sponsor's gradeLevel as className so teachers can filter by grade.
                // Falls back to request value if sponsor grade level is unavailable.
                .className(sponsor.getGradeLevel() != null && !sponsor.getGradeLevel().isBlank()
                        ? sponsor.getGradeLevel()
                        : r.getClassName())
                .academicYear(r.getAcademicYear())
                .sponsor(sponsor)
                .build()
        ).collect(Collectors.toList());
        return studentRepository.saveAll(students).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Sponsor Student Retrieval
    // ─────────────────────────────────────────────

    /**
     * Returns all students belonging to a sponsor.
     *
     * Students are sorted alphabetically by full name.
     *
     * @param sponsor sponsor(owner)
     * @return list of student response DTOs
     */
    public List<Dto.StudentResponse> getStudentsForSponsor(User sponsor) {
        return studentRepository.findBySponsorOrderByFullNameAsc(sponsor)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Student Deletion
    // ─────────────────────────────────────────────

    /**
     * Deletes a student record.
     *
     * Security validation:
     * - Student must exist
     * - Sponsor must own the student
     *
     * @param id student ID
     * @param sponsor authenticated sponsor
     */
    public void deleteStudent(Long id, User sponsor) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Ownership validation
        if (!student.getSponsor().getId().equals(sponsor.getId())) {
            throw new RuntimeException("Access denied");
        }
        studentRepository.delete(student);
    }

    // ─────────────────────────────────────────────
    // DTO Mapping Helper
    // ─────────────────────────────────────────────

    /**
     * Converts Student entity into response DTO.
     *
     * @param s student entity
     * @return student response DTO
     */
    public Dto.StudentResponse toResponse(Student s) {
        return Dto.StudentResponse.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .studentId(s.getStudentId())
                .className(s.getClassName())
                .academicYear(s.getAcademicYear())
                .build();
    }
}