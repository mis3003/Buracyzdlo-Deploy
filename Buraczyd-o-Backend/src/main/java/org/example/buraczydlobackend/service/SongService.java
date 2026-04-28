package org.example.buraczydlobackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.buraczydlobackend.dto.SongDTO;
import org.example.buraczydlobackend.model.Playlist;
import org.example.buraczydlobackend.model.Song;
import org.example.buraczydlobackend.repository.PlaylistRepository;
import org.example.buraczydlobackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getSongsByPlaylistId(Long playlistId) {
        // Check if playlist exists before proceeding
        if (!playlistRepository.existsById(playlistId)) {
            throw new EntityNotFoundException("Playlist not found with id: " + playlistId);
        }

        Playlist playlist = playlistRepository.findById(playlistId).get();

        return songRepository.findByPlaylist(playlist).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SongDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + id));
        return convertToDTO(song);
    }

    public SongDTO createSong(SongDTO songDTO) {
        // Check if playlist exists before proceeding
        if (!playlistRepository.existsById(songDTO.getPlaylistId())) {
            throw new EntityNotFoundException("Playlist not found with id: " + songDTO.getPlaylistId());
        }

        Playlist playlist = playlistRepository.findById(songDTO.getPlaylistId()).get();

        if (songRepository.existsByPlaylistAndSongUrl(playlist, songDTO.getSongUrl())) {
            throw new IllegalArgumentException("Song with this URL already exists in this playlist");
        }

        Song song = new Song();
        song.setTitle(songDTO.getTitle());
        song.setPlaylist(playlist);
        song.setSongUrl(songDTO.getSongUrl());
        song.setPlatform(songDTO.getPlatform());

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }

    public SongDTO updateSong(Long id, SongDTO songDTO) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + id));

        // Check if playlist exists before proceeding
        if (!playlistRepository.existsById(songDTO.getPlaylistId())) {
            throw new EntityNotFoundException("Playlist not found with id: " + songDTO.getPlaylistId());
        }

        Playlist playlist = playlistRepository.findById(songDTO.getPlaylistId()).get();

        // Check if URL is being changed and if it already exists in this playlist
        if (!song.getSongUrl().equals(songDTO.getSongUrl()) && 
            songRepository.existsByPlaylistAndSongUrl(playlist, songDTO.getSongUrl())) {
            throw new IllegalArgumentException("Song with this URL already exists in this playlist");
        }

        song.setPlaylist(playlist);
        song.setSongUrl(songDTO.getSongUrl());
        song.setPlatform(songDTO.getPlatform());
        song.setTitle(songDTO.getTitle());

        Song updatedSong = songRepository.save(song);
        return convertToDTO(updatedSong);
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new EntityNotFoundException("Song not found with id: " + id);
        }
        songRepository.deleteById(id);
    }

    private SongDTO convertToDTO(Song song) {
        SongDTO songDTO = new SongDTO();
        songDTO.setSongId(song.getSongId());
        songDTO.setTitle(song.getTitle());

        songDTO.setPlaylistId(song.getPlaylist().getPlaylistId());
        songDTO.setSongUrl(song.getSongUrl());
        songDTO.setPlatform(song.getPlatform());
        return songDTO;
    }
}
