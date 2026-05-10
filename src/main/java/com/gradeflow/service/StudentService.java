package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.Student;
import com.gradeflow.entity.User;
import com.gradeflow.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Dto.StudentResponse addStudent(Dto.StudentRequest request, User sponsor) {
        Student student = Student.builder()
                .fullName(request.getFullName())
                .studentId(request.getStudentId())
                .className(request.getClassName())
                .academicYear(request.getAcademicYear())
                .term(request.getTerm())
                .sponsor(sponsor)
                .build();
        return toResponse(studentRepository.save(student));
    }

    public List<Dto.StudentResponse> addStudentsBulk(List<Dto.StudentRequest> requests, User sponsor) {
        List<Student> students = requests.stream().map(r -> Student.builder()
                .fullName(r.getFullName())
                .studentId(r.getStudentId())
                .className(r.getClassName() != null ? r.getClassName() : "")
                .academicYear(r.getAcademicYear() != null ? r.getAcademicYear() : "")
                .term(r.getTerm() != null ? r.getTerm() : "")
                .sponsor(sponsor)
                .build()
        ).collect(Collectors.toList());
        return studentRepository.saveAll(students).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<Dto.StudentResponse> getStudentsForSponsor(User sponsor) {
        return studentRepository.findBySponsorOrderByFullNameAsc(sponsor)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteStudent(Long id, User sponsor) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (!student.getSponsor().getId().equals(sponsor.getId())) {
            throw new RuntimeException("Access denied");
        }
        studentRepository.delete(student);
    }

    public Dto.StudentResponse toResponse(Student s) {
        return Dto.StudentResponse.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .studentId(s.getStudentId())
                .className(s.getClassName())
                .academicYear(s.getAcademicYear())
                .term(s.getTerm())
                .build();
    }
}