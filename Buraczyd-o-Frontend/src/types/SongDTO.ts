export interface Song {
    id?: number;
    name: string;
    source: 'youtube' | 'spotify';
    url: string;
    playlistId: number;
}
