import React from 'react';
import { MantineProvider } from '@mantine/core';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { CommonLayout } from './Layouts';
import { Login, Register } from './components/Auth';
import { Home } from './components/Home';
// import { PlaylistPage } from './components/Playlist';
import { SpotifyPage } from './components/Spotify';
import { YoutubePage } from './components/Youtube';
import PrivateRoute from './Routing/PrivateRoute';
import { AuthProvider } from './context/AuthContext';
import SpotifySuccessPage from './components/Spotify/SpotifySuccessPage';


function App() {
  return (
    <AuthProvider>
      <MantineProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/app/*"
              element={
                <PrivateRoute>
                  <CommonLayout />
                </PrivateRoute>
              }
            >
              <Route index element={<Navigate to="/app/playlist" replace />} />
              {/*<Route path="playlist" element={<PlaylistPage />} />*/}
              <Route path="spotify" element={<SpotifyPage />} />
              <Route path="youtube" element={<YoutubePage />} />

            </Route>

            <Route path="/spotify/succes" element={<SpotifySuccessPage />} />
          </Routes>
        </BrowserRouter>
      </MantineProvider>
    </AuthProvider>
  );
}

export default App;
