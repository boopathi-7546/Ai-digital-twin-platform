package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.SystemSettingsResponse;
import com.digitaltwin.platform.service.AdminSettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminSettingsServiceImpl implements AdminSettingsService {

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${app.file-storage.max-resume-size-mb}")
    private int maxResumeSizeMb;

    @Value("${app.file-storage.upload-dir}")
    private String uploadDirectory;

    @Value("${app.cors.allowed-origins}")
    private String corsAllowedOrigins;

    @Value("${app.gemini.api-key:}")
    private String geminiApiKey;

    @Override
    public SystemSettingsResponse getSettings() {
        return SystemSettingsResponse.builder()
                .accessTokenExpirationMs(accessTokenExpirationMs)
                .refreshTokenExpirationMs(refreshTokenExpirationMs)
                .maxResumeSizeMb(maxResumeSizeMb)
                .uploadDirectory(uploadDirectory)
                .corsAllowedOrigins(corsAllowedOrigins)
                .geminiApiConfigured(geminiApiKey != null && !geminiApiKey.isBlank() ? "CONFIGURED" : "NOT_CONFIGURED")
                .build();
    }
}
