package com.yuva.TaskManager.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginCodeService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final Map<String, PendingLogin> pendingLogins =
            new ConcurrentHashMap<>();

    private static final long CODE_LIFETIME_SECONDS = 60;

    private record PendingLogin(
            String accessToken,
            Instant expiresAt
    ) {}

    public String createCode(String accessToken) {

        Instant now = Instant.now();

        // Remove expired codes.
        pendingLogins.entrySet().removeIf(
                entry -> !entry.getValue().expiresAt().isAfter(now)
        );

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String code = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        pendingLogins.put(
                code,
                new PendingLogin(
                        accessToken,
                        now.plusSeconds(CODE_LIFETIME_SECONDS)
                )
        );

        return code;
    }

    public String consumeCode(String code) {

        if (code == null || code.isBlank()) {
            return null;
        }

        // remove() ensures the code can be consumed only once.
        PendingLogin pendingLogin = pendingLogins.remove(code);

        if (pendingLogin == null) {
            return null;
        }

        if (!pendingLogin.expiresAt().isAfter(Instant.now())) {
            return null;
        }

        return pendingLogin.accessToken();
    }
}