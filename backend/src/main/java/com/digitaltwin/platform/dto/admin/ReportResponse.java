package com.digitaltwin.platform.dto.admin;

import com.digitaltwin.platform.entity.Report;
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
public class ReportResponse {

    private Long id;
    private Report.ReportType reportType;
    private Long studentId;
    private String studentName;
    private String downloadUrl;
    private LocalDateTime generatedAt;
}
