package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.AiPromptRequest;
import com.digitaltwin.platform.dto.admin.AiPromptResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.AdminPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/prompts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - AI Prompts", description = "Manage AI prompt templates used across the platform's AI features")
public class AdminPromptController {

    private final AdminPromptService adminPromptService;

    @GetMapping
    @Operation(summary = "List all AI prompt templates")
    public ResponseEntity<List<AiPromptResponse>> getAllPrompts() {
        return ResponseEntity.ok(adminPromptService.getAllPrompts());
    }

    @PutMapping("/{promptKey}")
    @Operation(summary = "Update a prompt template's content, description, or active flag")
    public ResponseEntity<AiPromptResponse> updatePrompt(
            @PathVariable String promptKey,
            @Valid @RequestBody AiPromptRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(adminPromptService.updatePrompt(promptKey, request, principal.getUsername()));
    }
}
