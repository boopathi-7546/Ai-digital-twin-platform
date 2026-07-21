package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.auth.*;

/**
 * Core authentication use-cases: registration, login, token refresh,
 * email verification, and the forgot/reset password flow.
 */
public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void verifyEmail(EmailVerificationRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
