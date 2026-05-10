package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.*;
import com.gradeflow.repository.GradeRepository;
import com.gradeflow.repository.StudentRepository;
import com.gradeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final TemplateService templateService;
    private final StudentService studentService;

    // ── Teacher: submit or update a grade ──────────────────────────────────────

    public Dto.GradeResponse submitGrade(Dto.GradeRequest request, User teacher) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Grade grade = gradeRepository.findByStudentAndTeacher(student, teacher)
                .orElse(Grade.builder().student(student).teacher(teacher).subject(teacher.getSubject()).build());

        grade.setFirstTest(request.getFirstTest());
        grade.setSecondTest(request.getSecondTest());
        grade.setMidTerm(request.getMidTerm());
        grade.setHomework(request.getHomework());
        grade.setExam(request.getExam());
        grade.setRemarks(request.getRemarks());
        grade.setTotal(computeTotal(request));

        return toGradeResponse(gradeRepository.save(grade));
    }

    public List<Dto.GradeResponse> getMyGrades(User teacher) {
        return gradeRepository.findByTeacher(teacher)
                .stream().map(this::toGradeResponse).collect(Collectors.toList());
    }

    // ── Sponsor: grade sheet status list ───────────────────────────────────────

    public List<Dto.GradeSheetStatusResponse> getAllGradeSheetStatus(User sponsor) {
        GradeTemplate template = templateService.getLatestActiveTemplate();
        List<Student> students = studentRepository.findBySponsorOrderByFullNameAsc(sponsor);

        if (template == null || students.isEmpty()) {
            return students.stream().map(s -> Dto.GradeSheetStatusResponse.builder()
                    .studentId(s.getId())
                    .fullName(s.getFullName())
                    .className(s.getClassName())
                    .term(s.getTerm())
                    .submittedSubjects(0)
                    .totalSubjects(0)
                    .isComplete(false)
                    .build()).collect(Collectors.toList());
        }

        List<String> allSubjects = parseList(template.getSubjects());
        int total = allSubjects.size();

        // Load all grades for these students in one query
        Map<Long, List<Grade>> gradesByStudent = gradeRepository.findByStudentIn(students)
                .stream().collect(Collectors.groupingBy(g -> g.getStudent().getId()));

        return students.stream().map(s -> {
            List<Grade> grades = gradesByStudent.getOrDefault(s.getId(), List.of());
            Set<String> submittedSubjects = grades.stream()
                    .map(g -> g.getSubject().trim().toLowerCase())
                    .collect(Collectors.toSet());
            int submitted = (int) allSubjects.stream()
                    .filter(sub -> submittedSubjects.contains(sub.trim().toLowerCase()))
                    .count();
            return Dto.GradeSheetStatusResponse.builder()
                    .studentId(s.getId())
                    .fullName(s.getFullName())
                    .className(s.getClassName())
                    .term(s.getTerm())
                    .submittedSubjects(submitted)
                    .totalSubjects(total)
                    .isComplete(submitted == total && total > 0)
                    .build();
        }).collect(Collectors.toList());
    }

    // ── Sponsor: compiled grade sheet for one student ──────────────────────────

    public Dto.CompiledGradeSheetResponse getCompiledGradeSheet(Long studentId, User sponsor) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getSponsor().getId().equals(sponsor.getId())) {
            throw new RuntimeException("Access denied");
        }

        GradeTemplate template = templateService.getLatestActiveTemplate();
        if (template == null) throw new RuntimeException("No active template found");

        List<String> allSubjects = parseList(template.getSubjects());
        List<Grade> grades = gradeRepository.findByStudent(student);

        // Map subject name (lowercase) → grade
        Map<String, Grade> gradeMap = grades.stream()
                .collect(Collectors.toMap(g -> g.getSubject().trim().toLowerCase(), g -> g, (a, b) -> a));

        List<Dto.SubjectGrade> subjectGrades = new ArrayList<>();
        List<String> pending = new ArrayList<>();
        List<Double> totals = new ArrayList<>();

        for (String subject : allSubjects) {
            Grade g = gradeMap.get(subject.trim().toLowerCase());
            if (g != null) {
                subjectGrades.add(Dto.SubjectGrade.builder()
                        .subject(subject)
                        .firstTest(g.getFirstTest())
                        .secondTest(g.getSecondTest())
                        .midTerm(g.getMidTerm())
                        .homework(g.getHomework())
                        .exam(g.getExam())
                        .total(g.getTotal())
                        .remarks(g.getRemarks())
                        .build());
                if (g.getTotal() != null) totals.add(g.getTotal());
            } else {
                pending.add(subject);
                subjectGrades.add(Dto.SubjectGrade.builder().subject(subject).build());
            }
        }

        Double overallAverage = totals.isEmpty() ? null
                : Math.round(totals.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 10.0) / 10.0;

        boolean isComplete = pending.isEmpty();

        return Dto.CompiledGradeSheetResponse.builder()
                .student(studentService.toResponse(student))
                .template(templateService.toResponse(template))
                .grades(subjectGrades)
                .overallAverage(overallAverage)
                .overallRemark(deriveRemark(overallAverage))
                .pendingSubjects(pending)
                .isComplete(isComplete)
                .build();
    }

    // ── Teacher: list students available to grade ──────────────────────────────

    public List<Dto.StudentResponse> getStudentsForTeacher() {
        GradeTemplate template = templateService.getLatestActiveTemplate();
        if (template == null) return List.of();
        return studentRepository.findByAcademicYearAndTermOrderByFullNameAsc(
                        template.getAcademicYear(), template.getTerm())
                .stream().map(studentService::toResponse).collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Double computeTotal(Dto.GradeRequest r) {
        double sum = 0;
        if (r.getFirstTest()  != null) sum += r.getFirstTest();
        if (r.getSecondTest() != null) sum += r.getSecondTest();
        if (r.getMidTerm()    != null) sum += r.getMidTerm();
        if (r.getHomework()   != null) sum += r.getHomework();
        if (r.getExam()       != null) sum += r.getExam();
        return Math.round(sum * 10.0) / 10.0;
    }

    private String deriveRemark(Double avg) {
        if (avg == null) return "—";
        if (avg >= 80) return "Excellent";
        if (avg >= 70) return "Very Good";
        if (avg >= 60) return "Good";
        if (avg >= 50) return "Pass";
        return "Fail";
    }

    private List<String> parseList(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Dto.GradeResponse toGradeResponse(Grade g) {
        return Dto.GradeResponse.builder()
                .id(g.getId())
                .student(studentService.toResponse(g.getStudent()))
                .subject(g.getSubject())
                .firstTest(g.getFirstTest())
                .secondTest(g.getSecondTest())
                .midTerm(g.getMidTerm())
                .homework(g.getHomework())
                .exam(g.getExam())
                .total(g.getTotal())
                .remarks(g.getRemarks())
                .build();
    }
}