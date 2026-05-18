import React, { useState } from 'react';
import { TextInput, PasswordInput, Button, Paper, Title, Text, Container, Anchor } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { register } from '../../services/authService';

export function Register() {
  const [login, setLogin] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Basic validation
    if (!login || !email || !password || !confirmPassword) {
      setError('Please fill in all fields');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    try {
      setLoading(true);

      // Call the register function from authService
      const response = await register({ login, email, password, roles: ["ROLE_USER"] });

      if (response.error) {
        setError(response.error);
      } else {
        setSuccess(response.message || 'Registration successful!');
        // Redirect to login page after a short delay
        setTimeout(() => {
          navigate('/login');
        }, 1500);
      }
    } catch (err) {
      setError('An unexpected error occurred. Please try again.');
      console.error('Registration error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container size={420} my={40}>
      <Title ta="center" style={{ color: '#730029' }}>
        Create an account
      </Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Already have an account?{' '}
        <Anchor size="sm" component="button" onClick={() => navigate('/login')} style={{ color: '#730029' }}>
          Sign in
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
            value={login}
            onChange={(e) => setLogin(e.target.value)}
          />

          <TextInput
            label="Email"
            placeholder="your@email.com"
            required
            mt="md"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <PasswordInput
            label="Password"
            placeholder="Your password"
            required
            mt="md"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <PasswordInput
            label="Confirm Password"
            placeholder="Confirm your password"
            required
            mt="md"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          <Button
            fullWidth
            mt="xl"
            type="submit"
            style={{ backgroundColor: '#730029' }}
            loading={loading}
            disabled={loading}
          >
            {loading ? 'Registering...' : 'Register'}
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
