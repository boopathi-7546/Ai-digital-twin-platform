package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.roadmap.MarkItemCompleteRequest;
import com.digitaltwin.platform.dto.roadmap.RoadmapItemResponse;
import com.digitaltwin.platform.dto.roadmap.RoadmapResponse;

import java.util.List;

/**
 * Generates a personalized learning roadmap (courses/projects/
 * certifications/skills) from a student's most recent skill-gap
 * analysis for a target role, and tracks completion of its items.
 */
public interface RoadmapService {

    RoadmapResponse generateRoadmap(Long userId, Long skillGapAnalysisId);

    List<RoadmapResponse> getMyRoadmaps(Long userId);

    RoadmapResponse getRoadmap(Long userId, Long roadmapId);

    RoadmapItemResponse markItemComplete(Long userId, Long roadmapId, Long itemId, MarkItemCompleteRequest request);
}
