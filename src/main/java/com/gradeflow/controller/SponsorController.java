package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import com.gradeflow.service.GradeService;
import com.gradeflow.service.ItService;
import com.gradeflow.service.StudentService;
import com.gradeflow.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sponsor Controller
 *
 * This controller handles all sponsor-level operations in GradeFlow.
 *
 * Responsibilities:
 * - Manage grading templates (view, activate)
 * - Manage students (bulk creation, retrieval, deletion)
 * - View assigned teachers (from IT assignments)
 * - View grade sheets and student performance data
 *
 * All endpoints require authenticated users with SPONSOR role.
 */
@RestController
@RequestMapping("/api/sponsor")
@RequiredArgsConstructor
public class SponsorController {

    private final TemplateService templateService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final ItService itService;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Gets the currently active grading template for this sponsor.
     *
     * Returns 204 No Content if no active template exists.
     */
    @GetMapping("/templates/active")
    public ResponseEntity<Dto.TemplateResponse> getActiveTemplate(
            @AuthenticationPrincipal UserDetails principal) {
        Dto.TemplateResponse response = templateService.getActiveTemplate(resolveUser(principal));
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all templates available to the sponsor.
     */
    @GetMapping("/templates")
    public ResponseEntity<List<Dto.TemplateResponse>> getAllTemplates(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.getAllTemplates(resolveUser(principal)));
    }

    /**
     * Activates a specific grading template for sponsor use.
     */
    @PutMapping("/templates/{id}/activate")
    public ResponseEntity<Dto.TemplateResponse> activateTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.activateTemplate(id, resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // STUDENT MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Adds a student or multiple students in bulk for a sponsor.
     */
    @PostMapping("/students/bulk")
    public ResponseEntity<List<Dto.StudentResponse>> addStudentsBulk(
            @RequestBody List<Dto.StudentRequest> requests,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(studentService.addStudentsBulk(requests, resolveUser(principal)));
    }

    /**
     * Retrieves all students belonging to the authenticated sponsor.
     */
    @GetMapping("/students")
    public ResponseEntity<List<Dto.StudentResponse>> getStudents(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(studentService.getStudentsForSponsor(resolveUser(principal)));
    }

    /**
     * Deletes a student from sponsor's list.
     */
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        studentService.deleteStudent(id, resolveUser(principal));
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────
    // TEACHER VIEW (READ-ONLY)
    // ─────────────────────────────────────────────────────────────

    /**
     * Retrieves simplified teacher view assigned to this sponsor.
     *
     * This is populated via IT assignments and includes:
     * - Teacher name
     * - Subject
     */
    @GetMapping("/teachers")
    public ResponseEntity<List<Dto.SponsorTeacherView>> getMyTeachers(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(itService.getTeacherViewForSponsor(resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // GRADE SHEETS
    // ─────────────────────────────────────────────────────────────

    /**
     * Retrieves status overview of all grade sheets for sponsor's students.
     *
     * Example:
     * - No grades yet
     * - 1 subject
     * - 6 subjects
     */
    @GetMapping("/gradesheets")
    public ResponseEntity<List<Dto.GradeSheetStatusResponse>> getGradeSheetStatus(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getAllGradeSheetStatus(resolveUser(principal)));
    }

    /**
     * Retrieves a fully compiled grade sheet for a specific student.
     */
    @GetMapping("/gradesheets/{studentId}")
    public ResponseEntity<Dto.CompiledGradeSheetResponse> getCompiledGradeSheet(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getCompiledGradeSheet(studentId, resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL UTILITY
    // ─────────────────────────────────────────────────────────────

    /**
     * Converts Spring Security UserDetails into a domain User entity.
     *
     * This allows services to work with real database entities
     * instead of security abstraction objects.
     */
    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}