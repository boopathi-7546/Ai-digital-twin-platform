package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.analytics.StudentAnalyticsResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student Analytics", description = "Aggregated dashboard metrics for the authenticated student")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    @Operation(summary = "Get dashboard analytics: resume/interview trends, skill distribution, roadmap progress")
    public ResponseEntity<StudentAnalyticsResponse> getMyAnalytics(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(analyticsService.getMyAnalytics(principal.getId()));
    }
}
