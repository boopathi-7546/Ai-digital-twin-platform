package com.digitaltwin.platform.service;

/**
 * Handles all transactional email sending for the platform.
 * Implementation is async so auth endpoints don't block on SMTP latency.
 */
public interface EmailService {

    void sendVerificationEmail(String toEmail, String fullName, String verificationToken);

    void sendPasswordResetEmail(String toEmail, String fullName, String resetToken);

    void sendWelcomeEmail(String toEmail, String fullName);
}
