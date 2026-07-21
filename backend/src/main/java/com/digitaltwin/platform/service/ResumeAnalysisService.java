package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.resume.ResumeAnalysisResponse;
import com.digitaltwin.platform.dto.resume.ResumeUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Orchestrates the resume lifecycle: upload -> parse -> AI analysis ->
 * persistence, plus retrieval/download/delete of a student's resumes.
 */
public interface ResumeAnalysisService {

    ResumeUploadResponse uploadResume(Long userId, MultipartFile file);

    ResumeAnalysisResponse analyzeResume(Long userId, Long resumeId);

    ResumeAnalysisResponse getLatestAnalysis(Long userId, Long resumeId);

    List<ResumeUploadResponse> getMyResumes(Long userId);

    Resource downloadResume(Long userId, Long resumeId);

    void deleteResume(Long userId, Long resumeId);
}
