package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.interview.*;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.MockInterviewService;
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

/**
 * Endpoints for running an AI mock interview: start a session, submit
 * answers one at a time, complete it (triggers AI evaluation), and
 * fetch results.
 */
@RestController
@RequestMapping("/api/student/interviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Mock Interview", description = "Start, answer, and evaluate AI mock interview sessions")
public class InterviewController {

    private final MockInterviewService mockInterviewService;

    @PostMapping("/start")
    @Operation(summary = "Start a new mock interview session for a target role")
    public ResponseEntity<InterviewSessionResponse> startSession(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody StartInterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mockInterviewService.startSession(principal.getId(), request));
    }

    @GetMapping
    @Operation(summary = "List all of the authenticated student's interview sessions")
    public ResponseEntity<List<InterviewSessionResponse>> getMySessions(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(mockInterviewService.getMySessions(principal.getId()));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get a specific interview session with its questions and answers so far")
    public ResponseEntity<InterviewSessionResponse> getSession(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(mockInterviewService.getSession(principal.getId(), sessionId));
    }

    @PostMapping("/{sessionId}/answers")
    @Operation(summary = "Submit (or update) an answer to one question in the session")
    public ResponseEntity<InterviewQuestionResponse> submitAnswer(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return ResponseEntity.ok(mockInterviewService.submitAnswer(principal.getId(), sessionId, request));
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Mark the session complete and generate AI feedback")
    public ResponseEntity<InterviewFeedbackResponse> completeSession(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(mockInterviewService.completeSession(principal.getId(), sessionId));
    }

    @GetMapping("/{sessionId}/feedback")
    @Operation(summary = "Get the AI feedback for a completed session")
    public ResponseEntity<InterviewFeedbackResponse> getFeedback(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(mockInterviewService.getFeedback(principal.getId(), sessionId));
    }
}
