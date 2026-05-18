import React, { useState } from 'react';
import { TextInput, PasswordInput, Button, Paper, Title, Text, Container, Group, Anchor } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { login as loginUser } from '../../services/authService';
import { useIsLogged } from '../../hooks/useIsLogged';

export function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Basic validation
    if (!username || !password) {
      setError('Please fill in all fields');
      return;
    }

    try {
      setLoading(true);

      // Call the login function from authService
      const response = await loginUser({ login: username, password });

      if (response.error) {
        setError(response.error);
      } else {
        setSuccess('Login successful!');
        // Redirect to main application after a short delay
        setTimeout(() => {
          navigate('/app/playlist');
        }, 10);
      }
    } catch (err) {
      setError('An unexpected error occurred. Please try again.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container size={420} my={40}>
      <Title ta="center" style={{ color: '#730029' }}>
        Welcome back!
      </Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Don't have an account yet?{' '}
        <Anchor size="sm" component="button" onClick={() => navigate('/register')} style={{ color: '#730029' }}>
          Create account
        </Anchor>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        {error && <Text c="red" mb={10}>{error}</Text>}
        {success && <Text c="green" mb={10}>{success}</Text>}

        <form onSubmit={handleSubmit}>
          <TextInput
            label="Username"
            placeholder="Your username"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />

          <PasswordInput
            label="Password"
            placeholder="Your password"
            required
            mt="md"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <Group justify="space-between" mt="lg">
            <Anchor component="button" size="sm" style={{ color: '#730029' }}>
              Forgot password
            </Anchor>
          </Group>

          <Button
            fullWidth
            mt="xl"
            type="submit"
            style={{ backgroundColor: '#730029' }}
            loading={loading}
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
