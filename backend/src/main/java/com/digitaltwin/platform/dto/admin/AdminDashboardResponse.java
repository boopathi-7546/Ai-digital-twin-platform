package com.digitaltwin.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalStudents;
    private long activeStudents;
    private long totalResumesUploaded;
    private long totalInterviewSessions;
    private long completedInterviewSessions;
    private long totalSkillsInCatalog;
    private long totalQuestionsInBank;
    private long totalRoadmapsGenerated;
    private long totalReportsGenerated;
}
