export interface Song {
    songId: number;
    title: string;
    songUrl: string;
    platform: 'YouTube' | 'Spotify';
    playlistId?: number; // Make this optional for creation

}

export interface CreateSongRequest {
    title: string;
    songUrl: string;
    platform: 'YouTube' | 'Spotify';
    playlistId: number;
}