package com.digitaltwin.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Dedicated bean definition for the password encoder used across the
 * application (auth registration/login, admin-created accounts, etc.).
 * Kept separate from SecurityConfig to avoid coupling the filter chain
 * definition to the hashing strategy, and to make it trivial to swap
 * BCrypt strength or algorithm in one place.
 */
@Configuration
public class PasswordEncoderConfig {

    private static final int BCRYPT_STRENGTH = 10;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }
}
