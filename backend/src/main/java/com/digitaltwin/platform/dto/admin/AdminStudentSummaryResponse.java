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
public class AdminStudentSummaryResponse {

    private Long studentId;
    private Long userId;
    private String fullName;
    private String email;
    private String collegeName;
    private String targetRole;
    private boolean active;
    private boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private int resumeCount;
    private int interviewSessionCount;
}
