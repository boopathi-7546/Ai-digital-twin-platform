package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.AdminStudentSummaryResponse;
import com.digitaltwin.platform.dto.admin.UpdateStudentStatusRequest;
import com.digitaltwin.platform.entity.Student;
import com.digitaltwin.platform.entity.User;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.InterviewSessionRepository;
import com.digitaltwin.platform.repository.ResumeRepository;
import com.digitaltwin.platform.repository.StudentRepository;
import com.digitaltwin.platform.repository.UserRepository;
import com.digitaltwin.platform.service.AdminStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStudentServiceImpl implements AdminStudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminStudentSummaryResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStudentSummaryResponse getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", studentId));
        return toSummaryResponse(student);
    }

    @Override
    @Transactional
    public AdminStudentSummaryResponse updateStudentStatus(Long studentId, UpdateStudentStatusRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", studentId));

        User user = student.getUser();
        user.setActive(request.getActive());
        userRepository.save(user);

        log.info("Admin set active={} for studentId={}", request.getActive(), studentId);
        return toSummaryResponse(student);
    }

    private AdminStudentSummaryResponse toSummaryResponse(Student student) {
        User user = student.getUser();
        int resumeCount = resumeRepository.findByStudentIdOrderByUploadedAtDesc(student.getId()).size();
        int interviewCount = interviewSessionRepository.findByStudentIdOrderByStartedAtDesc(student.getId()).size();

        return AdminStudentSummaryResponse.builder()
                .studentId(student.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .collegeName(student.getCollegeName())
                .targetRole(student.getTargetRole())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(student.getCreatedAt())
                .resumeCount(resumeCount)
                .interviewSessionCount(interviewCount)
                .build();
    }
}
