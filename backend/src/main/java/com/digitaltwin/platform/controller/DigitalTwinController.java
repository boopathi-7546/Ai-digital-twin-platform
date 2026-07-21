package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.twin.DigitalTwinResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.DigitalTwinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints for retrieving and regenerating a student's digital twin.
 */
@RestController
@RequestMapping("/api/student/digital-twin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Digital Twin", description = "AI-derived behavior, learning pattern, and career prediction")
public class DigitalTwinController {

    private final DigitalTwinService digitalTwinService;

    @GetMapping
    @Operation(summary = "Get the authenticated student's current digital twin, if generated")
    public ResponseEntity<DigitalTwinResponse> getMyTwin(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(digitalTwinService.getMyTwin(principal.getId()));
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Regenerate the digital twin using the latest profile, skills, and resume analysis")
    public ResponseEntity<DigitalTwinResponse> regenerateMyTwin(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(digitalTwinService.regenerateMyTwin(principal.getId()));
    }
}
