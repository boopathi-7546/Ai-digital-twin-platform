package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.auth.*;
import com.digitaltwin.platform.entity.Role;
import com.digitaltwin.platform.entity.User;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.exception.UnauthorizedException;
import com.digitaltwin.platform.repository.RoleRepository;
import com.digitaltwin.platform.repository.UserRepository;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.security.JwtTokenProvider;
import com.digitaltwin.platform.service.AuthService;
import com.digitaltwin.platform.service.EmailService;
import com.digitaltwin.platform.util.AppConstants;
import com.digitaltwin.platform.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements the full authentication lifecycle: register -> verify
 * email -> login -> refresh -> forgot/reset password. Business rules
 * (token expiry windows, duplicate email checks, etc.) all live here
 * rather than in the controller.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists.");
        }

        Role studentRole = roleRepository.findByName(Role.STUDENT)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_STUDENT is not seeded in the database. Run seed_data.sql / V1 migration first."));

        String verificationToken = TokenGenerator.generate();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationExpiry(LocalDateTime.now().plusHours(AppConstants.EMAIL_VERIFICATION_TOKEN_VALID_HOURS))
                .active(true)
                .roles(Set.of(studentRole))
                .build();

        userRepository.save(user);
        log.info("New student registered: {}", user.getEmail());

        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), verificationToken);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", principal.getId()));

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("Please verify your email address before logging in.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        return LoginResponse.of(user.getId(), user.getFullName(), user.getEmail(), roles, accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isRefreshToken(token)) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User associated with this token no longer exists."));

        if (!user.isActive()) {
            throw new UnauthorizedException("This account has been deactivated.");
        }

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        return LoginResponse.of(user.getId(), user.getFullName(), user.getEmail(), roles, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void verifyEmail(EmailVerificationRequest request) {
        User user = userRepository.findValidVerificationToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("This verification link is invalid or has expired."));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail().toLowerCase()).ifPresent(user -> {
            String resetToken = TokenGenerator.generate();
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordExpiry(LocalDateTime.now().plusMinutes(AppConstants.RESET_PASSWORD_TOKEN_VALID_MINUTES));
            userRepository.save(user);

            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetToken);
        });
        // Intentionally always returns silently (no BadRequestException) even if the
        // email doesn't exist, to avoid leaking which emails are registered.
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findValidResetToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("This password reset link is invalid or has expired."));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        userRepository.save(user);

        log.info("Password reset completed for user: {}", user.getEmail());
    }
}
