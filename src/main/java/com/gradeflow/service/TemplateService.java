package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.entity.User;
import com.gradeflow.repository.GradeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final GradeTemplateRepository templateRepository;

    public Dto.TemplateResponse createTemplate(Dto.TemplateRequest request, User sponsor) {
        // Deactivate all previous templates for this sponsor
        List<GradeTemplate> existing = templateRepository.findByCreatedByOrderByIdDesc(sponsor);
        existing.forEach(t -> t.setActive(false));
        templateRepository.saveAll(existing);

        GradeTemplate template = GradeTemplate.builder()
                .schoolName(request.getSchoolName())
                .schoolAddress(request.getSchoolAddress())
                .schoolMotto(request.getSchoolMotto())
                .academicYear(request.getAcademicYear())
                .term(request.getTerm())
                .className(request.getClassName())
                .subjects(request.getSubjects())
                .gradingColumns(request.getGradingColumns())
                .gradingScale(request.getGradingScale())
                .active(true)
                .createdBy(sponsor)
                .build();

        return toResponse(templateRepository.save(template));
    }

    public Dto.TemplateResponse getActiveTemplate(User sponsor) {
        return templateRepository
                .findFirstByCreatedByAndActiveTrueOrderByIdDesc(sponsor)
                .map(this::toResponse)
                .orElse(null);
    }

    /** Used by teachers — returns the globally latest active template */
    public GradeTemplate getLatestActiveTemplate() {
        return templateRepository.findFirstByActiveTrueOrderByIdDesc().orElse(null);
    }

    public List<Dto.TemplateResponse> getAllTemplates(User sponsor) {
        return templateRepository.findByCreatedByOrderByIdDesc(sponsor)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Dto.TemplateResponse activateTemplate(Long id, User sponsor) {
        GradeTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        if (!template.getCreatedBy().getId().equals(sponsor.getId())) {
            throw new RuntimeException("Access denied");
        }
        // Deactivate others
        templateRepository.findByCreatedByOrderByIdDesc(sponsor)
                .forEach(t -> { t.setActive(false); templateRepository.save(t); });
        template.setActive(true);
        return toResponse(templateRepository.save(template));
    }

    public Dto.TemplateResponse toResponse(GradeTemplate t) {
        return Dto.TemplateResponse.builder()
                .id(t.getId())
                .schoolName(t.getSchoolName())
                .schoolAddress(t.getSchoolAddress())
                .schoolMotto(t.getSchoolMotto())
                .academicYear(t.getAcademicYear())
                .term(t.getTerm())
                .className(t.getClassName())
                .subjects(t.getSubjects())
                .gradingColumns(t.getGradingColumns())
                .gradingScale(t.getGradingScale())
                .active(t.isActive())
                .build();
    }
}