package com.digitaltwin.platform.dto.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Parsed, frontend-friendly view of a resume analysis. The raw JSON
 * columns on ResumeAnalysis are deserialized into typed lists here so
 * the React app never has to parse JSON strings itself.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisResponse {

    private Long analysisId;
    private Long resumeId;
    private BigDecimal overallScore;
    private BigDecimal atsScore;
    private List<String> extractedSkills;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private List<String> predictedRoles;
    private LocalDateTime analyzedAt;
}
