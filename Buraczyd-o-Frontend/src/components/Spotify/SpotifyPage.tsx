import React from 'react';
import { Container, Title, Text } from '@mantine/core';

export function SpotifyPage() {
  return (
    <Container>
      <Title order={2} style={{ color: '#730029' }}>Spotify Integration</Title>
      <Text>This is the Spotify integration page. Here you can connect to your Spotify account and manage your Spotify playlists.</Text>
    </Container>
  );
}