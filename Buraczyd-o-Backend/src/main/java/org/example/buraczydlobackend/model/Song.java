package org.example.buraczydlobackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(name = "song_url")
    private String songUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Platform platform;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;
}