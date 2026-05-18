import { useState } from 'react';
import { ScrollArea, Stack, Title, Text, Button } from '@mantine/core';
import { Playlist } from '../../types/Playlist';
import classes from './Sidebar.module.css';

interface SidebarProps {
    playlists: Playlist[];
    onSelect: (playlist: Playlist) => void;
    onAddPlaylist: (name: string) => Promise<Playlist>;
}

export function Sidebar({ playlists, onSelect, onAddPlaylist }: SidebarProps) {
    const [selectedId, setSelectedId] = useState<number | null>(null);

    const handleAddPlaylist = async () => {
        const name = prompt('Podaj nazwę nowej playlisty:');
        if (name) {
            try {
                await onAddPlaylist(name);
            } catch (error) {
                console.error('Błąd podczas tworzenia playlisty:', error);
            }
        }
    };

    const handleSelect = (playlist: Playlist) => {
        console.log(playlist.playlistId);
        console.log(playlist.songs);
        setSelectedId(playlist.playlistId);
        onSelect(playlist);
    };

    return (
        <aside className={classes.sidebar}>
            <Title order={4} className={classes.title}>Biblioteka</Title>
            <Button
                onClick={handleAddPlaylist}
                fullWidth
                size="xs"
                mb="sm"
                className={classes.addButton}
                variant="filled"
            >
                Dodaj playlistę
            </Button>
            <ScrollArea h="80vh">
                <Stack>
                    {playlists.map((playlist) => (
                        <Text
                            key={playlist.playlistId}
                            className={`${classes.item} ${selectedId === playlist.playlistId ? classes.activeItem : ''}`}
                            onClick={() => handleSelect(playlist)}
                        >
                            {playlist.name}
                        </Text>
                    ))}
                </Stack>
            </ScrollArea>
        </aside>
    );
}