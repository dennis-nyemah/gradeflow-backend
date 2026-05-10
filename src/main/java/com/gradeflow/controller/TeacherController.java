package com.gradeflow.controller;

import com.gradeflow.dto.Dto;
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

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final GradeService gradeService;
    private final TemplateService templateService;
    private final UserRepository userRepository;

    /** Active template info shown in the teacher portal header */
    @GetMapping("/template")
    public ResponseEntity<Dto.TemplateResponse> getTemplate() {
        Dto.TemplateResponse response = templateService.toResponse(templateService.getLatestActiveTemplate());
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    /** All students for the active term */
    @GetMapping("/students")
    public ResponseEntity<List<Dto.StudentResponse>> getStudents() {
        return ResponseEntity.ok(gradeService.getStudentsForTeacher());
    }

    /** Submit or update a grade */
    @PostMapping("/grades")
    public ResponseEntity<Dto.GradeResponse> submitGrade(
            @Valid @RequestBody Dto.GradeRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.submitGrade(request, resolveUser(principal)));
    }

    /** All grades this teacher has submitted */
    @GetMapping("/grades/my")
    public ResponseEntity<List<Dto.GradeResponse>> getMyGrades(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(gradeService.getMyGrades(resolveUser(principal)));
    }

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}