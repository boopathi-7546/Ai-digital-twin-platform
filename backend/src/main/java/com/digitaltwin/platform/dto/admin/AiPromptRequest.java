package com.digitaltwin.platform.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPromptRequest {

    @NotBlank(message = "Prompt template content is required")
    private String promptTemplate;

    private String description;

    private boolean active = true;
}
