package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.student.*;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.mapper.StudentMapper;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements all student self-service profile operations. Every method
 * is scoped by the caller's userId (resolved by the controller from
 * the JWT principal), and sub-resource mutations always verify the
 * parent record belongs to that student before touching it — this is
 * the ownership boundary that keeps students from editing each other's data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final AchievementRepository achievementRepository;
    private final StudentMapper studentMapper;

    // ---------- Profile ----------

    @Override
    @Transactional
    public StudentProfileResponse getMyProfile(Long userId) {
        Student student = getOrCreateStudent(userId);
        return studentMapper.toProfileResponse(student);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateMyProfile(Long userId, StudentProfileRequest request) {
        Student student = getOrCreateStudent(userId);
        studentMapper.updateStudentFromRequest(request, student);
        studentRepository.save(student);
        log.info("Student profile updated for userId={}", userId);
        return studentMapper.toProfileResponse(student);
    }

    /**
     * Students don't get an explicit "create profile" step in the UI —
     * a bare Student row is created lazily the first time they touch
     * any profile endpoint after registering.
     */
    private Student getOrCreateStudent(Long userId) {
        return studentRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.forEntity("User", userId));
            Student newStudent = Student.builder().user(user).build();
            return studentRepository.save(newStudent);
        });
    }

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    // ---------- Education ----------

    @Override
    @Transactional
    public EducationResponse addEducation(Long userId, EducationRequest request) {
        Student student = getOrCreateStudent(userId);
        Education education = studentMapper.toEducationEntity(request);
        education.setStudent(student);
        educationRepository.save(education);
        return studentMapper.toEducationResponse(education);
    }

    @Override
    @Transactional
    public EducationResponse updateEducation(Long userId, Long educationId, EducationRequest request) {
        Student student = requireStudent(userId);
        Education education = educationRepository.findByIdAndStudentId(educationId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Education entry", educationId));
        studentMapper.updateEducationFromRequest(request, education);
        educationRepository.save(education);
        return studentMapper.toEducationResponse(education);
    }

    @Override
    @Transactional
    public void deleteEducation(Long userId, Long educationId) {
        Student student = requireStudent(userId);
        Education education = educationRepository.findByIdAndStudentId(educationId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Education entry", educationId));
        educationRepository.delete(education);
    }

    // ---------- Skills ----------

    @Override
    @Transactional
    public StudentSkillResponse addOrUpdateSkill(Long userId, StudentSkillRequest request) {
        Student student = getOrCreateStudent(userId);
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Skill", request.getSkillId()));

        StudentSkill studentSkill = studentSkillRepository
                .findByStudentIdAndSkillId(student.getId(), skill.getId())
                .orElseGet(() -> StudentSkill.builder().student(student).skill(skill).build());

        studentSkill.setProficiency(request.getProficiency());
        if (request.getYearsExperience() != null) {
            studentSkill.setYearsExperience(request.getYearsExperience());
        }

        studentSkillRepository.save(studentSkill);
        return studentMapper.toStudentSkillResponse(studentSkill);
    }

    @Override
    @Transactional
    public void removeSkill(Long userId, Long studentSkillId) {
        Student student = requireStudent(userId);
        StudentSkill studentSkill = studentSkillRepository.findByIdAndStudentId(studentSkillId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student skill", studentSkillId));
        studentSkillRepository.delete(studentSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSkillResponse> getMySkills(Long userId) {
        Student student = requireStudent(userId);
        return studentMapper.toStudentSkillResponseList(studentSkillRepository.findByStudentId(student.getId()));
    }

    // ---------- Projects ----------

    @Override
    @Transactional
    public ProjectResponse addProject(Long userId, ProjectRequest request) {
        Student student = getOrCreateStudent(userId);
        validateProjectDates(request);
        Project project = studentMapper.toProjectEntity(request);
        project.setStudent(student);
        projectRepository.save(project);
        return studentMapper.toProjectResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest request) {
        Student student = requireStudent(userId);
        validateProjectDates(request);
        Project project = projectRepository.findByIdAndStudentId(projectId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Project", projectId));
        studentMapper.updateProjectFromRequest(request, project);
        projectRepository.save(project);
        return studentMapper.toProjectResponse(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long userId, Long projectId) {
        Student student = requireStudent(userId);
        Project project = projectRepository.findByIdAndStudentId(projectId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Project", projectId));
        projectRepository.delete(project);
    }

    private void validateProjectDates(ProjectRequest request) {
        if (!request.isOngoing() && request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Project end date cannot be before the start date.");
        }
    }

    // ---------- Certifications ----------

    @Override
    @Transactional
    public CertificationResponse addCertification(Long userId, CertificationRequest request) {
        Student student = getOrCreateStudent(userId);
        Certification certification = studentMapper.toCertificationEntity(request);
        certification.setStudent(student);
        certificationRepository.save(certification);
        return studentMapper.toCertificationResponse(certification);
    }

    @Override
    @Transactional
    public CertificationResponse updateCertification(Long userId, Long certificationId, CertificationRequest request) {
        Student student = requireStudent(userId);
        Certification certification = certificationRepository.findByIdAndStudentId(certificationId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Certification", certificationId));
        studentMapper.updateCertificationFromRequest(request, certification);
        certificationRepository.save(certification);
        return studentMapper.toCertificationResponse(certification);
    }

    @Override
    @Transactional
    public void deleteCertification(Long userId, Long certificationId) {
        Student student = requireStudent(userId);
        Certification certification = certificationRepository.findByIdAndStudentId(certificationId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Certification", certificationId));
        certificationRepository.delete(certification);
    }

    // ---------- Achievements ----------

    @Override
    @Transactional
    public AchievementResponse addAchievement(Long userId, AchievementRequest request) {
        Student student = getOrCreateStudent(userId);
        Achievement achievement = studentMapper.toAchievementEntity(request);
        achievement.setStudent(student);
        achievementRepository.save(achievement);
        return studentMapper.toAchievementResponse(achievement);
    }

    @Override
    @Transactional
    public AchievementResponse updateAchievement(Long userId, Long achievementId, AchievementRequest request) {
        Student student = requireStudent(userId);
        Achievement achievement = achievementRepository.findByIdAndStudentId(achievementId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Achievement", achievementId));
        studentMapper.updateAchievementFromRequest(request, achievement);
        achievementRepository.save(achievement);
        return studentMapper.toAchievementResponse(achievement);
    }

    @Override
    @Transactional
    public void deleteAchievement(Long userId, Long achievementId) {
        Student student = requireStudent(userId);
        Achievement achievement = achievementRepository.findByIdAndStudentId(achievementId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Achievement", achievementId));
        achievementRepository.delete(achievement);
    }
}
