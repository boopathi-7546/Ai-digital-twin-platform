package com.digitaltwin.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPromptResponse {

    private Long id;
    private String promptKey;
    private String promptTemplate;
    private String description;
    private boolean active;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
