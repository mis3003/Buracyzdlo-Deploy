// Authentication service for handling login, registration, and token management
import Cookies from 'js-cookie';

// Types for authentication
export interface LoginCredentials {
  login: string;
  password: string;
}

export interface RegisterCredentials {
  login: string;
  email: string;
  password: string;
  roles: string[];
}

export interface AuthResponse {
  token?: string;
  message?: string;
  error?: string;
}

// API URL constants
const API_URL = 'http://localhost:8080/api/auth';
const LOGIN_URL = `${API_URL}/signin`;
const REGISTER_URL = `${API_URL}/signup`;

// Token storage key
const TOKEN_KEY = 'auth_token';

/**
 * Login user with email and password
 * @param credentials User credentials (email and password)
 * @returns Promise with auth response
 */
export const login = async (credentials: LoginCredentials): Promise<AuthResponse | { error: string }> => {
  try {
    const response = await fetch(LOGIN_URL, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    const data = await response.json();

    if (!response.ok) {
      return {
        error: data.message || 'Login failed',
      };
    }

    // Sesja została zapisana — nie potrzebujesz tokena
    console.log('Login successful, user data:', data);

    // (opcjonalnie) przekierowanie lub ustawienie stanu logowania
    return data;
  } catch (error) {
    return {
      error: 'Network error. Please try again.',
    };
  }
};

/**
 * Register a new user
 * @param credentials User registration details
 * @returns Promise with auth response
 */
export const register = async (credentials: RegisterCredentials): Promise<AuthResponse> => {
  try {
    const response = await fetch(REGISTER_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    const data = await response.json();

    if (!response.ok) {
      return {
        error: data.message || 'Registration failed',
      };
    }

    return {
      message: 'Registration successful',
    };
  } catch (error) {
    return {
      error: 'Network error. Please try again.',
    };
  }
};

/**
 * Get the stored authentication token
 * @returns The authentication token or null if not found
 */
export const getToken = (): string | null => {
  return Cookies.get(TOKEN_KEY) || null; // Retrieve token from cookies
};

/**
 * Check if the user is authenticated
 * @returns True if the user is authenticated, false otherwise
 */
export const isAuthenticated = (): boolean => {
  return !!getToken();
};

/**
 * Logout the user by removing the token
 */
export const logout = (): void => {
  Cookies.remove(TOKEN_KEY); // Remove token from cookies
};

// Add a function to handle redirection after login
/**
 * Redirect the user to the home page after successful login
 */
const redirectToHome = (): void => {
  window.location.href = '/'; // Redirect to the home page
};
