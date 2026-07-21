package com.digitaltwin.platform.dto.skillgap;

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
public class SkillGapRequest {

    @NotBlank(message = "Target role is required")
    @Size(max = 150)
    private String targetRole;
}
