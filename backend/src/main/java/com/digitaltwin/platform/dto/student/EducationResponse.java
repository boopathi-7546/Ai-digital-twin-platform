package com.digitaltwin.platform.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {

    private Long id;
    private String institutionName;
    private String qualification;
    private String fieldOfStudy;
    private Year startYear;
    private Year endYear;
    private String grade;
    private boolean current;
}
