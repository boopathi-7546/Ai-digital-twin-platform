package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.GenerateReportRequest;
import com.digitaltwin.platform.dto.admin.ReportResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.AdminReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Reports", description = "Generate and download resume/interview/progress reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a new report")
    public ResponseEntity<ReportResponse> generateReport(
            @Valid @RequestBody GenerateReportRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminReportService.generateReport(request, principal.getId()));
    }

    @GetMapping
    @Operation(summary = "List all generated reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(adminReportService.getAllReports());
    }

    @GetMapping("/{reportId}/download")
    @Operation(summary = "Download a generated report file")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
        Resource resource = adminReportService.downloadReport(reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}
