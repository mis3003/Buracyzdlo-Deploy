// services/playlistService.ts
import { Playlist, CreatePlaylistRequest, UpdatePlaylistRequest } from '../types/Playlist';

const API_URL = "/api"; 


if (!API_URL) {
    throw new Error('REACT_APP_API_BASE_URL nie jest ustawione w .env');
}
export async function getUserPlaylists(): Promise<Playlist[]> {
    try {
        const response = await fetch(`${API_URL}/playlists/me`, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        const data = await response.json();
        console.log('Response JSON:', data);
        return data;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Błąd połączenia z serwerem');
        }
        throw error;
    }
}


export async function getUserPlaylistsWithSongs(): Promise<Playlist[]> {
    try {
        const response = await fetch(`${API_URL}/playlists/me/full`, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        // Make sure the response matches our Playlist interface
        const data: Playlist[] = await response.json();
        return data;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Błąd połączenia z serwerem');
        }
        throw error;
    }
}

export async function createPlaylist(playlist: CreatePlaylistRequest): Promise<Playlist> {
    try {
        const response = await fetch(`${API_URL}/playlists`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(playlist),
            credentials: 'include',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        return await response.json();
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Błąd połączenia z serwerem');
        }
        throw error;
    }
}

export async function updatePlaylist(id: number, playlist: UpdatePlaylistRequest): Promise<Playlist> {
    try {
        const response = await fetch(`${API_URL}/playlists/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(playlist),
            credentials: 'include',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        return await response.json();
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Błąd połączenia z serwerem');
        }
        throw error;
    }
}

export async function deletePlaylist(id: number): Promise<string> {
    try {
        const response = await fetch(`${API_URL}/playlists/${id}`, {
            method: 'DELETE',
            credentials: 'include',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        const data = await response.json();
        return data.message;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Błąd połączenia z serwerem');
        }
        throw error;
    }
}