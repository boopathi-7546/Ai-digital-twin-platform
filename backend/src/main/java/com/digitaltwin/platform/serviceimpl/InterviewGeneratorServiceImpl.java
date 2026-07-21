package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.entity.AiPrompt;
import com.digitaltwin.platform.repository.AiPromptRepository;
import com.digitaltwin.platform.service.InterviewGeneratorService;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewGeneratorServiceImpl implements InterviewGeneratorService {

    private final AiPromptRepository aiPromptRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> generateQuestions(String targetRole, int count, String difficulty) {
        AiPrompt promptTemplate = aiPromptRepository
                .findByPromptKeyAndActiveTrue(AiPrompt.INTERVIEW_QUESTION_GENERATION)
                .orElseThrow(() -> new IllegalStateException(
                        "INTERVIEW_QUESTION_GENERATION prompt template is not configured. Seed ai_prompts first."));

        String prompt = PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(), Map.of(
                "count", String.valueOf(count),
                "target_role", targetRole,
                "difficulty", difficulty == null || difficulty.isBlank() ? "MEDIUM" : difficulty
        ));

        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);

        try {
            JsonNode arrayNode = objectMapper.readTree(cleanedJson);
            List<String> questions = new ArrayList<>();
            arrayNode.forEach(node -> questions.add(node.asText()));
            return questions;
        } catch (Exception ex) {
            log.error("Failed to parse Gemini question generation response: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException(
                    "The AI response for question generation could not be parsed. Please try again.", ex);
        }
    }
}
