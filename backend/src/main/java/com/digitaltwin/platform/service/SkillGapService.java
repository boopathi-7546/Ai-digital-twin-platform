package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.skillgap.SkillGapRequest;
import com.digitaltwin.platform.dto.skillgap.SkillGapResponse;

import java.util.List;

/**
 * Compares a student's current skills against a target role's typical
 * requirements (via Gemini) and returns matched/missing skills with a
 * match percentage.
 */
public interface SkillGapService {

    SkillGapResponse analyzeGap(Long userId, SkillGapRequest request);

    List<SkillGapResponse> getMyAnalyses(Long userId);
}
