package org.example.buraczydlobackend.controller;

import jakarta.validation.Valid;
import org.example.buraczydlobackend.dto.MessageResponse;
import org.example.buraczydlobackend.dto.SongDTO;
import org.example.buraczydlobackend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200","http://127.0.0.1:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;



    @GetMapping("/playlist/{playlistId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SongDTO>> getSongsByPlaylistId(@PathVariable Long playlistId) {
        List<SongDTO> songs = songService.getSongsByPlaylistId(playlistId);
        return ResponseEntity.ok(songs);
    }



    @PostMapping
      public ResponseEntity<SongDTO> createSong(@Valid @RequestBody SongDTO songDTO) {
        SongDTO createdSong = songService.createSong(songDTO);
        return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @Valid @RequestBody SongDTO songDTO) {
        SongDTO updatedSong = songService.updateSong(id, songDTO);
        return ResponseEntity.ok(updatedSong);
    }

    @DeleteMapping("/{id}")
       public ResponseEntity<MessageResponse> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok(new MessageResponse("Song deleted successfully"));
    }
}
