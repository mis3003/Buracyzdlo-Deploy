package org.example.buraczydlobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @NotBlank(message = "Login is required")
    @Size(max = 255, message = "Login cannot exceed 255 characters")
    private String login;

    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Email(message = "Email should be valid")
    private String email;

    // Password is not included in the response DTO for security reasons
    // But we need it for create/update operations
    @Size(max = 255, message = "Password cannot exceed 255 characters")
    private String password;

    @Size(max = 1000, message = "Spotify refresh token cannot exceed 1000 characters")
    private String spotifyRefreshToken;
}
