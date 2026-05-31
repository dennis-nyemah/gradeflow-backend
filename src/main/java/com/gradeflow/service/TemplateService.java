package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.entity.User;
import com.gradeflow.repository.GradeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Template Service
 *
 * Handles grade sheet template management including:
 * - Template creation
 * - Template activation
 * - Template retrieval
 * - Sponsor-specific active templates
 *
 * Templates define:
 * - School information
 * - Subjects
 * - Grading columns
 * - Grading scales
 * - Academic year configuration
 *
 * Business Rule:
 * Each sponsor can only have ONE active template at a time.
 */
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final GradeTemplateRepository templateRepository;

    // ─────────────────────────────────────────────
    // Template Creation
    // ─────────────────────────────────────────────

    /**
     * Creates a new grade template.
     *
     * Business logic:
     * - Deactivates all previous templates for the user
     * - Creates a new template
     * - Automatically marks the new template as active
     *
     * This guarantees:
     * ONE active template per sponsor at any time.
     *
     * @param request template creation request
     * @param createdBy authenticated sponsor/creator
     * @return created template response DTO
     */
    public Dto.TemplateResponse createTemplate(Dto.TemplateRequest request, User createdBy) {

        // Deactivate all previous templates for this user
        List<GradeTemplate> existing = templateRepository.findByCreatedByOrderByIdDesc(createdBy);
        existing.forEach(t -> t.setActive(false));
        templateRepository.saveAll(existing);

        // Build new active template
        GradeTemplate template = GradeTemplate.builder()
                .schoolName(request.getSchoolName())
                .schoolAddress(request.getSchoolAddress())
                .schoolMotto(request.getSchoolMotto())
                .className(request.getClassName())
                .academicYear(request.getAcademicYear())
                .subjects(request.getSubjects())
                .gradingColumns(request.getGradingColumns())
                .gradingScale(request.getGradingScale())
                .active(true)
                .createdBy(createdBy)
                .build();

        return toResponse(templateRepository.save(template));
    }

    // ─────────────────────────────────────────────
    // Active Template Retrieval
    // ─────────────────────────────────────────────

    /**
     * Returns the active template for a specific user.
     *
     * Used primarily by sponsors.
     *
     * @param createdBy template owner
     * @return active template response DTO or null
     */
    public Dto.TemplateResponse getActiveTemplate(User createdBy) {
        return templateRepository
                .findFirstByCreatedByAndActiveTrueOrderByIdDesc(createdBy)
                .map(this::toResponse)
                .orElse(null);
    }

    /**
     * Returns the latest globally active template.
     *
     * Primarily used by teachers when they need
     * the currently active grading structure.
     *
     * @return latest active template entity or null
     */
    public GradeTemplate getLatestActiveTemplate() {
        return templateRepository.findFirstByActiveTrueOrderByIdDesc().orElse(null);
    }

    /**
     * Returns the latest active template for a specific sponsor.
     *
     * Used by GradeService during compiled grade sheet generation.
     *
     * @param createdBy sponsor(owner)
     * @return active template entity or null
     */
    public GradeTemplate getLatestActiveTemplateForUser(User createdBy) {
        return templateRepository
                .findFirstByCreatedByAndActiveTrueOrderByIdDesc(createdBy)
                .orElse(null);
    }

    // ─────────────────────────────────────────────
    // Template Retrieval
    // ─────────────────────────────────────────────

    /**
     * Returns all templates created by a user.
     *
     * Templates are ordered newest-first.
     *
     * @param createdBy template owner
     * @return list of template response DTOs
     */
    public List<Dto.TemplateResponse> getAllTemplates(User createdBy) {
        return templateRepository.findByCreatedByOrderByIdDesc(createdBy)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Template Activation
    // ─────────────────────────────────────────────

    /**
     * Activates a selected template.
     *
     * Business logic:
     * - User must own the template
     * - All existing templates are first deactivated
     * - Selected template becomes active
     *
     * Ensures:
     * Only one active template per sponsor.
     *
     * @param id template ID
     * @param createdBy authenticated owner
     * @return activated template response DTO
     */
    public Dto.TemplateResponse activateTemplate(Long id, User createdBy) {
        GradeTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Ownership validation
        if (!template.getCreatedBy().getId().equals(createdBy.getId())) {
            throw new RuntimeException("Access denied");
        }
        // Deactivate all others first
        templateRepository.findByCreatedByOrderByIdDesc(createdBy)
                .forEach(t -> { t.setActive(false); templateRepository.save(t); });

        // Activate selected template
        template.setActive(true);
        return toResponse(templateRepository.save(template));
    }

    // ─────────────────────────────────────────────
    // DTO Mapping Helper
    // ─────────────────────────────────────────────

    /**
     * Converts GradeTemplate entity into response DTO.
     *
     * @param t template entity
     * @return template response DTO
     */
    public Dto.TemplateResponse toResponse(GradeTemplate t) {
        return Dto.TemplateResponse.builder()
                .id(t.getId())
                .schoolName(t.getSchoolName())
                .schoolAddress(t.getSchoolAddress())
                .schoolMotto(t.getSchoolMotto())
                .className(t.getClassName())
                .academicYear(t.getAcademicYear())
                .subjects(t.getSubjects())
                .gradingColumns(t.getGradingColumns())
                .gradingScale(t.getGradingScale())
                .active(t.isActive())
                .build();
    }
}