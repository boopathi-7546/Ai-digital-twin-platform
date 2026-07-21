package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.roadmap.MarkItemCompleteRequest;
import com.digitaltwin.platform.dto.roadmap.RoadmapItemResponse;
import com.digitaltwin.platform.dto.roadmap.RoadmapResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/roadmaps")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Learning Roadmap", description = "Generate and track a personalized learning roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate/{skillGapAnalysisId}")
    @Operation(summary = "Generate a roadmap from a previous skill gap analysis")
    public ResponseEntity<RoadmapResponse> generateRoadmap(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long skillGapAnalysisId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roadmapService.generateRoadmap(principal.getId(), skillGapAnalysisId));
    }

    @GetMapping
    @Operation(summary = "List all of the authenticated student's roadmaps")
    public ResponseEntity<List<RoadmapResponse>> getMyRoadmaps(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(roadmapService.getMyRoadmaps(principal.getId()));
    }

    @GetMapping("/{roadmapId}")
    @Operation(summary = "Get a single roadmap with all its items")
    public ResponseEntity<RoadmapResponse> getRoadmap(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long roadmapId) {
        return ResponseEntity.ok(roadmapService.getRoadmap(principal.getId(), roadmapId));
    }

    @PatchMapping("/{roadmapId}/items/{itemId}")
    @Operation(summary = "Mark a roadmap item complete or incomplete")
    public ResponseEntity<RoadmapItemResponse> markItemComplete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long roadmapId,
            @PathVariable Long itemId,
            @Valid @RequestBody MarkItemCompleteRequest request) {
        return ResponseEntity.ok(roadmapService.markItemComplete(principal.getId(), roadmapId, itemId, request));
    }
}
