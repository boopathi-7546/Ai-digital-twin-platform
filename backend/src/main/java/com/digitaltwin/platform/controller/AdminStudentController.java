package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.AdminStudentSummaryResponse;
import com.digitaltwin.platform.dto.admin.UpdateStudentStatusRequest;
import com.digitaltwin.platform.service.AdminStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Students", description = "Manage student accounts")
public class AdminStudentController {

    private final AdminStudentService adminStudentService;

    @GetMapping
    @Operation(summary = "List all students")
    public ResponseEntity<List<AdminStudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(adminStudentService.getAllStudents());
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get a single student's summary")
    public ResponseEntity<AdminStudentSummaryResponse> getStudentById(@PathVariable Long studentId) {
        return ResponseEntity.ok(adminStudentService.getStudentById(studentId));
    }

    @PatchMapping("/{studentId}/status")
    @Operation(summary = "Activate or deactivate a student's account")
    public ResponseEntity<AdminStudentSummaryResponse> updateStudentStatus(
            @PathVariable Long studentId,
            @Valid @RequestBody UpdateStudentStatusRequest request) {
        return ResponseEntity.ok(adminStudentService.updateStudentStatus(studentId, request));
    }
}
