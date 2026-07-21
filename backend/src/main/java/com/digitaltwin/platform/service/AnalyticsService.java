package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.analytics.StudentAnalyticsResponse;

/**
 * Aggregates a student's data (resumes, interviews, skills, roadmaps)
 * into dashboard-ready metrics and chart-friendly series.
 */
public interface AnalyticsService {

    StudentAnalyticsResponse getMyAnalytics(Long userId);
}
