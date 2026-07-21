package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.admin.GenerateReportRequest;
import com.digitaltwin.platform.dto.admin.ReportResponse;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Generates and manages admin-triggered reports (resume summaries,
 * interview summaries, per-student progress reports).
 */
public interface AdminReportService {

    ReportResponse generateReport(GenerateReportRequest request, Long adminUserId);

    List<ReportResponse> getAllReports();

    Resource downloadReport(Long reportId);
}
