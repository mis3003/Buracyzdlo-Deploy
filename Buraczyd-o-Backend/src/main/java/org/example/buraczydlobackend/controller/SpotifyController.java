package org.example.buraczydlobackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.example.buraczydlobackend.dto.MessageResponse;
import org.example.buraczydlobackend.dto.TokenResponse;
import org.example.buraczydlobackend.model.User;
import org.example.buraczydlobackend.repository.UserRepository;
import org.example.buraczydlobackend.service.SpotifyService;
import org.example.buraczydlobackend.service.SpotifyTokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200","http://127.0.0.1:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private UserRepository userRepository;


    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    /**
     * Initiates the Spotify authorization flow
     * @return The Spotify authorization URL
     */
    @GetMapping("/authorize")
    public ResponseEntity<String> authorize(HttpSession session) {
        String authUrl = "https://accounts.spotify.com/authorize?response_type=code&client_id="
                + clientId + "&redirect_uri=" + redirectUri + "&scope=streaming user-read-email user-read-private user-read-playback-state user-modify-playback-state"
                ;

        return ResponseEntity.ok(authUrl);
    }
    /**
     * Callback endpoint for Spotify OAuth
     * @param code The authorization code returned by Spotify
     * @return Redirects to frontend after successful authorization
     */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpSession session) {
        System.out.println("Access code: " + code);
        System.out.println("AAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n sdffffffffasfhlkahsfklgasjkgalskh");
        // 1) Exchange code for token
        TokenResponse tr = spotifyService.getAccessToken(code);
        System.out.println("Access Token: " + tr.getAccessToken());
        System.out.println("Refresh Token: " + tr.getRefreshToken());

        // 2) Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            System.out.println("Brak uwierzytelnionego użytkownika");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak zalogowanego użytkownika");
        }
        String currentUsername = authentication.getName();
        System.out.println("Zalogowany użytkownik: " + currentUsername);

        // 3) Save the Spotify refresh token to the user
        Optional<User> userOptional = userRepository.findByLogin(currentUsername);
        if (userOptional.isPresent()) {
            try {
                User user = userOptional.get();
                if (tr.getRefreshToken() != null) {
                    user.setSpotifyRefreshToken(tr.getRefreshToken());
                    userRepository.save(user);
                    System.out.println("Zapisano token do bazy danych");
                } else {
                    System.out.println("Otrzymany token jest pusty!");
                }
            } catch (Exception e) {
                System.out.println("Błąd zapisu tokena: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd zapisu tokena");
            }
        } else {
            System.out.println("Nie znaleziono użytkownika w bazie danych");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik nie istnieje");
        }

        // 4) Save tokens in session (opcjonalnie)
        session.setAttribute("access_token", tr.getAccessToken());
        session.setAttribute("refresh_token", tr.getRefreshToken());

        return ResponseEntity.ok("Spotify autoryzacja zakończona sukcesem");
    }

    /**
     * Checks if the current user has a valid Spotify token
     * @return Status message
     */
    @GetMapping("/status")
    public ResponseEntity<MessageResponse> status() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            Optional<User> userOptional = userRepository.findByLogin(currentUsername);

            if (userOptional.isPresent() && userOptional.get().getSpotifyRefreshToken() != null) {
                return ResponseEntity.ok(new MessageResponse("User has Spotify authorization"));
            }
        }
        return ResponseEntity.ok(new MessageResponse("User does not have Spotify authorization"));
    }


    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            Optional<User> userOptional = userRepository.findByLogin(currentUsername);

            if (userOptional.isPresent()) {
                Optional<String> accessTokenOpt = spotifyService.getCachedOrRefreshedToken(currentUsername);

                if (accessTokenOpt.isPresent()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("access_token", accessTokenOpt.get());
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to retrieve access token"));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User not authenticated or no refresh token available"));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            Optional<User> userOptional = userRepository.findByLogin(currentUsername);

            if (userOptional.isPresent() && userOptional.get().getSpotifyRefreshToken() != null) {
                String accessToken = userOptional.get().getSpotifyRefreshToken();
                try {
                    JsonNode profile = spotifyService.getUserProfile(accessToken);
                    System.out.println("Działa eeee");
                    return ResponseEntity.ok(profile);
                } catch (Exception e) {
                    // Token might be expired, try to refresh it
                    return ResponseEntity.status(401).body(new MessageResponse("Spotify token expired or invalid"));
                }
            }
        }
        return ResponseEntity.status(401).body(new MessageResponse("User not authenticated or no Spotify token"));
    }



}
