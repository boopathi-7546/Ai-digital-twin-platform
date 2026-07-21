package com.digitaltwin.platform.dto.interview;

import com.digitaltwin.platform.entity.QuestionBank;
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
public class QuestionBankResponse {

    private Long id;
    private String questionText;
    private String category;
    private String role;
    private QuestionBank.Difficulty difficulty;
    private boolean aiGenerated;
}
