package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.ai.GeminiClient;
import com.digitaltwin.platform.dto.resume.ResumeAnalysisResponse;
import com.digitaltwin.platform.dto.resume.ResumeUploadResponse;
import com.digitaltwin.platform.entity.AiPrompt;
import com.digitaltwin.platform.entity.Resume;
import com.digitaltwin.platform.entity.ResumeAnalysis;
import com.digitaltwin.platform.entity.Student;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.AiPromptRepository;
import com.digitaltwin.platform.repository.ResumeAnalysisRepository;
import com.digitaltwin.platform.repository.ResumeRepository;
import com.digitaltwin.platform.repository.StudentRepository;
import com.digitaltwin.platform.service.FileStorageService;
import com.digitaltwin.platform.service.ResumeAnalysisService;
import com.digitaltwin.platform.service.ResumeParserService;
import com.digitaltwin.platform.util.AppConstants;
import com.digitaltwin.platform.util.PromptTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements the full resume pipeline:
 * 1. Store the uploaded file on disk (FileStorageService)
 * 2. Extract raw text (ResumeParserService)
 * 3. Fill the RESUME_ANALYSIS prompt template with that text
 * 4. Call Gemini (GeminiClient) and parse its structured JSON reply
 * 5. Persist a ResumeAnalysis row and return a typed response
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final StudentRepository studentRepository;
    private final AiPromptRepository aiPromptRepository;
    private final FileStorageService fileStorageService;
    private final ResumeParserService resumeParserService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ResumeUploadResponse uploadResume(Long userId, MultipartFile file) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));

        String relativePath = fileStorageService.store(file, AppConstants.RESUME_SUBDIRECTORY);
        String extension = getExtension(file.getOriginalFilename());

        Resume resume = Resume.builder()
                .student(student)
                .fileName(file.getOriginalFilename())
                .storedPath(relativePath)
                .fileType(extension)
                .fileSizeBytes(file.getSize())
                .active(true)
                .build();

        resumeRepository.save(resume);
        log.info("Resume uploaded for studentId={}: resumeId={}", student.getId(), resume.getId());

        return toUploadResponse(resume);
    }

    @Override
    @Transactional
    public ResumeAnalysisResponse analyzeResume(Long userId, Long resumeId) {
        Student student = requireStudent(userId);
        Resume resume = resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Resume", resumeId));

        String resumeText = extractResumeText(resume);
        String prompt = buildPrompt(resumeText);

        String aiRawResponse = geminiClient.generateContent(prompt);
        String cleanedJson = GeminiClient.stripJsonFences(aiRawResponse);

        JsonNode parsed = parseAiJson(cleanedJson);

        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .resume(resume)
                .overallScore(getBigDecimal(parsed, "overall_score"))
                .atsScore(getBigDecimal(parsed, "ats_score"))
                .extractedSkills(getArrayAsJson(parsed, "extracted_skills"))
                .strengths(getArrayAsJson(parsed, "strengths"))
                .weaknesses(getArrayAsJson(parsed, "weaknesses"))
                .suggestions(getArrayAsJson(parsed, "suggestions"))
                .predictedRoles(getArrayAsJson(parsed, "predicted_roles"))
                .rawAiResponse(aiRawResponse)
                .build();

        resumeAnalysisRepository.save(analysis);
        log.info("Resume analysis completed for resumeId={}", resumeId);

        return toAnalysisResponse(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getLatestAnalysis(Long userId, Long resumeId) {
        Student student = requireStudent(userId);
        resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Resume", resumeId));

        ResumeAnalysis analysis = resumeAnalysisRepository.findFirstByResumeIdOrderByAnalyzedAtDesc(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "This resume has not been analyzed yet. Trigger an analysis first."));

        return toAnalysisResponse(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeUploadResponse> getMyResumes(Long userId) {
        Student student = requireStudent(userId);
        return resumeRepository.findByStudentIdOrderByUploadedAtDesc(student.getId())
                .stream()
                .map(this::toUploadResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadResume(Long userId, Long resumeId) {
        Student student = requireStudent(userId);
        Resume resume = resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Resume", resumeId));
        return fileStorageService.loadAsResource(resume.getStoredPath());
    }

    @Override
    @Transactional
    public void deleteResume(Long userId, Long resumeId) {
        Student student = requireStudent(userId);
        Resume resume = resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Resume", resumeId));

        fileStorageService.delete(resume.getStoredPath());
        resumeRepository.delete(resume);
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    private String extractResumeText(Resume resume) {
        Resource resource = fileStorageService.loadAsResource(resume.getStoredPath());
        try {
            return resumeParserService.extractText(resource.getInputStream(), resume.getFileType());
        } catch (IOException ex) {
            throw new BadRequestException("Could not read the stored resume file for analysis.");
        }
    }

    private String buildPrompt(String resumeText) {
        AiPrompt promptTemplate = aiPromptRepository.findByPromptKeyAndActiveTrue(AiPrompt.RESUME_ANALYSIS)
                .orElseThrow(() -> new IllegalStateException(
                        "RESUME_ANALYSIS prompt template is not configured. Seed ai_prompts first."));

        return PromptTemplateUtil.fill(promptTemplate.getPromptTemplate(), Map.of("resume_text", resumeText));
    }

    private JsonNode parseAiJson(String cleanedJson) {
        try {
            return objectMapper.readTree(cleanedJson);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini JSON response: {}", cleanedJson);
            throw new GeminiClient.GeminiApiException("The AI response could not be parsed. Please try again.", ex);
        }
    }

    private BigDecimal getBigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.decimalValue() : null;
    }

    private String getArrayAsJson(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? "[]" : value.toString();
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

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private ResumeUploadResponse toUploadResponse(Resume resume) {
        return ResumeUploadResponse.builder()
                .resumeId(resume.getId())
                .fileName(resume.getFileName())
                .fileType(resume.getFileType())
                .fileSizeBytes(resume.getFileSizeBytes())
                .uploadedAt(resume.getUploadedAt())
                .downloadUrl("/api/student/resumes/" + resume.getId() + "/download")
                .build();
    }

    private ResumeAnalysisResponse toAnalysisResponse(ResumeAnalysis analysis) {
        return ResumeAnalysisResponse.builder()
                .analysisId(analysis.getId())
                .resumeId(analysis.getResume().getId())
                .overallScore(analysis.getOverallScore())
                .atsScore(analysis.getAtsScore())
                .extractedSkills(jsonArrayToList(analysis.getExtractedSkills()))
                .strengths(jsonArrayToList(analysis.getStrengths()))
                .weaknesses(jsonArrayToList(analysis.getWeaknesses()))
                .suggestions(jsonArrayToList(analysis.getSuggestions()))
                .predictedRoles(jsonArrayToList(analysis.getPredictedRoles()))
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }
}
