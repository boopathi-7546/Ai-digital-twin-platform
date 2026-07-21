package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.QuestionBankRequest;
import com.digitaltwin.platform.dto.interview.InterviewSessionResponse;
import com.digitaltwin.platform.dto.interview.QuestionBankResponse;
import com.digitaltwin.platform.service.AdminInterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/interviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Interviews", description = "Manage the curated question bank and view all interview sessions")
public class AdminInterviewController {

    private final AdminInterviewService adminInterviewService;

    @PostMapping("/questions")
    @Operation(summary = "Add a question to the curated question bank")
    public ResponseEntity<QuestionBankResponse> createQuestion(@Valid @RequestBody QuestionBankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminInterviewService.createQuestion(request));
    }

    @PutMapping("/questions/{questionId}")
    @Operation(summary = "Update a question bank entry")
    public ResponseEntity<QuestionBankResponse> updateQuestion(
            @PathVariable Long questionId, @Valid @RequestBody QuestionBankRequest request) {
        return ResponseEntity.ok(adminInterviewService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Delete a question bank entry")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable Long questionId) {
        adminInterviewService.deleteQuestion(questionId);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully."));
    }

    @GetMapping("/sessions")
    @Operation(summary = "List all students' interview sessions")
    public ResponseEntity<List<InterviewSessionResponse>> getAllSessions() {
        return ResponseEntity.ok(adminInterviewService.getAllSessions());
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get a specific interview session by id")
    public ResponseEntity<InterviewSessionResponse> getSessionById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(adminInterviewService.getSessionById(sessionId));
    }
}
