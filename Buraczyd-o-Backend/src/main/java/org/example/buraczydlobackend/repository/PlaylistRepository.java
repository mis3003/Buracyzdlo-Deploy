package org.example.buraczydlobackend.repository;

import org.example.buraczydlobackend.model.Playlist;
import org.example.buraczydlobackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUser(User user);
    List<Playlist> findAllByUser(User user);

    List<Playlist> findByUserAndNameContaining(User user, String name);
    boolean existsByUserAndName(User user, String name);
}