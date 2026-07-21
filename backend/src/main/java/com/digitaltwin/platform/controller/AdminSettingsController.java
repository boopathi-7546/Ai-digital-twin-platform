package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.SystemSettingsResponse;
import com.digitaltwin.platform.service.AdminSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Settings", description = "Read-only view of key platform runtime configuration")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @GetMapping
    @Operation(summary = "Get current system settings (JWT expiry, upload limits, CORS origins, AI config status)")
    public ResponseEntity<SystemSettingsResponse> getSettings() {
        return ResponseEntity.ok(adminSettingsService.getSettings());
    }
}
