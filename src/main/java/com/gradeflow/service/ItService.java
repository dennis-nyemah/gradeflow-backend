package com.gradeflow.service;

import com.gradeflow.dto.Dto;
import com.gradeflow.entity.SponsorTeacher;
import com.gradeflow.entity.User;
import com.gradeflow.repository.SponsorTeacherRepository;
import com.gradeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IT Service
 *
 * Administrative service responsible for:
 * - Managing sponsor accounts
 * - Managing teacher accounts
 * - Assigning teachers to sponsors
 * - Removing teacher assignments
 * - Providing sponsor-teacher relationship data
 *
 * This service acts as the administration layer of the GradeFlow system.
 */
@Service
@RequiredArgsConstructor
public class ItService {

    private final UserRepository userRepository;
    private final SponsorTeacherRepository sponsorTeacherRepository;
    private final PasswordEncoder passwordEncoder;

    // ─────────────────────────────────────────────
    // Sponsor Management
    // ─────────────────────────────────────────────

    /**
     * Creates a sponsor account.
     *
     * Responsibilities:
     * - Validate unique username
     * - Encrypt password before storage
     * - Assign SPONSOR role
     * - Store sponsor grade level
     *
     * @param req sponsor creation request
     * @return created sponsor response DTO
     */
    public Dto.SponsorResponse createSponsor(Dto.CreateSponsorRequest req) {

        // Prevent duplicate usernames
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());
        }

        // Build sponsor entity
        User sponsor = User.builder()
                .fullName(req.getFullName())
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(User.Role.SPONSOR)
                .gradeLevel(req.getGradeLevel())
                .build();
        return toSponsorResponse(userRepository.save(sponsor));
    }

    /**
     * Returns all sponsor accounts.
     */
    public List<Dto.SponsorResponse> getAllSponsors() {
        return userRepository.findByRole(User.Role.SPONSOR)
                .stream().map(this::toSponsorResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Teacher Management
    // ─────────────────────────────────────────────

    /**
     * Creates a teacher account.
     *
     * Responsibilities:
     * - Validate unique username
     * - Encrypt password
     * - Assign TEACHER role
     * - Store teacher subject(s) specialization
     *
     * @param req teacher creation request
     * @return created teacher response DTO
     */
    public Dto.TeacherResponse createTeacher(Dto.CreateTeacherRequest req) {

        // Prevent duplicate usernames
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());
        }

        // Build teacher entity
        User teacher = User.builder()
                .fullName(req.getFullName())
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(User.Role.TEACHER)
                .subject(req.getSubject())
                .build();
        return toTeacherResponse(userRepository.save(teacher));
    }

    /**
     * Returns all teacher accounts.
     */
    public List<Dto.TeacherResponse> getAllTeachers() {
        return userRepository.findByRole(User.Role.TEACHER)
                .stream().map(this::toTeacherResponse).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Teacher ↔ Sponsor Assignments
    // ─────────────────────────────────────────────

    /**
     * Assigns a teacher to a sponsor.
     *
     * Validation:
     * - Sponsor must exist
     * - Teacher must exist
     * - Roles must match expected types
     * - Duplicate assignments are prevented
     *
     * @param req assignment request
     */
    public void assignTeacher(Dto.AssignTeacherRequest req) {
        User sponsor = userRepository.findById(req.getSponsorId())
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        User teacher = userRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Validate roles
        if (sponsor.getRole() != User.Role.SPONSOR) throw new IllegalArgumentException("Target is not a sponsor");

        if (teacher.getRole() != User.Role.TEACHER) throw new IllegalArgumentException("Target is not a teacher");

        // Prevent duplicate assignment
        if (!sponsorTeacherRepository.existsBySponsorAndTeacher(sponsor, teacher)) {
            sponsorTeacherRepository.save(SponsorTeacher.builder()
                    .sponsor(sponsor).teacher(teacher).build());
        }
    }

    /**
     * Removes a teacher assignment from a sponsor.
     *
     * Transactional because it performs database modification.
     *
     * @param req unassignment request
     */
    @Transactional
    public void unassignTeacher(Dto.AssignTeacherRequest req) {
        User sponsor = userRepository.findById(req.getSponsorId())
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        User teacher = userRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        sponsorTeacherRepository.deleteBySponsorAndTeacher(sponsor, teacher);
    }

    // ─────────────────────────────────────────────
    // Assignment Queries
    // ─────────────────────────────────────────────

    /**
     * Returns all teachers assigned to a specific sponsor.
     *
     * @param sponsorId sponsor ID
     * @return list of teacher response DTOs
     */
    public List<Dto.TeacherResponse> getTeachersForSponsor(Long sponsorId) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        return sponsorTeacherRepository.findBySponsor(sponsor).stream()
                .map(st -> toTeacherResponse(st.getTeacher()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all sponsors a teacher is assigned to.
     *
     * @param teacherId teacher ID
     * @return list of sponsor response DTOs
     */
    public List<Dto.SponsorResponse> getSponsorsForTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return sponsorTeacherRepository.findByTeacher(teacher).stream()
                .map(st -> toSponsorResponse(st.getSponsor()))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Sponsor-facing Teacher View
    // ─────────────────────────────────────────────

    /**
     * Returns simplified teacher information visible to sponsors.
     *
     * Exposes:
     * - Teacher full name
     * - Teacher subject
     *
     * @param sponsor sponsor user
     * @return sponsor-safe teacher view
     */
    public List<Dto.SponsorTeacherView> getTeacherViewForSponsor(User sponsor) {
        return sponsorTeacherRepository.findBySponsor(sponsor).stream()
                .map(st -> Dto.SponsorTeacherView.builder()
                        .fullName(st.getTeacher().getFullName())
                        .subject(st.getTeacher().getSubject())
                        .build())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // DTO Mapping Helpers
    // ─────────────────────────────────────────────

    /**
     * Converts sponsor entity to response DTO.
     */
    public Dto.SponsorResponse toSponsorResponse(User u) {
        return Dto.SponsorResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .username(u.getUsername())
                .gradeLevel(u.getGradeLevel())
                .build();
    }

    /**
     * Converts teacher entity to response DTO.
     */
    public Dto.TeacherResponse toTeacherResponse(User u) {
        return Dto.TeacherResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .username(u.getUsername())
                .subject(u.getSubject())
                .build();
    }
}