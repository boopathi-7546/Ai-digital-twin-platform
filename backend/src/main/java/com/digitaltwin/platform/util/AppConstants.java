package com.digitaltwin.platform.util;

/**
 * Shared, cross-cutting constants used across services/controllers.
 * Keeping these in one place avoids magic strings scattered through
 * the codebase.
 */
public final class AppConstants {

    private AppConstants() {
        // utility class
    }

    // Token expiry windows
    public static final long EMAIL_VERIFICATION_TOKEN_VALID_HOURS = 24;
    public static final long RESET_PASSWORD_TOKEN_VALID_MINUTES = 30;

    // Pagination defaults
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Notification types
    public static final String NOTIFICATION_INFO = "INFO";
    public static final String NOTIFICATION_WARNING = "WARNING";
    public static final String NOTIFICATION_SUCCESS = "SUCCESS";
    public static final String NOTIFICATION_ALERT = "ALERT";

    // File storage
    public static final String RESUME_SUBDIRECTORY = "resumes";
    public static final String PROFILE_PICTURE_SUBDIRECTORY = "profile-pictures";
    public static final String REPORT_SUBDIRECTORY = "reports";

    // Misc
    public static final String SYSTEM_USER = "SYSTEM";
}
