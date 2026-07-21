package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.admin.AdminStudentSummaryResponse;
import com.digitaltwin.platform.dto.admin.UpdateStudentStatusRequest;

import java.util.List;

/**
 * Admin-facing student management: list/search students and toggle
 * their account active status (e.g. suspend a student).
 */
public interface AdminStudentService {

    List<AdminStudentSummaryResponse> getAllStudents();

    AdminStudentSummaryResponse getStudentById(Long studentId);

    AdminStudentSummaryResponse updateStudentStatus(Long studentId, UpdateStudentStatusRequest request);
}
