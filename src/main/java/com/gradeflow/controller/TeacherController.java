package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import com.gradeflow.service.GradeService;
import com.gradeflow.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Teacher Controller
 *
 * This controller handles all teacher-related operations in GradeFlow.
 *
 * Responsibilities:
 * - View active grading template
 * - View assigned students (from sponsor assignments)
 * - Submit and update grades
 * - View personal grading history
 *
 * All endpoints require authenticated users with TEACHER role.
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final GradeService gradeService;
    private final TemplateService templateService;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE VIEW
    // ─────────────────────────────────────────────────────────────

    /**
     * Retrieves the currently active grading template.
     *
     * This is used to get grading columns to send student grades.
     */
    @GetMapping("/template")
    public ResponseEntity<Dto.TemplateResponse> getTemplate() {
        GradeTemplate template = templateService.getLatestActiveTemplate();
        if (template == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(templateService.toResponse(template));
    }

    // ─────────────────────────────────────────────────────────────
    // STUDENTS
    // ─────────────────────────────────────────────────────────────

    /**
     * Retrieves students from all sponsors this teacher is assigned to.
     */
    @GetMapping("/students")
    public ResponseEntity<List<Dto.StudentResponse>> getStudents(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getStudentsForTeacher(resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // GRADING
    // ─────────────────────────────────────────────────────────────

    /**
     * Submits or updates a grade for a student.
     */
    @PostMapping("/grades")
    public ResponseEntity<Dto.GradeResponse> submitGrade(
            @Valid @RequestBody Dto.GradeRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.submitGrade(request, resolveUser(principal)));
    }

    /**
     * Retrieves all grades submitted by the authenticated teacher.
     */
    @GetMapping("/grades/my")
    public ResponseEntity<List<Dto.GradeResponse>> getMyGrades(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getGradesByTeacher(resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL UTILITY
    // ─────────────────────────────────────────────────────────────

    /**
     * Converts authenticated UserDetails into domain User entity.
     *
     * This allows service layer to operate on full database entities
     * instead of Spring Security abstractions.
     */
    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}