import { useState } from 'react';
import { TextInput, Button, Container, Group } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useNavigate, useLocation } from 'react-router-dom';

import classes from './Header.module.css';

const links = [
    { link: '/Playlist', label: 'Playlists' },
];

export function Header() {
    const [active] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const [searchQuery, setSearchQuery] = useState('');

    const handleSearch = () => {
        // Handle search logic here
        console.log('Searching for:', searchQuery);
    };

    return (
        <header className={classes.header}>
            <Container size="md" className={classes.inner}>

                    <Button
                        className={classes.link}
                        data-active={active === '/Spotify' || undefined}
                        onClick={async () => {
                            try {
                                // Wysyłanie żądania do backendu
                                const response = await fetch("http://localhost:8080/api/spotify/authorize", {
                                    method: "GET",
                                    credentials: "include"
                                });
                                if (response.ok) {
                                    console.log(response);
                                    // Przekierowanie użytkownika do Spotify
                                    const redirectUrl = await response.text();
                                    window.location.href = redirectUrl;
                                } else {
                                    console.error('Błąd podczas autoryzacji');
                                }
                            } catch (error) {
                                console.error('Problem z połączeniem', error);
                            }
                        }}
                    >


                        Zaloguj do Spotify
                    </Button>



            </Container>
        </header>
    );
}
