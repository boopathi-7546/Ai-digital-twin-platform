package com.digitaltwin.platform.dto.student;

import com.digitaltwin.platform.entity.StudentSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillResponse {

    private Long id;
    private Long skillId;
    private String skillName;
    private String category;
    private StudentSkill.Proficiency proficiency;
    private BigDecimal yearsExperience;
    private boolean verified;
}
