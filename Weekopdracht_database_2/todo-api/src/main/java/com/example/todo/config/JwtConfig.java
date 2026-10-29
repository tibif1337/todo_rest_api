package com.example.todo.config;

public final class JwtConfig {

    private static final String ENV_NAME = "JWT_SECRET";

    private JwtConfig() {
    }

    public static String getSecret() {
        String secret = System.getenv(ENV_NAME);

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable is not set."
            );
        }

        if (secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must contain at least 32 bytes."
            );
        }

        return secret;
    }

    public static long getExpirationMilliseconds() {
        return 60 * 60 * 1000L;
    }
}