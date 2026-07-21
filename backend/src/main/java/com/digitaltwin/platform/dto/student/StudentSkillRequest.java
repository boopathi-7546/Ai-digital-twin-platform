package com.digitaltwin.platform.dto.student;

import com.digitaltwin.platform.entity.StudentSkill;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkillRequest {

    @NotNull(message = "Skill id is required")
    private Long skillId;

    @NotNull(message = "Proficiency level is required")
    private StudentSkill.Proficiency proficiency;

    @DecimalMin(value = "0.0", message = "Years of experience cannot be negative")
    private BigDecimal yearsExperience;
}
