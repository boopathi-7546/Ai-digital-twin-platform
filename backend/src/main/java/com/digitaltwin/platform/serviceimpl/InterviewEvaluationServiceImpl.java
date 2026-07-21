package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.entity.AiPrompt;
import com.digitaltwin.platform.entity.InterviewFeedback;
import com.digitaltwin.platform.entity.InterviewQuestion;
import com.digitaltwin.platform.entity.InterviewSession;
import com.digitaltwin.platform.repository.AiPromptRepository;
import com.digitaltwin.platform.repository.InterviewFeedbackRepository;
import com.digitaltwin.platform.repository.InterviewSessionRepository;
import com.digitaltwin.platform.service.InterviewEvaluationService;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewEvaluationServiceImpl implements InterviewEvaluationService {

    private final AiPromptRepository aiPromptRepository;
    private final InterviewFeedbackRepository interviewFeedbackRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void evaluateSession(InterviewSession session) {
        String qaPairsJson = buildQaPairsJson(session);
        String prompt = buildPrompt(qaPairsJson);

        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);
        JsonNode parsed = parseAiJson(cleanedJson);

        InterviewFeedback feedback = interviewFeedbackRepository.findBySessionId(session.getId())
                .orElseGet(() -> InterviewFeedback.builder().session(session).build());

        feedback.setConfidenceScore(getBigDecimal(parsed, "confidence_score"));
        feedback.setCommunicationScore(getBigDecimal(parsed, "communication_score"));
        feedback.setTechnicalScore(getBigDecimal(parsed, "technical_score"));
        feedback.setStrengths(parsed.path("strengths").toString());
        feedback.setWeaknesses(parsed.path("weaknesses").toString());
        feedback.setDetailedFeedback(parsed.path("detailed_feedback").asText(""));
        feedback.setRawAiResponse(aiRawResponse);

        interviewFeedbackRepository.save(feedback);

        BigDecimal overall = averageOf(
                feedback.getConfidenceScore(), feedback.getCommunicationScore(), feedback.getTechnicalScore());
        session.setOverallScore(overall);
        session.setStatus(InterviewSession.Status.COMPLETED);
        interviewSessionRepository.save(session);

        log.info("Interview feedback generated for sessionId={}", session.getId());
    }

    private String buildQaPairsJson(InterviewSession session) {
        List<Map<String, String>> qaPairs = new ArrayList<>();
        for (InterviewQuestion question : session.getQuestions()) {
            Map<String, String> pair = new LinkedHashMap<>();
            pair.put("question", question.getQuestionText());
            pair.put("answer", question.getAnswer() != null ? question.getAnswer().getAnswerText() : "");
            qaPairs.add(pair);
        }
        try {
            return objectMapper.writeValueAsString(qaPairs);
        } catch (Exception ex) {
            log.error("Failed to serialize Q&A pairs for evaluation: {}", ex.getMessage());
            return "[]";
        }
    }

    private String buildPrompt(String qaPairsJson) {
        AiPrompt promptTemplate = aiPromptRepository.findByPromptKeyAndActiveTrue(AiPrompt.INTERVIEW_FEEDBACK)
                .orElseThrow(() -> new IllegalStateException(
                        "INTERVIEW_FEEDBACK prompt template is not configured. Seed ai_prompts first."));

        return PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(), Map.of("qa_pairs_json", qaPairsJson));
    }

    private JsonNode parseAiJson(String cleanedJson) {
        try {
            return objectMapper.readTree(cleanedJson);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini JSON response for interview feedback: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException("The AI response could not be parsed. Please try again.", ex);
        }
    }

    private BigDecimal getBigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.decimalValue() : null;
    }

    private BigDecimal averageOf(BigDecimal... values) {
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (BigDecimal value : values) {
            if (value != null) {
                sum = sum.add(value);
                count++;
            }
        }
        return count == 0 ? null : sum.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }
}
