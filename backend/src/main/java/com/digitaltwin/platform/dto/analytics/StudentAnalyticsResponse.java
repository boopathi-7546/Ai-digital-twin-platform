package com.digitaltwin.platform.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Aggregated metrics powering the student dashboard's charts: resume
 * score trend, interview score trend, skill distribution, roadmap
 * progress. Kept as simple maps/lists so Recharts on the frontend can
 * consume it directly without extra transformation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalyticsResponse {

    private int totalResumesUploaded;
    private BigDecimal latestResumeScore;
    private int totalInterviewsCompleted;
    private BigDecimal averageInterviewScore;
    private int totalSkillsTracked;
    private int totalProjects;
    private int totalCertifications;
    private int activeRoadmapCount;
    private int completedRoadmapItemCount;
    private int totalRoadmapItemCount;

    /** proficiency level -> count, for a simple skill distribution chart */
    private Map<String, Long> skillsByProficiency;

    /** chronological list of {date, score} for resume score trend */
    private List<Map<String, Object>> resumeScoreTrend;

    /** chronological list of {date, score} for interview score trend */
    private List<Map<String, Object>> interviewScoreTrend;
}
