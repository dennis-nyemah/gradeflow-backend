package com.gradeflow.repository;

import com.gradeflow.entity.Grade;
import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByTeacher(User teacher);
    List<Grade> findByStudent(Student student);
    Optional<Grade> findByStudentAndTeacher(Student student, User teacher);
    List<Grade> findByStudentIn(List<Student> students);
}