import React from 'react';
import { Container, Title, Text } from '@mantine/core';

export function YoutubePage() {
  return (
    <Container>
      <Title order={2} style={{ color: '#730029' }}>Youtube Integration</Title>
      <Text>This is the Youtube integration page. Here you can search for Youtube videos and add them to your playlists.</Text>
    </Container>
  );
}