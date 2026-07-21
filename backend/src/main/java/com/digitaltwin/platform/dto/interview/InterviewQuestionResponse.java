package com.digitaltwin.platform.dto.interview;

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
public class InterviewQuestionResponse {

    private Long questionId;
    private String questionText;
    private int sequenceNo;
    private String answerText;
    private Integer answerDurationSeconds;
    private boolean answered;
}
