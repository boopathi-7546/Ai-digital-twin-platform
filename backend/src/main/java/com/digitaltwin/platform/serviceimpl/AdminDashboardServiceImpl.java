package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.AdminDashboardResponse;
import com.digitaltwin.platform.entity.InterviewSession;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final SkillRepository skillRepository;
    private final QuestionBankRepository questionBankRepository;
    private final RoadmapRepository roadmapRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStats() {
        long totalStudents = studentRepository.count();
        long activeStudents = userRepository.findAll().stream().filter(u -> u.isActive()).count();

        long completedInterviews = interviewSessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == InterviewSession.Status.COMPLETED)
                .count();

        return AdminDashboardResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .totalResumesUploaded(resumeRepository.count())
                .totalInterviewSessions(interviewSessionRepository.count())
                .completedInterviewSessions(completedInterviews)
                .totalSkillsInCatalog(skillRepository.count())
                .totalQuestionsInBank(questionBankRepository.count())
                .totalRoadmapsGenerated(roadmapRepository.count())
                .totalReportsGenerated(reportRepository.count())
                .build();
    }
}
