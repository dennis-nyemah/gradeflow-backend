package com.gradeflow.dto;

import com.gradeflow.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// ─────────────────────────────────────────
// Auth
// ─────────────────────────────────────────

public class Dto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank private String fullName;
        @NotBlank private String username;
        @NotBlank private String password;
        @NotNull  private User.Role role;
        private String subject; // required for TEACHER

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public User.Role getRole() {
            return role;
        }

        public void setRole(User.Role role) {
            this.role = role;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private Long id;
        private String fullName;
        private String username;
        private String role;
        private String subject;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    // ─────────────────────────────────────────
    // Template
    // ─────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TemplateRequest {
        @NotBlank private String schoolName;
        private String schoolAddress;
        private String schoolMotto;
        @NotBlank private String academicYear;
        @NotBlank private String term;
        @NotBlank private String className;
        @NotBlank private String subjects;
        @NotBlank private String gradingColumns;
        private String gradingScale;

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(String schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public String getSchoolMotto() {
            return schoolMotto;
        }

        public void setSchoolMotto(String schoolMotto) {
            this.schoolMotto = schoolMotto;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSubjects() {
            return subjects;
        }

        public void setSubjects(String subjects) {
            this.subjects = subjects;
        }

        public String getGradingColumns() {
            return gradingColumns;
        }

        public void setGradingColumns(String gradingColumns) {
            this.gradingColumns = gradingColumns;
        }

        public String getGradingScale() {
            return gradingScale;
        }

        public void setGradingScale(String gradingScale) {
            this.gradingScale = gradingScale;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TemplateResponse {
        private Long id;
        private String schoolName;
        private String schoolAddress;
        private String schoolMotto;
        private String academicYear;
        private String term;
        private String className;
        private String subjects;
        private String gradingColumns;
        private String gradingScale;
        private boolean active;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(String schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public String getSchoolMotto() {
            return schoolMotto;
        }

        public void setSchoolMotto(String schoolMotto) {
            this.schoolMotto = schoolMotto;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSubjects() {
            return subjects;
        }

        public void setSubjects(String subjects) {
            this.subjects = subjects;
        }

        public String getGradingColumns() {
            return gradingColumns;
        }

        public void setGradingColumns(String gradingColumns) {
            this.gradingColumns = gradingColumns;
        }

        public String getGradingScale() {
            return gradingScale;
        }

        public void setGradingScale(String gradingScale) {
            this.gradingScale = gradingScale;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    // ─────────────────────────────────────────
    // Student
    // ─────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StudentRequest {
        @NotBlank private String fullName;
        private String studentId;
        @NotBlank private String className;
        @NotBlank private String academicYear;
        @NotBlank private String term;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudentResponse {
        private Long id;
        private String fullName;
        private String studentId;
        private String className;
        private String academicYear;
        private String term;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

    // ─────────────────────────────────────────
    // Teacher (list view)
    // ─────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TeacherResponse {
        private Long id;
        private String fullName;
        private String username;
        private String subject;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    // ─────────────────────────────────────────
    // Grade submission
    // ─────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class GradeRequest {
        @NotNull private Long studentId;
        private Double firstTest;
        private Double secondTest;
        private Double midTerm;
        private Double homework;
        private Double exam;
        private String remarks;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Double getFirstTest() {
            return firstTest;
        }

        public void setFirstTest(Double firstTest) {
            this.firstTest = firstTest;
        }

        public Double getSecondTest() {
            return secondTest;
        }

        public void setSecondTest(Double secondTest) {
            this.secondTest = secondTest;
        }

        public Double getMidTerm() {
            return midTerm;
        }

        public void setMidTerm(Double midTerm) {
            this.midTerm = midTerm;
        }

        public Double getHomework() {
            return homework;
        }

        public void setHomework(Double homework) {
            this.homework = homework;
        }

        public Double getExam() {
            return exam;
        }

        public void setExam(Double exam) {
            this.exam = exam;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GradeResponse {
        private Long id;
        private StudentResponse student;
        private String subject;
        private Double firstTest;
        private Double secondTest;
        private Double midTerm;
        private Double homework;
        private Double exam;
        private Double total;
        private String remarks;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public StudentResponse getStudent() {
            return student;
        }

        public void setStudent(StudentResponse student) {
            this.student = student;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Double getFirstTest() {
            return firstTest;
        }

        public void setFirstTest(Double firstTest) {
            this.firstTest = firstTest;
        }

        public Double getSecondTest() {
            return secondTest;
        }

        public void setSecondTest(Double secondTest) {
            this.secondTest = secondTest;
        }

        public Double getMidTerm() {
            return midTerm;
        }

        public void setMidTerm(Double midTerm) {
            this.midTerm = midTerm;
        }

        public Double getHomework() {
            return homework;
        }

        public void setHomework(Double homework) {
            this.homework = homework;
        }

        public Double getExam() {
            return exam;
        }

        public void setExam(Double exam) {
            this.exam = exam;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    // ─────────────────────────────────────────
    // Grade Sheet (compiled, per student)
    // ─────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GradeSheetStatusResponse {
        private Long studentId;
        private String fullName;
        private String className;
        private String term;
        private int submittedSubjects;
        private int totalSubjects;
        private boolean isComplete;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public int getSubmittedSubjects() {
            return submittedSubjects;
        }

        public void setSubmittedSubjects(int submittedSubjects) {
            this.submittedSubjects = submittedSubjects;
        }

        public int getTotalSubjects() {
            return totalSubjects;
        }

        public void setTotalSubjects(int totalSubjects) {
            this.totalSubjects = totalSubjects;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CompiledGradeSheetResponse {
        private StudentResponse student;
        private TemplateResponse template;
        private List<SubjectGrade> grades;
        private Double overallAverage;
        private String overallRemark;
        private List<String> pendingSubjects;
        private boolean isComplete;

        public StudentResponse getStudent() {
            return student;
        }

        public void setStudent(StudentResponse student) {
            this.student = student;
        }

        public TemplateResponse getTemplate() {
            return template;
        }

        public void setTemplate(TemplateResponse template) {
            this.template = template;
        }

        public List<SubjectGrade> getGrades() {
            return grades;
        }

        public void setGrades(List<SubjectGrade> grades) {
            this.grades = grades;
        }

        public Double getOverallAverage() {
            return overallAverage;
        }

        public void setOverallAverage(Double overallAverage) {
            this.overallAverage = overallAverage;
        }

        public String getOverallRemark() {
            return overallRemark;
        }

        public void setOverallRemark(String overallRemark) {
            this.overallRemark = overallRemark;
        }

        public List<String> getPendingSubjects() {
            return pendingSubjects;
        }

        public void setPendingSubjects(List<String> pendingSubjects) {
            this.pendingSubjects = pendingSubjects;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubjectGrade {
        private String subject;
        private Double firstTest;
        private Double secondTest;
        private Double midTerm;
        private Double homework;
        private Double exam;
        private Double total;
        private String remarks;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Double getFirstTest() {
            return firstTest;
        }

        public void setFirstTest(Double firstTest) {
            this.firstTest = firstTest;
        }

        public Double getSecondTest() {
            return secondTest;
        }

        public void setSecondTest(Double secondTest) {
            this.secondTest = secondTest;
        }

        public Double getMidTerm() {
            return midTerm;
        }

        public void setMidTerm(Double midTerm) {
            this.midTerm = midTerm;
        }

        public Double getHomework() {
            return homework;
        }

        public void setHomework(Double homework) {
            this.homework = homework;
        }

        public Double getExam() {
            return exam;
        }

        public void setExam(Double exam) {
            this.exam = exam;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    // ─────────────────────────────────────────
    // Generic error response
    // ─────────────────────────────────────────

    @Data @AllArgsConstructor
    public static class ErrorResponse {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}