const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export async function fetchSpotifyToken() {
    const response = await fetch(`${API_BASE_URL}/spotify/token`, {
        credentials: 'include',
    });

    if (!response.ok) {
        throw new Error('Failed to fetch Spotify token');
    }

    const data = await response.json();
    return data.access_token;
}
