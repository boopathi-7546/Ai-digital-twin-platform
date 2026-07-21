package com.digitaltwin.platform.dto.student;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Year;

/**
 * Payload for PUT /api/student/profile — updates the core Student
 * record (not education/skills/projects, which have their own
 * sub-resources).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 20)
    private String gender;

    @Size(max = 255)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 200, message = "College name must not exceed 200 characters")
    private String collegeName;

    @Size(max = 150)
    private String degree;

    @Size(max = 150)
    private String branch;

    private Year graduationYear;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*$", message = "Must be a valid LinkedIn URL")
    private String linkedinUrl;

    @Pattern(regexp = "^(https?://)?(www\\.)?github\\.com/.*$", message = "Must be a valid GitHub URL")
    private String githubUrl;

    @Size(max = 255)
    private String portfolioUrl;

    @Size(max = 150)
    private String targetRole;
}
