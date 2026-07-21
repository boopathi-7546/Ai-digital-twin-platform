package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.twin.DigitalTwinResponse;

/**
 * Builds and retrieves a student's AI-derived "digital twin": inferred
 * behavior profile, learning pattern, and career prediction, generated
 * from their profile, skills, and resume analysis history.
 */
public interface DigitalTwinService {

    DigitalTwinResponse getMyTwin(Long userId);

    DigitalTwinResponse regenerateMyTwin(Long userId);
}
