import React from 'react';
import { Container, Title, Text, Button, Group } from '@mantine/core';
import { useNavigate } from 'react-router-dom';

export function Home() {
  const navigate = useNavigate();

  return (
    <Container size="md" style={{ marginTop: 40 }}>
      <Title ta="center" style={{ color: '#730029' }}>
        Welcome to Buraczydło
      </Title>
      <Text c="dimmed" size="lg" ta="center" mt={5}>
        Your music playlist manager
      </Text>
      
      <Group justify="center" mt={30}>
        <Button 
          size="lg"
          style={{ backgroundColor: '#730029' }}
          onClick={() => navigate('/login')}
        >
          Get Started
        </Button>
      </Group>
    </Container>
  );
}