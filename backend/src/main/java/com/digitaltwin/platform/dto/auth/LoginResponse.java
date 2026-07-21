package com.digitaltwin.platform.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response for POST /api/auth/login and /api/auth/refresh-token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;
    private String fullName;
    private String email;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public static LoginResponse of(Long userId, String fullName, String email, List<String> roles,
                                    String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .userId(userId)
                .fullName(fullName)
                .email(email)
                .roles(roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}
