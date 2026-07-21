package com.digitaltwin.platform.dto.admin;

import com.digitaltwin.platform.entity.QuestionBank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    @Size(max = 100)
    private String category;

    @Size(max = 150)
    private String role;

    @NotNull(message = "Difficulty is required")
    private QuestionBank.Difficulty difficulty;
}
