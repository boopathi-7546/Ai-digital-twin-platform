package com.digitaltwin.platform.dto.interview;

import com.digitaltwin.platform.entity.InterviewSession;
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
public class InterviewSessionResponse {

    private Long sessionId;
    private String targetRole;
    private InterviewSession.Status status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private BigDecimal overallScore;
    private List<InterviewQuestionResponse> questions;
}
