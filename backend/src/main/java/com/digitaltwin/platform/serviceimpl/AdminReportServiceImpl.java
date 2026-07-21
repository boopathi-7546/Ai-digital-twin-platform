package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.GenerateReportRequest;
import com.digitaltwin.platform.dto.admin.ReportResponse;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.AdminReportService;
import com.digitaltwin.platform.service.FileStorageService;
import com.digitaltwin.platform.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates simple, human-readable plain-text report files (stored
 * locally via FileStorageService) summarizing a student's resume
 * analyses, interview performance, or overall progress. Kept as plain
 * text rather than a binary format to avoid pulling in a PDF-rendering
 * dependency purely for admin-facing summaries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ReportResponse generateReport(GenerateReportRequest request, Long adminUserId) {
        String content;
        Student student = null;

        switch (request.getReportType()) {
            case RESUME -> {
                student = requireStudent(request.getStudentId());
                content = buildResumeReport(student);
            }
            case INTERVIEW -> {
                student = requireStudent(request.getStudentId());
                content = buildInterviewReport(student);
            }
            case PROGRESS -> {
                student = requireStudent(request.getStudentId());
                content = buildProgressReport(student);
            }
            default -> throw new BadRequestException("Unsupported report type: " + request.getReportType());
        }

        String fileName = request.getReportType().name().toLowerCase() + "-report-"
                + (student != null ? student.getId() : "platform") + ".txt";
        String relativePath = fileStorageService.storeBytes(
                content.getBytes(StandardCharsets.UTF_8), AppConstants.REPORT_SUBDIRECTORY, fileName);

        Report report = Report.builder()
                .student(student)
                .reportType(request.getReportType())
                .filePath(relativePath)
                .generatedBy(adminUserId)
                .build();
        reportRepository.save(report);

        log.info("Report generated: type={}, studentId={}, reportId={}",
                request.getReportType(), request.getStudentId(), report.getId());

        return toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByGeneratedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Report", reportId));
        return fileStorageService.loadAsResource(report.getFilePath());
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long studentId) {
        if (studentId == null) {
            throw new BadRequestException("studentId is required for this report type.");
        }
        return studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student", studentId));
    }

    private String buildResumeReport(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESUME REPORT\n");
        sb.append("Student: ").append(student.getUser().getFullName())
                .append(" (").append(student.getUser().getEmail()).append(")\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now().format(TS)).append("\n\n");

        List<Resume> resumes = resumeRepository.findByStudentIdOrderByUploadedAtDesc(student.getId());
        if (resumes.isEmpty()) {
            sb.append("No resumes uploaded.\n");
            return sb.toString();
        }

        for (Resume resume : resumes) {
            sb.append("- ").append(resume.getFileName())
                    .append(" (uploaded ").append(resume.getUploadedAt().format(TS)).append(")\n");
            resumeAnalysisRepository.findFirstByResumeIdOrderByAnalyzedAtDesc(resume.getId()).ifPresent(analysis -> {
                sb.append("  Overall score: ").append(analysis.getOverallScore()).append("\n");
                sb.append("  ATS score: ").append(analysis.getAtsScore()).append("\n");
            });
        }
        return sb.toString();
    }

    private String buildInterviewReport(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("INTERVIEW PERFORMANCE REPORT\n");
        sb.append("Student: ").append(student.getUser().getFullName())
                .append(" (").append(student.getUser().getEmail()).append(")\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now().format(TS)).append("\n\n");

        List<InterviewSession> sessions = interviewSessionRepository.findByStudentIdOrderByStartedAtDesc(student.getId());
        if (sessions.isEmpty()) {
            sb.append("No interview sessions recorded.\n");
            return sb.toString();
        }

        for (InterviewSession session : sessions) {
            sb.append("- Session #").append(session.getId())
                    .append(" | Role: ").append(session.getTargetRole())
                    .append(" | Status: ").append(session.getStatus())
                    .append(" | Score: ").append(session.getOverallScore() != null ? session.getOverallScore() : "N/A")
                    .append("\n");
        }
        return sb.toString();
    }

    private String buildProgressReport(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("OVERALL PROGRESS REPORT\n");
        sb.append("Student: ").append(student.getUser().getFullName())
                .append(" (").append(student.getUser().getEmail()).append(")\n");
        sb.append("Generated: ").append(java.time.LocalDateTime.now().format(TS)).append("\n\n");

        sb.append("Target role: ").append(student.getTargetRole()).append("\n");
        sb.append("Skills tracked: ").append(student.getStudentSkills().size()).append("\n");
        sb.append("Projects: ").append(student.getProjects().size()).append("\n");
        sb.append("Certifications: ").append(student.getCertifications().size()).append("\n");
        sb.append("Resumes uploaded: ")
                .append(resumeRepository.findByStudentIdOrderByUploadedAtDesc(student.getId()).size()).append("\n");
        sb.append("Interview sessions: ")
                .append(interviewSessionRepository.findByStudentIdOrderByStartedAtDesc(student.getId()).size()).append("\n");

        return sb.toString();
    }

    private ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reportType(report.getReportType())
                .studentId(report.getStudent() != null ? report.getStudent().getId() : null)
                .studentName(report.getStudent() != null ? report.getStudent().getUser().getFullName() : null)
                .downloadUrl("/api/admin/reports/" + report.getId() + "/download")
                .generatedAt(report.getGeneratedAt())
                .build();
    }
}
