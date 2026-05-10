package com.gradeflow.repository;

import com.gradeflow.entity.GradeTemplate;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeTemplateRepository extends JpaRepository<GradeTemplate, Long> {
    Optional<GradeTemplate> findFirstByCreatedByAndActiveTrueOrderByIdDesc(User createdBy);
    List<GradeTemplate> findByCreatedByOrderByIdDesc(User createdBy);
    Optional<GradeTemplate> findFirstByActiveTrueOrderByIdDesc();
}