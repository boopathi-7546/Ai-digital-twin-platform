package com.digitaltwin.platform.dto.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull(message = "Question id is required")
    private Long questionId;

    @NotBlank(message = "Answer text is required")
    private String answerText;

    @PositiveOrZero(message = "Answer duration cannot be negative")
    private Integer answerDurationSeconds;
}
