package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.analytics.StudentAnalyticsResponse;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pulls together counts and trends across resumes, interviews, skills,
 * and roadmaps for a single student dashboard call, so the frontend
 * doesn't need to make five separate requests to render its charts.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final StudentRepository studentRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final RoadmapRepository roadmapRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentAnalyticsResponse getMyAnalytics(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));

        List<Resume> resumes = resumeRepository.findByStudentIdOrderByUploadedAtDesc(student.getId());

        List<Map<String, Object>> resumeScoreTrend = resumes.stream()
                .flatMap(resume -> resumeAnalysisRepository.findFirstByResumeIdOrderByAnalyzedAtDesc(resume.getId()).stream())
                .sorted((a, b) -> a.getAnalyzedAt().compareTo(b.getAnalyzedAt()))
                .map(analysis -> {
                    Map<String, Object> point = new LinkedHashMap<>();
                    point.put("date", analysis.getAnalyzedAt().format(DATE_FORMAT));
                    point.put("score", analysis.getOverallScore());
                    return point;
                })
                .collect(Collectors.toList());

        BigDecimal latestResumeScore = resumeScoreTrend.isEmpty()
                ? null
                : (BigDecimal) resumeScoreTrend.get(resumeScoreTrend.size() - 1).get("score");

        List<InterviewSession> completedSessions = interviewSessionRepository
                .findByStudentIdOrderByStartedAtDesc(student.getId())
                .stream()
                .filter(s -> s.getStatus() == InterviewSession.Status.COMPLETED)
                .toList();

        List<Map<String, Object>> interviewScoreTrend = completedSessions.stream()
                .sorted((a, b) -> a.getStartedAt().compareTo(b.getStartedAt()))
                .map(session -> {
                    Map<String, Object> point = new LinkedHashMap<>();
                    point.put("date", session.getStartedAt().format(DATE_FORMAT));
                    point.put("score", session.getOverallScore());
                    return point;
                })
                .collect(Collectors.toList());

        BigDecimal averageInterviewScore = null;
        if (!completedSessions.isEmpty()) {
            BigDecimal sum = completedSessions.stream()
                    .map(InterviewSession::getOverallScore)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageInterviewScore = sum.divide(BigDecimal.valueOf(completedSessions.size()), 2, RoundingMode.HALF_UP);
        }

        Map<String, Long> skillsByProficiency = student.getStudentSkills().stream()
                .collect(Collectors.groupingBy(ss -> ss.getProficiency().name(), Collectors.counting()));

        List<Roadmap> roadmaps = roadmapRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        int activeRoadmapCount = (int) roadmaps.stream()
                .filter(r -> r.getStatus() == Roadmap.Status.ACTIVE)
                .count();
        int completedRoadmapItems = roadmaps.stream()
                .flatMap(r -> r.getItems().stream())
                .filter(RoadmapItem::isCompleted)
                .mapToInt(i -> 1)
                .sum();
        int totalRoadmapItems = roadmaps.stream()
                .mapToInt(r -> r.getItems().size())
                .sum();

        return StudentAnalyticsResponse.builder()
                .totalResumesUploaded(resumes.size())
                .latestResumeScore(latestResumeScore)
                .totalInterviewsCompleted(completedSessions.size())
                .averageInterviewScore(averageInterviewScore)
                .totalSkillsTracked(student.getStudentSkills().size())
                .totalProjects(student.getProjects().size())
                .totalCertifications(student.getCertifications().size())
                .activeRoadmapCount(activeRoadmapCount)
                .completedRoadmapItemCount(completedRoadmapItems)
                .totalRoadmapItemCount(totalRoadmapItems)
                .skillsByProficiency(skillsByProficiency)
                .resumeScoreTrend(resumeScoreTrend)
                .interviewScoreTrend(interviewScoreTrend)
                .build();
    }
}
