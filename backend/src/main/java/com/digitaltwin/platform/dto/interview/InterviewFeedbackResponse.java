package com.digitaltwin.platform.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewFeedbackResponse {

    private Long sessionId;
    private BigDecimal confidenceScore;
    private BigDecimal communicationScore;
    private BigDecimal technicalScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private String detailedFeedback;
    private LocalDateTime generatedAt;
}
