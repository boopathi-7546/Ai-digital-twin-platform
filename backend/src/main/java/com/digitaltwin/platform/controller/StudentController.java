package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.student.*;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Self-service endpoints for the authenticated student's own profile,
 * education, skills, projects, certifications, and achievements.
 * The student's identity always comes from the JWT principal — never
 * from a path/body parameter — so one student can't act on another's data.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student Profile", description = "Manage the authenticated student's profile and related records")
public class StudentController {

    private final StudentService studentService;

    // ---------- Profile ----------

    @GetMapping("/profile")
    @Operation(summary = "Get the authenticated student's full profile")
    public ResponseEntity<StudentProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(studentService.getMyProfile(principal.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update the authenticated student's core profile fields")
    public ResponseEntity<StudentProfileResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(studentService.updateMyProfile(principal.getId(), request));
    }

    // ---------- Education ----------

    @PostMapping("/education")
    @Operation(summary = "Add an education entry")
    public ResponseEntity<EducationResponse> addEducation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addEducation(principal.getId(), request));
    }

    @PutMapping("/education/{educationId}")
    @Operation(summary = "Update an education entry")
    public ResponseEntity<EducationResponse> updateEducation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long educationId,
            @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(studentService.updateEducation(principal.getId(), educationId, request));
    }

    @DeleteMapping("/education/{educationId}")
    @Operation(summary = "Delete an education entry")
    public ResponseEntity<Map<String, String>> deleteEducation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long educationId) {
        studentService.deleteEducation(principal.getId(), educationId);
        return ResponseEntity.ok(Map.of("message", "Education entry deleted successfully."));
    }

    // ---------- Skills ----------

    @GetMapping("/skills")
    @Operation(summary = "List the authenticated student's skills")
    public ResponseEntity<List<StudentSkillResponse>> getMySkills(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(studentService.getMySkills(principal.getId()));
    }

    @PostMapping("/skills")
    @Operation(summary = "Add a skill or update its proficiency if already added")
    public ResponseEntity<StudentSkillResponse> addOrUpdateSkill(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody StudentSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addOrUpdateSkill(principal.getId(), request));
    }

    @DeleteMapping("/skills/{studentSkillId}")
    @Operation(summary = "Remove a skill from the student's profile")
    public ResponseEntity<Map<String, String>> removeSkill(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long studentSkillId) {
        studentService.removeSkill(principal.getId(), studentSkillId);
        return ResponseEntity.ok(Map.of("message", "Skill removed successfully."));
    }

    // ---------- Projects ----------

    @PostMapping("/projects")
    @Operation(summary = "Add a project")
    public ResponseEntity<ProjectResponse> addProject(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addProject(principal.getId(), request));
    }

    @PutMapping("/projects/{projectId}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ProjectResponse> updateProject(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(studentService.updateProject(principal.getId(), projectId, request));
    }

    @DeleteMapping("/projects/{projectId}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Map<String, String>> deleteProject(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long projectId) {
        studentService.deleteProject(principal.getId(), projectId);
        return ResponseEntity.ok(Map.of("message", "Project deleted successfully."));
    }

    // ---------- Certifications ----------

    @PostMapping("/certifications")
    @Operation(summary = "Add a certification")
    public ResponseEntity<CertificationResponse> addCertification(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addCertification(principal.getId(), request));
    }

    @PutMapping("/certifications/{certificationId}")
    @Operation(summary = "Update a certification")
    public ResponseEntity<CertificationResponse> updateCertification(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(studentService.updateCertification(principal.getId(), certificationId, request));
    }

    @DeleteMapping("/certifications/{certificationId}")
    @Operation(summary = "Delete a certification")
    public ResponseEntity<Map<String, String>> deleteCertification(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long certificationId) {
        studentService.deleteCertification(principal.getId(), certificationId);
        return ResponseEntity.ok(Map.of("message", "Certification deleted successfully."));
    }

    // ---------- Achievements ----------

    @PostMapping("/achievements")
    @Operation(summary = "Add an achievement")
    public ResponseEntity<AchievementResponse> addAchievement(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody AchievementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.addAchievement(principal.getId(), request));
    }

    @PutMapping("/achievements/{achievementId}")
    @Operation(summary = "Update an achievement")
    public ResponseEntity<AchievementResponse> updateAchievement(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long achievementId,
            @Valid @RequestBody AchievementRequest request) {
        return ResponseEntity.ok(studentService.updateAchievement(principal.getId(), achievementId, request));
    }

    @DeleteMapping("/achievements/{achievementId}")
    @Operation(summary = "Delete an achievement")
    public ResponseEntity<Map<String, String>> deleteAchievement(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long achievementId) {
        studentService.deleteAchievement(principal.getId(), achievementId);
        return ResponseEntity.ok(Map.of("message", "Achievement deleted successfully."));
    }
}
