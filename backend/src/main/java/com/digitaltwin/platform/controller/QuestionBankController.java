package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.interview.QuestionBankResponse;
import com.digitaltwin.platform.entity.QuestionBank;
import com.digitaltwin.platform.repository.QuestionBankRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only access to the admin-curated static question bank, so
 * students can browse practice questions by role/category outside of
 * a live AI-generated mock interview session. Full CRUD management of
 * this bank lives in the Admin module (Phase 7).
 */
@RestController
@RequestMapping("/api/student/question-bank")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
@Tag(name = "Question Bank", description = "Browse the curated practice question bank")
public class QuestionBankController {

    private final QuestionBankRepository questionBankRepository;

    @GetMapping
    @Operation(summary = "List question bank entries, optionally filtered by role or category")
    public ResponseEntity<List<QuestionBankResponse>> listQuestions(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String category) {

        List<QuestionBank> questions;
        if (StringUtils.hasText(role)) {
            questions = questionBankRepository.findByRoleIgnoreCase(role);
        } else if (StringUtils.hasText(category)) {
            questions = questionBankRepository.findByCategoryIgnoreCase(category);
        } else {
            questions = questionBankRepository.findAll();
        }

        List<QuestionBankResponse> response = questions.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private QuestionBankResponse toResponse(QuestionBank question) {
        return QuestionBankResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory())
                .role(question.getRole())
                .difficulty(question.getDifficulty())
                .aiGenerated(question.isAiGenerated())
                .build();
    }
}
