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
public class AchievementRequest {

    @NotBlank(message = "Achievement title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDate achievementDate;
}
