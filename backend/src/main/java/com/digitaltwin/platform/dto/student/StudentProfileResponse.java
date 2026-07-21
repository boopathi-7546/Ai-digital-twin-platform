package com.digitaltwin.platform.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * Full profile response returned by GET /api/student/profile. Combines
 * core Student fields with the account's identity fields (name/email)
 * so the frontend can render everything from one call.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private Long studentId;
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;

    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String country;
    private String collegeName;
    private String degree;
    private String branch;
    private Year graduationYear;
    private String profilePictureUrl;
    private String bio;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String targetRole;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<EducationResponse> education;
    private List<StudentSkillResponse> skills;
    private List<ProjectResponse> projects;
    private List<CertificationResponse> certifications;
    private List<AchievementResponse> achievements;
}
