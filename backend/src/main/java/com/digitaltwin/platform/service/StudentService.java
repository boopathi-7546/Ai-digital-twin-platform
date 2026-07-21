package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.student.*;

import java.util.List;

/**
 * Business logic for a student's own profile and its sub-resources
 * (education, skills, projects, certifications, achievements).
 * All methods are scoped by the authenticated student's id, which the
 * controller resolves from the JWT principal — students can never
 * read or mutate another student's data through these methods.
 */
public interface StudentService {

    // Profile
    StudentProfileResponse getMyProfile(Long userId);

    StudentProfileResponse updateMyProfile(Long userId, StudentProfileRequest request);

    // Education
    EducationResponse addEducation(Long userId, EducationRequest request);

    EducationResponse updateEducation(Long userId, Long educationId, EducationRequest request);

    void deleteEducation(Long userId, Long educationId);

    // Skills
    StudentSkillResponse addOrUpdateSkill(Long userId, StudentSkillRequest request);

    void removeSkill(Long userId, Long studentSkillId);

    List<StudentSkillResponse> getMySkills(Long userId);

    // Projects
    ProjectResponse addProject(Long userId, ProjectRequest request);

    ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest request);

    void deleteProject(Long userId, Long projectId);

    // Certifications
    CertificationResponse addCertification(Long userId, CertificationRequest request);

    CertificationResponse updateCertification(Long userId, Long certificationId, CertificationRequest request);

    void deleteCertification(Long userId, Long certificationId);

    // Achievements
    AchievementResponse addAchievement(Long userId, AchievementRequest request);

    AchievementResponse updateAchievement(Long userId, Long achievementId, AchievementRequest request);

    void deleteAchievement(Long userId, Long achievementId);
}
