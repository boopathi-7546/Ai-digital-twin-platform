package com.digitaltwin.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Read-only snapshot of key platform configuration for the admin
 * settings screen. These values come from application.yml / env vars
 * rather than a database table — changing them requires a redeploy,
 * which is intentional for security-sensitive settings like JWT expiry.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettingsResponse {

    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;
    private int maxResumeSizeMb;
    private String uploadDirectory;
    private String corsAllowedOrigins;
    private String geminiApiConfigured;
}
