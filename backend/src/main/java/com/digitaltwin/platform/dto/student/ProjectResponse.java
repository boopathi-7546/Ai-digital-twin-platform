package com.digitaltwin.platform.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String techStack;
    private String projectUrl;
    private String repoUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean ongoing;
}
