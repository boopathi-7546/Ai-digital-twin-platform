package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.resume.ResumeAnalysisResponse;
import com.digitaltwin.platform.dto.resume.ResumeUploadResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Endpoints for uploading resumes, triggering AI analysis, and
 * downloading/listing/deleting a student's own resumes.
 */
@RestController
@RequestMapping("/api/student/resumes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Resume", description = "Upload, analyze, and manage resumes")
public class ResumeController {

    private final ResumeAnalysisService resumeAnalysisService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a resume file (PDF or DOCX)")
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeAnalysisService.uploadResume(principal.getId(), file));
    }

    @PostMapping("/{resumeId}/analyze")
    @Operation(summary = "Run AI analysis on a previously uploaded resume")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeAnalysisService.analyzeResume(principal.getId(), resumeId));
    }

    @GetMapping("/{resumeId}/analysis")
    @Operation(summary = "Get the most recent AI analysis for a resume")
    public ResponseEntity<ResumeAnalysisResponse> getLatestAnalysis(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeAnalysisService.getLatestAnalysis(principal.getId(), resumeId));
    }

    @GetMapping
    @Operation(summary = "List all resumes uploaded by the authenticated student")
    public ResponseEntity<List<ResumeUploadResponse>> getMyResumes(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(resumeAnalysisService.getMyResumes(principal.getId()));
    }

    @GetMapping("/{resumeId}/download")
    @Operation(summary = "Download the original resume file")
    public ResponseEntity<Resource> downloadResume(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long resumeId) {
        Resource resource = resumeAnalysisService.downloadResume(principal.getId(), resumeId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{resumeId}")
    @Operation(summary = "Delete a resume and its analysis history")
    public ResponseEntity<Map<String, String>> deleteResume(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long resumeId) {
        resumeAnalysisService.deleteResume(principal.getId(), resumeId);
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully."));
    }
}
