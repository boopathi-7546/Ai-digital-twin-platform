package com.digitaltwin.platform.dto.twin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Frontend-facing view of a student's digital twin. behaviorProfile,
 * learningPattern, and careerPrediction are returned as generic maps
 * since their shape is AI-derived and may evolve without a schema change.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwinResponse {

    private Long twinId;
    private Long studentId;
    private Map<String, Object> behaviorProfile;
    private Map<String, Object> learningPattern;
    private Map<String, Object> careerPrediction;
    private BigDecimal confidenceIndex;
    private LocalDateTime lastGeneratedAt;
    private boolean generated;
}
