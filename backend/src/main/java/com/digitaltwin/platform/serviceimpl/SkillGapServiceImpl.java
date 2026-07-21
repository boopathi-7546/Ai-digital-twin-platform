package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.dto.skillgap.SkillGapRequest;
import com.digitaltwin.platform.dto.skillgap.SkillGapResponse;
import com.digitaltwin.platform.entity.AiPrompt;
import com.digitaltwin.platform.entity.SkillGapAnalysis;
import com.digitaltwin.platform.entity.Student;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.AiPromptRepository;
import com.digitaltwin.platform.repository.SkillGapAnalysisRepository;
import com.digitaltwin.platform.repository.StudentRepository;
import com.digitaltwin.platform.service.SkillGapService;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillGapServiceImpl implements SkillGapService {

    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final StudentRepository studentRepository;
    private final AiPromptRepository aiPromptRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public SkillGapResponse analyzeGap(Long userId, SkillGapRequest request) {
        Student student = requireStudent(userId);

        String currentSkillsJson = buildCurrentSkillsJson(student);
        String prompt = buildPrompt(currentSkillsJson, request.getTargetRole());

        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);
        JsonNode parsed = parseAiJson(cleanedJson);

        SkillGapAnalysis analysis = SkillGapAnalysis.builder()
                .student(student)
                .targetRole(request.getTargetRole())
                .matchedSkills(parsed.path("matched_skills").toString())
                .missingSkills(parsed.path("missing_skills").toString())
                .matchPercentage(getBigDecimal(parsed, "match_percentage"))
                .build();

        skillGapAnalysisRepository.save(analysis);
        log.info("Skill gap analysis completed for studentId={}, targetRole={}", student.getId(), request.getTargetRole());

        return toResponse(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillGapResponse> getMyAnalyses(Long userId) {
        Student student = requireStudent(userId);
        return skillGapAnalysisRepository.findByStudentIdOrderByAnalyzedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    private String buildCurrentSkillsJson(Student student) {
        List<Map<String, Object>> skills = student.getStudentSkills().stream()
                .map(ss -> Map.<String, Object>of(
                        "name", ss.getSkill().getName(),
                        "proficiency", ss.getProficiency().name()
                ))
                .collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (Exception ex) {
            log.error("Failed to serialize current skills for skill gap prompt: {}", ex.getMessage());
            return "[]";
        }
    }

    private String buildPrompt(String currentSkillsJson, String targetRole) {
        AiPrompt promptTemplate = aiPromptRepository.findByPromptKeyAndActiveTrue(AiPrompt.SKILL_GAP_ANALYSIS)
                .orElseThrow(() -> new IllegalStateException(
                        "SKILL_GAP_ANALYSIS prompt template is not configured. Seed ai_prompts first."));

        return PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(), Map.of(
                "current_skills_json", currentSkillsJson,
                "target_role", targetRole
        ));
    }

    private JsonNode parseAiJson(String cleanedJson) {
        try {
            return objectMapper.readTree(cleanedJson);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini JSON response for skill gap: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException("The AI response could not be parsed. Please try again.", ex);
        }
    }

    private BigDecimal getBigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.decimalValue() : null;
    }

    private List<String> jsonArrayToList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            JsonNode arrayNode = objectMapper.readTree(json);
            List<String> result = new ArrayList<>();
            arrayNode.forEach(n -> result.add(n.asText()));
            return result;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private SkillGapResponse toResponse(SkillGapAnalysis analysis) {
        return SkillGapResponse.builder()
                .id(analysis.getId())
                .targetRole(analysis.getTargetRole())
                .matchedSkills(jsonArrayToList(analysis.getMatchedSkills()))
                .missingSkills(jsonArrayToList(analysis.getMissingSkills()))
                .matchPercentage(analysis.getMatchPercentage())
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }
}
