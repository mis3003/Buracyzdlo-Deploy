package org.example.buraczydlobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Long id;
    private String login;
    private String email;
    private Set<String> roles;
    private String spotifyRefreshToken;

    public AuthResponse(Long id, String login, String email, Set<String> roles) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.roles = roles;
    }
}
