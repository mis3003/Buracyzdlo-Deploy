import {CreateSongRequest, Song} from '../types/Song';

const API_URL = "/api"; 




export async function createSong(song: CreateSongRequest): Promise<Song> {
    const response = await fetch(`${API_URL}/songs`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(song),
        credentials: 'include',
    });
console.log(JSON.stringify(song));
    if (!response.ok) {
        throw new Error('Nie udało się utworzyć piosenki');
    }

    return await response.json();
}

