package com.gradeflow.repository;

import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySponsorOrderByFullNameAsc(User sponsor);
    // Teachers see all students (across any sponsor) for the active term
    List<Student> findByAcademicYearAndTermOrderByFullNameAsc(String academicYear, String term);
}