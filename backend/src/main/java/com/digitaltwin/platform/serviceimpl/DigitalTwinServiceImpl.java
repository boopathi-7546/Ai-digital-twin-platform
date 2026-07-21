package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.dto.twin.DigitalTwinResponse;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.DigitalTwinService;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a student's digital twin by summarizing their profile,
 * skills, and most recent resume analysis into a compact JSON blob,
 * sending it to Gemini via the DIGITAL_TWIN_BEHAVIOR prompt, and
 * persisting the structured result.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalTwinServiceImpl implements DigitalTwinService {

    private final DigitalTwinRepository digitalTwinRepository;
    private final StudentRepository studentRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final AiPromptRepository aiPromptRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public DigitalTwinResponse getMyTwin(Long userId) {
        Student student = requireStudent(userId);
        return digitalTwinRepository.findByStudentId(student.getId())
                .map(this::toResponse)
                .orElseGet(() -> DigitalTwinResponse.builder()
                        .studentId(student.getId())
                        .generated(false)
                        .build());
    }

    @Override
    @Transactional
    public DigitalTwinResponse regenerateMyTwin(Long userId) {
        Student student = requireStudent(userId);

        String studentProfileJson = buildStudentProfileJson(student);
        String prompt = buildPrompt(studentProfileJson);

        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);
        JsonNode parsed = parseAiJson(cleanedJson);

        DigitalTwin twin = digitalTwinRepository.findByStudentId(student.getId())
                .orElseGet(() -> DigitalTwin.builder().student(student).build());

        twin.setBehaviorProfile(parsed.path("behavior_profile").toString());
        twin.setLearningPattern(parsed.path("learning_pattern").toString());
        twin.setCareerPrediction(parsed.path("career_prediction").toString());
        twin.setConfidenceIndex(getBigDecimal(parsed, "confidence_index"));
        twin.setLastGeneratedAt(LocalDateTime.now());

        digitalTwinRepository.save(twin);
        log.info("Digital twin regenerated for studentId={}", student.getId());

        return toResponse(twin);
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    private String buildStudentProfileJson(Student student) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("targetRole", student.getTargetRole());
        profile.put("degree", student.getDegree());
        profile.put("branch", student.getBranch());
        profile.put("bio", student.getBio());

        profile.put("skills", student.getStudentSkills().stream()
                .map(ss -> Map.of(
                        "name", ss.getSkill().getName(),
                        "proficiency", ss.getProficiency().name(),
                        "yearsExperience", ss.getYearsExperience()
                ))
                .collect(Collectors.toList()));

        profile.put("projectCount", student.getProjects().size());
        profile.put("certificationCount", student.getCertifications().size());

        resumeRepository.findFirstByStudentIdAndActiveTrueOrderByUploadedAtDesc(student.getId())
                .flatMap(resume -> resumeAnalysisRepository.findFirstByResumeIdOrderByAnalyzedAtDesc(resume.getId()))
                .ifPresent(analysis -> {
                    profile.put("latestResumeOverallScore", analysis.getOverallScore());
                    profile.put("latestResumeStrengths", analysis.getStrengths());
                    profile.put("latestResumeWeaknesses", analysis.getWeaknesses());
                });

        try {
            return objectMapper.writeValueAsString(profile);
        } catch (Exception ex) {
            log.error("Failed to serialize student profile for digital twin prompt: {}", ex.getMessage());
            return "{}";
        }
    }

    private String buildPrompt(String studentProfileJson) {
        AiPrompt promptTemplate = aiPromptRepository.findByPromptKeyAndActiveTrue(AiPrompt.DIGITAL_TWIN_BEHAVIOR)
                .orElseThrow(() -> new IllegalStateException(
                        "DIGITAL_TWIN_BEHAVIOR prompt template is not configured. Seed ai_prompts first."));

        return PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(),
                Map.of("student_profile_json", studentProfileJson));
    }

    private JsonNode parseAiJson(String cleanedJson) {
        try {
            return objectMapper.readTree(cleanedJson);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini JSON response for digital twin: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException("The AI response could not be parsed. Please try again.", ex);
        }
    }

    private BigDecimal getBigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.decimalValue() : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isBlank() || "null".equals(json)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    private DigitalTwinResponse toResponse(DigitalTwin twin) {
        return DigitalTwinResponse.builder()
                .twinId(twin.getId())
                .studentId(twin.getStudent().getId())
                .behaviorProfile(jsonToMap(twin.getBehaviorProfile()))
                .learningPattern(jsonToMap(twin.getLearningPattern()))
                .careerPrediction(jsonToMap(twin.getCareerPrediction()))
                .confidenceIndex(twin.getConfidenceIndex())
                .lastGeneratedAt(twin.getLastGeneratedAt())
                .generated(true)
                .build();
    }
}
