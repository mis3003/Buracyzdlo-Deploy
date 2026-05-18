import {CreateSongRequest, Song} from "./Song";

// types/Playlist.ts

export type Playlist = {
    playlistId: number;
    name: string;
    songs?: Song[];
};

export interface PlaylistProps {     // ← Component props
    id: number;
    selected: string;
    songs: Song[];
    onAddSong: (song: Song) => void;
    onSongSelect: (url: string, index: number) => void;
    onDeleteSong: (songID: number) => void;
    onDeletePlaylist: (playlistId: number) => void;
}
export interface CreatePlaylistRequest {
    name: string;
    description?: string;
}

export interface UpdatePlaylistRequest {
    name?: string;
    description?: string;
}