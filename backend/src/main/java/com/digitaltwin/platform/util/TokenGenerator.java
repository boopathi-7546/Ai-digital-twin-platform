package com.digitaltwin.platform.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically-random, URL-safe tokens for email
 * verification links and password reset links (distinct from JWTs,
 * which carry auth session claims).
 */
public final class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEFAULT_BYTE_LENGTH = 32;

    private TokenGenerator() {
        // utility class
    }

    public static String generate() {
        return generate(DEFAULT_BYTE_LENGTH);
    }

    public static String generate(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
