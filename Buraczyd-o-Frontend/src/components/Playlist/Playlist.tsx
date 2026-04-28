import { useState } from 'react';
import { Title, Stack, Text, Button, Group, ActionIcon } from '@mantine/core';
import {IconPlus, IconPlayerPlay, IconTrash} from '@tabler/icons-react';
import { AddSongModal } from './Modal/AddSongModal';
import {Song} from "../../types/Song";
import {PlaylistProps} from "../../types/Playlist";
import classes from './Playlist.module.css'; // Add this import

export function Playlist({ id,selected, songs, onAddSong, onSongSelect,onDeleteSong,onDeletePlaylist }: PlaylistProps) {
    const [modalOpened, setModalOpened] = useState(false);

    return (
        <>
            <AddSongModal
                opened={modalOpened}
                onClose={() => setModalOpened(false)}
                onAddSong={onAddSong}
                playlistId={id}
            />

            <Group justify="space-between" mb="md">
                <Title order={3}>{selected}</Title>
                <Button
                    onClick={() => setModalOpened(true)}
                    leftSection={<IconPlus size={16} />}
                    className={classes.addSongButton}
                    variant="filled"
                >
                    Dodaj piosenkę
                </Button>
                <ActionIcon
                    variant="light"
                    color="red"
                    size="lg"
                    onClick={() => onDeletePlaylist(id)}
                    title="Usuń playlistę"
                >
                    <IconTrash size={18} />
                </ActionIcon>
            </Group>

            <Stack>
                {songs.map((song, index) => (
                    <Group
                        key={song.songUrl}
                        justify="space-between"
                        className={classes.songItem}
                    >
                        <Text className={classes.songTitle}>{song.title}</Text>

                        <Group gap="xs" className={classes.songActions}>
                            <ActionIcon
                                variant="light"
                                size="lg"
                                onClick={() => onDeleteSong(song.songId)}
                                title="Usuń piosenkę"
                                className={classes.deleteIcon}
                            >
                                <IconTrash size={18} />
                            </ActionIcon>
                            <ActionIcon
                                variant="filled"
                                size="lg"
                                onClick={() => onSongSelect(song.songUrl, index)}
                                className={classes.playIcon}
                            >
                                <IconPlayerPlay size={18} />
                            </ActionIcon>
                        </Group>
                    </Group>
                ))}
            </Stack>
        </>
    );
}