package com.digitaltwin.platform.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

    @NotBlank(message = "Institution name is required")
    @Size(max = 200)
    private String institutionName;

    @NotBlank(message = "Qualification is required")
    @Size(max = 150)
    private String qualification;

    @Size(max = 150)
    private String fieldOfStudy;

    private Year startYear;

    private Year endYear;

    @Size(max = 50)
    private String grade;

    private boolean current;
}
