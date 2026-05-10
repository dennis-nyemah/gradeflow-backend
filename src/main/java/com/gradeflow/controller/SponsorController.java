package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import com.gradeflow.service.GradeService;
import com.gradeflow.service.StudentService;
import com.gradeflow.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sponsor")
@RequiredArgsConstructor
public class SponsorController {

    private final TemplateService templateService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final UserRepository userRepository;

    // ── Templates ─────────────────────────────────────────────────────────────

    @PostMapping("/templates")
    public ResponseEntity<Dto.TemplateResponse> createTemplate(
            @Valid @RequestBody Dto.TemplateRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.createTemplate(request, resolveUser(principal)));
    }

    @GetMapping("/templates/active")
    public ResponseEntity<Dto.TemplateResponse> getActiveTemplate(
            @AuthenticationPrincipal UserDetails principal) {
        Dto.TemplateResponse response = templateService.getActiveTemplate(resolveUser(principal));
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/templates")
    public ResponseEntity<List<Dto.TemplateResponse>> getAllTemplates(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.getAllTemplates(resolveUser(principal)));
    }

    @PutMapping("/templates/{id}/activate")
    public ResponseEntity<Dto.TemplateResponse> activateTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(templateService.activateTemplate(id, resolveUser(principal)));
    }

    // ── Students ──────────────────────────────────────────────────────────────

    @PostMapping("/students")
    public ResponseEntity<Dto.StudentResponse> addStudent(
            @Valid @RequestBody Dto.StudentRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(studentService.addStudent(request, resolveUser(principal)));
    }

    @PostMapping("/students/bulk")
    public ResponseEntity<List<Dto.StudentResponse>> addStudentsBulk(
            @RequestBody List<Dto.StudentRequest> requests,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(studentService.addStudentsBulk(requests, resolveUser(principal)));
    }

    @GetMapping("/students")
    public ResponseEntity<List<Dto.StudentResponse>> getStudents(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(studentService.getStudentsForSponsor(resolveUser(principal)));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        studentService.deleteStudent(id, resolveUser(principal));
        return ResponseEntity.noContent().build();
    }

    // ── Teachers ──────────────────────────────────────────────────────────────

    @GetMapping("/teachers")
    public ResponseEntity<List<Dto.TeacherResponse>> getTeachers() {
        List<Dto.TeacherResponse> teachers = userRepository.findByRole(User.Role.TEACHER)
                .stream().map(t -> Dto.TeacherResponse.builder()
                        .id(t.getId())
                        .fullName(t.getFullName())
                        .username(t.getUsername())
                        .subject(t.getSubject())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(teachers);
    }

    // ── Grade Sheets ──────────────────────────────────────────────────────────

    @GetMapping("/gradesheets")
    public ResponseEntity<List<Dto.GradeSheetStatusResponse>> getGradeSheetStatus(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getAllGradeSheetStatus(resolveUser(principal)));
    }

    @GetMapping("/gradesheets/{studentId}")
    public ResponseEntity<Dto.CompiledGradeSheetResponse> getCompiledGradeSheet(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getCompiledGradeSheet(studentId, resolveUser(principal)));
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}