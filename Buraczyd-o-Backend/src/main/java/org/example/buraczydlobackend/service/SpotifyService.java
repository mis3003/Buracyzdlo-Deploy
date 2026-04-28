package org.example.buraczydlobackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.buraczydlobackend.config.SpotifyConfig;
import org.example.buraczydlobackend.dto.TokenResponse;
import org.example.buraczydlobackend.model.User;
import org.example.buraczydlobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;

@Service
public class SpotifyService {

    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize";
    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1";
    private static final String SCOPE = "user-read-private user-read-email playlist-read-private playlist-modify-private playlist-modify-public";

    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private SpotifyTokenCache spotifyTokenCache;





    /**
     * Generates the Spotify authorization URL
     * @return URI for Spotify authorization
     */
    public URI getAuthorizationCodeUri() {
        return UriComponentsBuilder.fromHttpUrl(SPOTIFY_AUTH_URL)
                .queryParam("client_id", spotifyConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", spotifyConfig.getRedirectUri())
                .queryParam("scope", SCOPE)
                .queryParam("show_dialog", true)
                .build()
                .toUri();
    }

    /**
     * Exchanges the authorization code for an access token
     * @param code The authorization code returned by Spotify
     * @return TokenResponse containing access token and refresh token
     */
    public TokenResponse getAccessToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Set Basic Auth header with client_id:client_secret
            String auth = spotifyConfig.getClientId() + ":" + spotifyConfig.getClientSecret();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            headers.set("Authorization", "Basic " + new String(encodedAuth));

            // Set form parameters
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "authorization_code");
            map.add("code", code);
            map.add("redirect_uri", spotifyConfig.getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    SPOTIFY_TOKEN_URL, 
                    request, 
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String accessToken = root.path("access_token").asText();
                String refreshToken = root.path("refresh_token").asText();
                String tokenType = root.path("token_type").asText();
                int expiresIn = root.path("expires_in").asInt();
                String scope = root.path("scope").asText();

                // Create TokenResponse object
                org.example.buraczydlobackend.dto.TokenResponse tokenResponse = 
                    new org.example.buraczydlobackend.dto.TokenResponse(
                        accessToken, refreshToken, tokenType, expiresIn, scope);

                // Save token to current user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String currentUsername = authentication.getName();
                    Optional<User> userOptional = userRepository.findByLogin(currentUsername);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        user.setSpotifyRefreshToken(refreshToken);
                        userRepository.save(user);
                    }
                }

                return tokenResponse;
            } else {
                throw new RuntimeException("Failed to get access token from Spotify");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting Spotify access token", e);
        }
    }

    /**
     * Refreshes the Spotify access token
     * @param refreshToken The refresh token
     * @return The new access token
     */
    public String refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Set Basic Auth header with client_id:client_secret
            String auth = spotifyConfig.getClientId() + ":" + spotifyConfig.getClientSecret();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            headers.set("Authorization", "Basic " + new String(encodedAuth));

            // Set form parameters
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "refresh_token");
            map.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    SPOTIFY_TOKEN_URL, 
                    request, 
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String accessToken = root.path("access_token").asText();

                // Update token for current user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String currentUsername = authentication.getName();
                    Optional<User> userOptional = userRepository.findByLogin(currentUsername);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
//                        user.setSpotifyRefreshToken(accessToken);
//                        userRepository.save(user);
                    }
                }

                return accessToken;
            } else {
                throw new RuntimeException("Failed to refresh access token from Spotify");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing Spotify access token", e);
        }
    }

    /**
     * Gets the current user's Spotify profile
     * @param accessToken The Spotify access token
     * @return The user's profile as a JsonNode
     */
    public JsonNode getUserProfile(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    SPOTIFY_API_URL + "/me", 
                    HttpMethod.GET, 
                    entity, 
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readTree(response.getBody());
            } else {
                throw new RuntimeException("Failed to get user profile from Spotify");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting Spotify user profile", e);
        }
    }

    public Optional<String> getCachedOrRefreshedToken(String username) {

        Optional<String> cached = spotifyTokenCache.getValidToken(username);
        if (cached.isPresent()) {
            return cached;
        }


        Optional<User> userOpt = userRepository.findByLogin(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String refreshToken = user.getSpotifyRefreshToken();
            System.out.println("Refresh token: " + refreshToken);
            if (refreshToken != null) {
                String newAccessToken = this.refreshToken(refreshToken);
                if (newAccessToken != null) {
                    spotifyTokenCache.store(username, newAccessToken); // 3. Zapisz w cache
                    return Optional.of(newAccessToken);
                }
            }
        }

        // 4. Nie udało się
        return Optional.empty();
    }



}
