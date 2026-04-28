package org.example.buraczydlobackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.buraczydlobackend.dto.PlaylistDTO;
import org.example.buraczydlobackend.dto.SongDTO;
import org.example.buraczydlobackend.model.Playlist;
import org.example.buraczydlobackend.model.User;
import org.example.buraczydlobackend.repository.PlaylistRepository;
import org.example.buraczydlobackend.repository.SongRepository;
import org.example.buraczydlobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    UserService userService ;
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    public PlaylistService(UserService userService) {
        this.userService = userService;
    }

    public List<PlaylistDTO> getPlaylistsFromCurrentUser() {
        User currentUser = userService.getCurrentUser();

        List<Playlist> playlists = playlistRepository.findAllByUser(currentUser);

        return playlists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Autowired
    private SongRepository songRepository;

    public List<PlaylistDTO> getPlaylistsWithSongsFromCurrentUser() {
        User currentUser = userService.getCurrentUser();

        List<Playlist> playlists = playlistRepository.findAllByUser(currentUser);

        return playlists.stream()
                .map(playlist -> {
                    PlaylistDTO dto = convertToDTO(playlist);
                    List<SongDTO> songs = songRepository.findByPlaylist(playlist).stream()
                            .map(song -> {
                                SongDTO songDTO = new SongDTO();
                                songDTO.setSongId(song.getSongId());
                                songDTO.setTitle(song.getTitle());
                                songDTO.setPlaylistId(playlist.getPlaylistId());
                                songDTO.setSongUrl(song.getSongUrl());
                                songDTO.setPlatform(song.getPlatform());
                                return songDTO;
                            }).collect(Collectors.toList());
                    dto.setSongs(songs);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        User currentUser = userService.getCurrentUser(); // ⬅️ pobierz użytkownika z kontekstu

        if (playlistRepository.existsByUserAndName(currentUser, playlistDTO.getName())) {
            throw new IllegalArgumentException("Playlist with this name already exists for this user");
        }

        Playlist playlist = new Playlist();
        playlist.setUser(currentUser); // ⬅️ przypisz bieżącego użytkownika
        playlist.setName(playlistDTO.getName());

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }


    public PlaylistDTO updatePlaylist(Long id, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + id));

        User user = userRepository.findById(playlistDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + playlistDTO.getUserId()));

        // Check if name is being changed and if it already exists for this user
        if (!playlist.getName().equals(playlistDTO.getName()) && 
            playlistRepository.existsByUserAndName(user, playlistDTO.getName())) {
            throw new IllegalArgumentException("Playlist with this name already exists for this user");
        }

        playlist.setUser(user);
        playlist.setName(playlistDTO.getName());

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(updatedPlaylist);
    }

    public void deletePlaylist(Long id) {
        if (!playlistRepository.existsById(id)) {
            throw new EntityNotFoundException("Playlist not found with id: " + id);
        }
        playlistRepository.deleteById(id);
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setPlaylistId(playlist.getPlaylistId());
        playlistDTO.setUserId(playlist.getUser().getId());
        playlistDTO.setName(playlist.getName());
        return playlistDTO;
    }
}