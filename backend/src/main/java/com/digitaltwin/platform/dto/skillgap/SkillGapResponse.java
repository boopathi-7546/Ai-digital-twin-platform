package com.digitaltwin.platform.dto.skillgap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapResponse {

    private Long id;
    private String targetRole;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private BigDecimal matchPercentage;
    private LocalDateTime analyzedAt;
}
