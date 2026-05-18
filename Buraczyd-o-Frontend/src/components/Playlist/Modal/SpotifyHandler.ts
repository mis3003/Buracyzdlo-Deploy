/**
 * Pobiera tytuł piosenki ze Spotify na podstawie linku do utworu, używając wbudowanego Fetch API.
 * Wymaga uwierzytelnienia za pomocą tokena dostępu Spotify API.
 *
 * @param spotifyUrl Link do utworu Spotify (np. "https://open.spotify.com/track/123xyz...")
 * @param accessToken Twój token dostępu do Spotify Web API. Możesz go uzyskać np. poprzez proces autoryzacji OAuth 2.0.
 * @returns Promise<string | null> Zwraca tytuł piosenki lub null, jeśli wystąpi błąd.
 */
export async function fetchSpotifyTitle(spotifyUrl: string, accessToken: string): Promise<string | null> {
    try {
        const trackIdMatch = spotifyUrl.match(/\/track\/([a-zA-Z0-9]+)/);

        if (!trackIdMatch || !trackIdMatch[1]) {
            console.error('Nieprawidłowy link Spotify. Nie można znaleźć ID utworu.');
            return null;
        }

        const trackId = trackIdMatch[1];
        const apiUrl = `https://api.spotify.com/v1/tracks/${trackId}`; // Upewnij się, że URL jest poprawny!

        const response = await fetch(apiUrl, {
            headers: {
                'Authorization': `Bearer ${accessToken}`,
            },
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error(`Błąd HTTP ${response.status}: ${errorData.error?.message || response.statusText}`);
            return null;
        }

        const data = await response.json();

        // Sprawdzamy, czy dane utworu i nazwa utworu istnieją
        if (data && data.name) {
            let artistName: string | null = null;

            // Sprawdzamy, czy istnieje tablica artystów i czy nie jest pusta
            if (data.artists && Array.isArray(data.artists) && data.artists.length > 0) {
                // Pobieramy nazwę pierwszego artysty (głównego artysty)
                artistName = data.artists[0].name;

                // Jeśli chcesz wszystkich artystów oddzielonych przecinkami:
                // artistName = data.artists.map((artist: any) => artist.name).join(', ');
            }

            if (artistName) {
                return `${artistName} - ${data.name}`; // Zwracamy "Artysta - Tytuł"
            } else {
                return data.name; // Jeśli nie ma artysty, zwracamy tylko tytuł
            }
        } else {
            console.warn('Nie znaleziono tytułu dla podanego utworu.');
            return null;
        }
    } catch (error) {
        console.error('Wystąpił nieoczekiwany błąd podczas pobierania danych:', error);
        return null;
    }
}