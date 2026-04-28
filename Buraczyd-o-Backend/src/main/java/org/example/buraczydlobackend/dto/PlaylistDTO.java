package org.example.buraczydlobackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private Long playlistId;

    @JsonIgnore
    private Long userId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    private List<SongDTO> songs;

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

}