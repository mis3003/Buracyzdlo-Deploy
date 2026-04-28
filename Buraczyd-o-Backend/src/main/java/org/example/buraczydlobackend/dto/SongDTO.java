package org.example.buraczydlobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.buraczydlobackend.model.Platform;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long songId;

    private String title;

    @NotBlank(message = "Song URL is required")
    @Size(max = 255, message = "Song URL cannot exceed 255 characters")
    private String songUrl;
    
    @NotNull(message = "Platform is required")
    private Platform platform;
    
    @NotNull(message = "Playlist ID is required")
    private Long playlistId;
}