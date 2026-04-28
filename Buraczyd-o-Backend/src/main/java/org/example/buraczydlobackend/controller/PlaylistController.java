package org.example.buraczydlobackend.controller;

import jakarta.validation.Valid;
import org.example.buraczydlobackend.dto.MessageResponse;
import org.example.buraczydlobackend.dto.PlaylistDTO;
import org.example.buraczydlobackend.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200","http://127.0.0.1:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;


    @GetMapping("/me")
    public List<PlaylistDTO> getCurrentUserPlaylists() {
        return playlistService.getPlaylistsFromCurrentUser();
    }

    @GetMapping("/me/full")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<PlaylistDTO>> getCurrentUserPlaylistsWithSongs() {
        List<PlaylistDTO> playlists = playlistService.getPlaylistsWithSongsFromCurrentUser();
        return ResponseEntity.ok(playlists);
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO createdPlaylist = playlistService.createPlaylist(playlistDTO);
        return new ResponseEntity<>(createdPlaylist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
     public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Long id, @Valid @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO updatedPlaylist = playlistService.updatePlaylist(id, playlistDTO);
        return ResponseEntity.ok(updatedPlaylist);
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<MessageResponse> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok(new MessageResponse("Playlist deleted successfully"));
    }
}
