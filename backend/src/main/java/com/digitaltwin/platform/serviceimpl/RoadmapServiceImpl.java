package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.dto.roadmap.MarkItemCompleteRequest;
import com.digitaltwin.platform.dto.roadmap.RoadmapItemResponse;
import com.digitaltwin.platform.dto.roadmap.RoadmapResponse;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.RoadmapService;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapItemRepository roadmapItemRepository;
    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final StudentRepository studentRepository;
    private final AiPromptRepository aiPromptRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public RoadmapResponse generateRoadmap(Long userId, Long skillGapAnalysisId) {
        Student student = requireStudent(userId);

        SkillGapAnalysis gapAnalysis = skillGapAnalysisRepository.findById(skillGapAnalysisId)
                .filter(a -> a.getStudent().getId().equals(student.getId()))
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Skill gap analysis", skillGapAnalysisId));

        String prompt = buildPrompt(gapAnalysis.getMissingSkills(), gapAnalysis.getTargetRole());
        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);
        JsonNode itemsArray = parseAiJson(cleanedJson);

        Roadmap roadmap = Roadmap.builder()
                .student(student)
                .targetRole(gapAnalysis.getTargetRole())
                .title("Roadmap for " + gapAnalysis.getTargetRole())
                .description("AI-generated learning path based on your skill gap analysis dated "
                        + gapAnalysis.getAnalyzedAt().toLocalDate())
                .status(Roadmap.Status.ACTIVE)
                .build();
        roadmapRepository.save(roadmap);

        int sequence = 1;
        if (itemsArray.isArray()) {
            for (JsonNode itemNode : itemsArray) {
                RoadmapItem item = RoadmapItem.builder()
                        .roadmap(roadmap)
                        .itemType(parseItemType(itemNode.path("item_type").asText("SKILL")))
                        .title(itemNode.path("title").asText("Untitled item"))
                        .description(itemNode.path("description").asText(""))
                        .resourceUrl(itemNode.path("resource_url").asText(null))
                        .sequenceNo(sequence++)
                        .build();
                roadmapItemRepository.save(item);
                roadmap.getItems().add(item);
            }
        }

        log.info("Roadmap generated for studentId={}, targetRole={}, items={}",
                student.getId(), gapAnalysis.getTargetRole(), roadmap.getItems().size());

        return toResponse(roadmap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadmapResponse> getMyRoadmaps(Long userId) {
        Student student = requireStudent(userId);
        return roadmapRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmap(Long userId, Long roadmapId) {
        Student student = requireStudent(userId);
        Roadmap roadmap = roadmapRepository.findByIdAndStudentId(roadmapId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Roadmap", roadmapId));
        return toResponse(roadmap);
    }

    @Override
    @Transactional
    public RoadmapItemResponse markItemComplete(Long userId, Long roadmapId, Long itemId,
                                                 MarkItemCompleteRequest request) {
        Student student = requireStudent(userId);
        Roadmap roadmap = roadmapRepository.findByIdAndStudentId(roadmapId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Roadmap", roadmapId));

        RoadmapItem item = roadmapItemRepository.findByIdAndRoadmapId(itemId, roadmap.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Roadmap item", itemId));

        item.setCompleted(request.isCompleted());
        item.setCompletedAt(request.isCompleted() ? LocalDateTime.now() : null);
        roadmapItemRepository.save(item);

        return toItemResponse(item);
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    private String buildPrompt(String missingSkillsJson, String targetRole) {
        AiPrompt promptTemplate = aiPromptRepository.findByPromptKeyAndActiveTrue(AiPrompt.ROADMAP_GENERATION)
                .orElseThrow(() -> new IllegalStateException(
                        "ROADMAP_GENERATION prompt template is not configured. Seed ai_prompts first."));

        return PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(), Map.of(
                "missing_skills_json", missingSkillsJson == null ? "[]" : missingSkillsJson,
                "target_role", targetRole
        ));
    }

    private JsonNode parseAiJson(String cleanedJson) {
        try {
            return objectMapper.readTree(cleanedJson);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini JSON response for roadmap: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException("The AI response could not be parsed. Please try again.", ex);
        }
    }

    private RoadmapItem.ItemType parseItemType(String raw) {
        try {
            return RoadmapItem.ItemType.valueOf(raw.toUpperCase());
        } catch (Exception ex) {
            return RoadmapItem.ItemType.SKILL;
        }
    }

    private RoadmapResponse toResponse(Roadmap roadmap) {
        List<RoadmapItemResponse> itemResponses = roadmap.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        long completedCount = itemResponses.stream().filter(RoadmapItemResponse::isCompleted).count();

        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .targetRole(roadmap.getTargetRole())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .status(roadmap.getStatus())
                .createdAt(roadmap.getCreatedAt())
                .items(itemResponses)
                .completedItemCount((int) completedCount)
                .totalItemCount(itemResponses.size())
                .build();
    }

    private RoadmapItemResponse toItemResponse(RoadmapItem item) {
        return RoadmapItemResponse.builder()
                .id(item.getId())
                .itemType(item.getItemType())
                .title(item.getTitle())
                .description(item.getDescription())
                .resourceUrl(item.getResourceUrl())
                .sequenceNo(item.getSequenceNo())
                .completed(item.isCompleted())
                .completedAt(item.getCompletedAt())
                .build();
    }
}
