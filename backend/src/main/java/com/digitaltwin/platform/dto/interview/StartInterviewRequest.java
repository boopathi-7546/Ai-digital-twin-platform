package com.digitaltwin.platform.dto.interview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartInterviewRequest {

    @NotBlank(message = "Target role is required")
    @Size(max = 150)
    private String targetRole;

    @Min(value = 3, message = "A session must have at least 3 questions")
    @Max(value = 15, message = "A session can have at most 15 questions")
    private int questionCount = 5;

    private String difficulty; // EASY, MEDIUM, HARD — optional hint to the generator
}
