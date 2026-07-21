package com.digitaltwin.platform.dto.admin;

import com.digitaltwin.platform.entity.Report;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {

    @NotNull(message = "Report type is required")
    private Report.ReportType reportType;

    /** Required for RESUME/PROGRESS reports; ignored for platform-wide reports. */
    private Long studentId;
}
