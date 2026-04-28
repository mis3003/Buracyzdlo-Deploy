package org.example.buraczydlobackend.repository;

import org.example.buraczydlobackend.model.Playlist;
import org.example.buraczydlobackend.model.Platform;
import org.example.buraczydlobackend.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByPlaylist(Playlist playlist);
    List<Song> findByPlaylistAndPlatform(Playlist playlist, Platform platform);
    List<Song> findBySongUrlContaining(String songUrl);
    boolean existsByPlaylistAndSongUrl(Playlist playlist, String songUrl);
}