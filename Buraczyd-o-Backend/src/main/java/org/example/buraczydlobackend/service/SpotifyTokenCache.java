package org.example.buraczydlobackend.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpotifyTokenCache {

    private final Map<String, CachedToken> tokenCache = new ConcurrentHashMap<>();
    private static final Duration TOKEN_TTL = Duration.ofHours(1);


    public void store(String username, String accessToken) {
        Instant expirationTime = Instant.now().plus(TOKEN_TTL);
        tokenCache.put(username, new CachedToken(accessToken, expirationTime));
    }


    public boolean isValid(String username) {
        CachedToken cachedToken = tokenCache.get(username);
        return cachedToken != null && Instant.now().isBefore(cachedToken.expiresAt());
    }


    public Optional<String> getValidToken(String username) {
        CachedToken cachedToken = tokenCache.get(username);
        if (cachedToken == null) {
            return Optional.empty();
        }

        if (Instant.now().isBefore(cachedToken.expiresAt())) {
            return Optional.of(cachedToken.token());
        }

        tokenCache.remove(username); // remove expired
        return Optional.empty();
    }


    public Optional<String> getToken(String username) {
        CachedToken cachedToken = tokenCache.get(username);
        return cachedToken != null ? Optional.of(cachedToken.token()) : Optional.empty();
    }


    public void invalidate(String username) {
        tokenCache.remove(username);
    }

    private record CachedToken(String token, Instant expiresAt) {}
}
