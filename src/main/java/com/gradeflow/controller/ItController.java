package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.User;
import com.gradeflow.service.ItService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.gradeflow.service.TemplateService;
import com.gradeflow.repository.UserRepository;

import java.util.List;

/**
 * IT Controller
 *
 * This controller exposes all IT (Admin-level) operations for GradeFlow.
 *
 * Responsibilities:
 * - Manage templates (create, activate, list)
 * - Manage sponsors and teachers
 * - Assign and unassign teachers to sponsors
 * - Retrieve relationships between sponsors and teachers
 *
 * Only authenticated users with IT role should access these endpoints.
 */
@RestController
@RequestMapping("/api/it")
@RequiredArgsConstructor
public class ItController {

    private final ItService itService;
    private final TemplateService templateService;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────────────────────
    // TEMPLATE MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new grading template.
     *
     * Requires IT authentication.
     */
    @PostMapping("/templates")
    public ResponseEntity<Dto.TemplateResponse> createTemplate(
            @Valid @RequestBody Dto.TemplateRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.createTemplate(request, resolveUser(principal)));
    }

    /**
     * Retrieves all templates accessible to IT.
     */
    @GetMapping("/templates")
    public ResponseEntity<List<Dto.TemplateResponse>> getAllTemplates(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.getAllTemplates(resolveUser(principal)));
    }

    /**
     * Activates a specific grading template.
     */
    @PutMapping("/templates/{id}/activate")
    public ResponseEntity<Dto.TemplateResponse> activateTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.activateTemplate(id, resolveUser(principal)));
    }

    // ─────────────────────────────────────────────────────────────
    // SPONSOR MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new sponsor account.
     */
    @PostMapping("/sponsors")
    public ResponseEntity<Dto.SponsorResponse> createSponsor(
            @Valid @RequestBody Dto.CreateSponsorRequest request) {
        return ResponseEntity.ok(itService.createSponsor(request));
    }

    /**
     * Retrieves all sponsors in the system.
     */
    @GetMapping("/sponsors")
    public ResponseEntity<List<Dto.SponsorResponse>> getAllSponsors() {
        return ResponseEntity.ok(itService.getAllSponsors());
    }

    // ─────────────────────────────────────────────────────────────
    // TEACHER MANAGEMENT
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new teacher account.
     */
    @PostMapping("/teachers")
    public ResponseEntity<Dto.TeacherResponse> createTeacher(
            @Valid @RequestBody Dto.CreateTeacherRequest request) {
        return ResponseEntity.ok(itService.createTeacher(request));
    }

    /**
     * Retrieves all teachers in the system.
     */
    @GetMapping("/teachers")
    public ResponseEntity<List<Dto.TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(itService.getAllTeachers());
    }

    // ─────────────────────────────────────────────────────────────
    // TEACHER ↔ SPONSOR ASSIGNMENTS
    // ─────────────────────────────────────────────────────────────

    /**
     * Assigns a teacher to a sponsor.
     */
    @PostMapping("/assignments")
    public ResponseEntity<Void> assignTeacher(
            @Valid @RequestBody Dto.AssignTeacherRequest request) {
        itService.assignTeacher(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Removes a teacher assignment from a sponsor.
     */
    @DeleteMapping("/assignments")
    public ResponseEntity<Void> unassignTeacher(
            @Valid @RequestBody Dto.AssignTeacherRequest request) {
        itService.unassignTeacher(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets all teachers assigned to a specific sponsor.
     */
    @GetMapping("/sponsors/{sponsorId}/teachers")
    public ResponseEntity<List<Dto.TeacherResponse>> getTeachersForSponsor(
            @PathVariable Long sponsorId) {
        return ResponseEntity.ok(itService.getTeachersForSponsor(sponsorId));
    }

    /**
     * Gets all sponsors assigned to a specific teacher.
     */
    @GetMapping("/teachers/{teacherId}/sponsors")
    public ResponseEntity<List<Dto.SponsorResponse>> getSponsorsForTeacher(
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(itService.getSponsorsForTeacher(teacherId));
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL HELPER
    // ─────────────────────────────────────────────────────────────

    /**
     * Resolves authenticated UserDetails into a full User entity.
     *
     * This is needed because services operate on domain entities,
     * not Spring Security's UserDetails abstraction.
     */
    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}