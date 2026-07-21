package com.digitaltwin.platform.mapper;

import com.digitaltwin.platform.dto.student.*;
import com.digitaltwin.platform.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Maps between Student (+ sub-entities) and their DTOs. Update methods
 * only touch mutable fields, ignoring nulls, so partial updates from
 * the frontend don't wipe out unrelated columns.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    // ---------- Student core profile ----------

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "userId", source = "student.user.id")
    @Mapping(target = "fullName", source = "student.user.fullName")
    @Mapping(target = "email", source = "student.user.email")
    @Mapping(target = "phoneNumber", source = "student.user.phoneNumber")
    @Mapping(target = "education", source = "student.education")
    @Mapping(target = "skills", source = "student.studentSkills")
    @Mapping(target = "projects", source = "student.projects")
    @Mapping(target = "certifications", source = "student.certifications")
    @Mapping(target = "achievements", source = "student.achievements")
    StudentProfileResponse toProfileResponse(Student student);

    void updateStudentFromRequest(StudentProfileRequest request, @MappingTarget Student student);

    // ---------- Education ----------

    Education toEducationEntity(EducationRequest request);

    EducationResponse toEducationResponse(Education education);

    List<EducationResponse> toEducationResponseList(List<Education> education);

    void updateEducationFromRequest(EducationRequest request, @MappingTarget Education education);

    // ---------- Student Skill ----------

    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "skillName", source = "skill.name")
    @Mapping(target = "category", source = "skill.category")
    StudentSkillResponse toStudentSkillResponse(StudentSkill studentSkill);

    List<StudentSkillResponse> toStudentSkillResponseList(List<StudentSkill> studentSkills);

    // ---------- Project ----------

    Project toProjectEntity(ProjectRequest request);

    ProjectResponse toProjectResponse(Project project);

    List<ProjectResponse> toProjectResponseList(List<Project> projects);

    void updateProjectFromRequest(ProjectRequest request, @MappingTarget Project project);

    // ---------- Certification ----------

    Certification toCertificationEntity(CertificationRequest request);

    CertificationResponse toCertificationResponse(Certification certification);

    List<CertificationResponse> toCertificationResponseList(List<Certification> certifications);

    void updateCertificationFromRequest(CertificationRequest request, @MappingTarget Certification certification);

    // ---------- Achievement ----------

    Achievement toAchievementEntity(AchievementRequest request);

    AchievementResponse toAchievementResponse(Achievement achievement);

    List<AchievementResponse> toAchievementResponseList(List<Achievement> achievements);

    void updateAchievementFromRequest(AchievementRequest request, @MappingTarget Achievement achievement);
}
