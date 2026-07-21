package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends verification, password-reset, and welcome emails via SMTP
 * (configured through spring.mail.* properties). Runs asynchronously
 * so a slow mail server never delays the HTTP response to the client.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.frontend.verify-email-path}")
    private String verifyEmailPath;

    @Value("${app.frontend.reset-password-path}")
    private String resetPasswordPath;

    @Value("${spring.mail.username:no-reply@digitaltwin.io}")
    private String fromAddress;

    @Override
    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) {
        String link = frontendBaseUrl + verifyEmailPath + "?token=" + verificationToken;
        String body = "Hi " + fullName + ",\n\n"
                + "Welcome to the AI-Powered Digital Twin & Interview Readiness Platform.\n"
                + "Please verify your email address by clicking the link below:\n\n"
                + link + "\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "If you did not create this account, you can safely ignore this email.";

        sendPlainTextEmail(toEmail, "Verify your email address", body);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        String link = frontendBaseUrl + resetPasswordPath + "?token=" + resetToken;
        String body = "Hi " + fullName + ",\n\n"
                + "We received a request to reset your password. Click the link below to choose a new one:\n\n"
                + link + "\n\n"
                + "This link will expire in 30 minutes. If you did not request this, please ignore this email "
                + "and your password will remain unchanged.";

        sendPlainTextEmail(toEmail, "Reset your password", body);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        String body = "Hi " + fullName + ",\n\n"
                + "Your email has been verified and your account is now active. "
                + "Log in to build your profile, upload your resume, and start your first AI mock interview.\n\n"
                + "Good luck!";

        sendPlainTextEmail(toEmail, "Welcome aboard!", body);
    }

    private void sendPlainTextEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            // Email failures should never break the calling flow (e.g. registration
            // should still succeed even if SMTP is temporarily down).
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}
