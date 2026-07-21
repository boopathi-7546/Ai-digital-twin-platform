package com.digitaltwin.platform.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    @Size(max = 500, message = "Tech stack must not exceed 500 characters")
    private String techStack;

    @Size(max = 255)
    private String projectUrl;

    @Size(max = 255)
    private String repoUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean ongoing;
}
