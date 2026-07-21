package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.skillgap.SkillGapRequest;
import com.digitaltwin.platform.dto.skillgap.SkillGapResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.SkillGapService;
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
@RequestMapping("/api/student/skill-gap")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Skill Gap Analysis", description = "Compare current skills against a target role")
public class SkillGapController {

    private final SkillGapService skillGapService;

    @PostMapping("/analyze")
    @Operation(summary = "Run an AI skill gap analysis for a target role")
    public ResponseEntity<SkillGapResponse> analyzeGap(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody SkillGapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillGapService.analyzeGap(principal.getId(), request));
    }

    @GetMapping
    @Operation(summary = "List all of the authenticated student's past skill gap analyses")
    public ResponseEntity<List<SkillGapResponse>> getMyAnalyses(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(skillGapService.getMyAnalyses(principal.getId()));
    }
}
