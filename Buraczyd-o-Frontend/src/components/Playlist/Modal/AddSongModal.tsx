import { useState } from 'react';
import { Modal, TextInput, Button, ButtonGroup } from '@mantine/core';
import { fetchYoutubeTitle } from './YoutubeHandler';
import { fetchSpotifyTitle } from "./SpotifyHandler";
import { fetchSpotifyToken } from "../../../services/spotifyService";
import { Song,CreateSongRequest } from "../../../types/Song";
import {createSong} from "../../../services/songService";


interface AddSongModalProps {
  opened: boolean;
  onClose: () => void;
  onAddSong: (song: Song) => void; // Use CreateSongRequest instead
  playlistId: number;
}

export function AddSongModal({ opened, onClose, onAddSong, playlistId }: AddSongModalProps) {

  const [songURL, setSongUrl] = useState('');
  const [songName, setSongName] = useState('');
  const [platform, setPlatform] = useState<'YouTube' | 'Spotify'>('YouTube');
  const [loading, setLoading] = useState(false);

  const handleAdd = async () => {
    if (!songURL.trim()) {
      alert('Podaj link do piosenki');
      return;
    }

    setLoading(true);
    let title = songName.trim();

    try {
      if (!title) {
        if (platform === 'YouTube') {
          title = await fetchYoutubeTitle(songURL);
        } else if (platform === 'Spotify') {
          const spotifyAccessToken = await fetchSpotifyToken();
          if (spotifyAccessToken) {
            title = (await fetchSpotifyTitle(songURL, spotifyAccessToken)) ?? "";
          }
        }
      }

      if (!title.trim()) {
        alert('Nie udało się pobrać nazwy piosenki');
        return;
      }

      const newSong: CreateSongRequest = {
        title: title,
        platform: platform,
        songUrl: songURL,
        playlistId: playlistId,
      };

      const savedSong = await createSong(newSong); // <-- wywołanie API
      console.log('savedSong', savedSong);
      onAddSong(savedSong); // <-- przekazanie zapisanego obiektu do rodzica

      // Reset
      setSongName('');
      setSongUrl('');
      setPlatform('YouTube');
      onClose();

    } catch (error) {
      console.error('Error adding song:', error);
      alert('Błąd podczas dodawania piosenki');
    } finally {
      setLoading(false);
    }
  };


  return (
      <Modal opened={opened} onClose={onClose} title="Dodaj piosenkę">
        <ButtonGroup mb="md">
          <Button
              variant={platform === 'YouTube' ? 'filled' : 'outline'}
              onClick={() => setPlatform('YouTube')}
              disabled={loading}
          >
            Youtube
          </Button>
          <Button
              variant={platform === 'Spotify' ? 'filled' : 'outline'}
              onClick={() => setPlatform('Spotify')}
              disabled={loading}
          >
            Spotify
          </Button>
        </ButtonGroup>

        <TextInput
            label="Link do piosenki"
            placeholder="Wklej link do piosenki"
            value={songURL}
            onChange={(event) => setSongUrl(event.currentTarget.value)}
            mb="md"
            disabled={loading}
            required
        />

        <TextInput
            label="Nazwa piosenki (opcjonalne)"
            placeholder="Zostaw puste aby pobrać automatycznie"
            value={songName}
            onChange={(event) => setSongName(event.currentTarget.value)}
            mb="md"
            disabled={loading}
        />

        <Button
            fullWidth
            onClick={handleAdd}
            loading={loading}
            disabled={!songURL.trim()}
        >
          Dodaj
        </Button>
      </Modal>
  );
}