import { useEffect, useRef, useState, useCallback } from 'react';

interface WebPlaybackProps {
    token: string;
    trackUri: string;
    isPlaying: boolean;
    onTrackEnd: () => void;
    volume: number;
    onProgressUpdate: (progress: number, duration: number) => void;
    onPlayerReady?: (player: Spotify.Player) => void;
}

// Global flag to prevent multiple SDK loads
let isSDKLoaded = false;
let isSDKLoading = false;

const WebPlayback = ({ token, trackUri, isPlaying, onTrackEnd, volume, onProgressUpdate, onPlayerReady }: WebPlaybackProps) => {
    const [player, setPlayer] = useState<Spotify.Player | null>(null);
    const [deviceId, setDeviceId] = useState<string | null>(null);
    const [isReady, setIsReady] = useState(false);
    const previousTrackUri = useRef<string | null>(null);
    const hasPlayedOnce = useRef<boolean>(false);
    const progressIntervalRef = useRef<NodeJS.Timeout | null>(null);
    const initializationRef = useRef(false);

    // Memoized SDK ready callback
    const handleSDKReady = useCallback(() => {
        if (initializationRef.current || !token) return;
        initializationRef.current = true;

        console.log('[Spotify] Initializing player...');

        const p = new Spotify.Player({
            name: 'Web Playback SDK Player',
            getOAuthToken: cb => cb(token),
            volume: volume,
        });

        p.addListener('ready', ({ device_id }) => {
            console.log('[Spotify] Player ready with device ID:', device_id);
            setDeviceId(device_id);
            setIsReady(true);
        });

        p.addListener('not_ready', ({ device_id }) => {
            console.warn('[Spotify] Player went offline', device_id);
            setIsReady(false);
        });

        p.addListener('player_state_changed', state => {
            if (!state) return;

            const isTrackEnded =
                state.paused &&
                state.position === 0 &&
                state.track_window.previous_tracks.length > 0;

            if (isTrackEnded) {
                onTrackEnd();
                hasPlayedOnce.current = false;
            }

            // Update progress when state changes
            if (state.track_window.current_track) {
                const progress = (state.position / state.duration) * 100;
                onProgressUpdate(progress, state.duration);
            }
        });

        p.addListener('initialization_error', ({ message }) => {
            console.error('[Spotify] Initialization error:', message);
        });

        p.addListener('authentication_error', ({ message }) => {
            console.error('[Spotify] Authentication error:', message);
        });

        p.addListener('account_error', ({ message }) => {
            console.error('[Spotify] Account error:', message);
        });

        p.addListener('playback_error', ({ message }) => {
            console.error('[Spotify] Playback error:', message);
        });

        p.connect().then(success => {
            if (success) {
                console.log('[Spotify] Web Playback connected');
                setPlayer(p);
                onPlayerReady?.(p);
            } else {
                console.error('[Spotify] Failed to connect Web Playback');
                initializationRef.current = false;
            }
        }).catch(err => {
            console.error('[Spotify] Connection error:', err);
            initializationRef.current = false;
        });
    }, [token, volume, onTrackEnd, onProgressUpdate, onPlayerReady]);

    // Initialize SDK only once
    useEffect(() => {
        if (isSDKLoaded) {
            handleSDKReady();
            return;
        }

        if (isSDKLoading) {
            // Wait for SDK to load
            const checkSDK = setInterval(() => {
                if (window.Spotify && isSDKLoaded) {
                    clearInterval(checkSDK);
                    handleSDKReady();
                }
            }, 100);
            return () => clearInterval(checkSDK);
        }

        // Load SDK
        isSDKLoading = true;
        const script = document.createElement('script');
        script.src = 'https://sdk.scdn.co/spotify-player.js';
        script.async = true;

        script.onload = () => {
            isSDKLoaded = true;
            isSDKLoading = false;
        };

        script.onerror = () => {
            console.error('[Spotify] Failed to load SDK');
            isSDKLoading = false;
        };

        document.body.appendChild(script);

        window.onSpotifyWebPlaybackSDKReady = () => {
            console.log('[Spotify] SDK Ready');
            handleSDKReady();
        };

        return () => {
            if (progressIntervalRef.current) {
                clearInterval(progressIntervalRef.current);
            }
        };
    }, [handleSDKReady]);

    // Poll for playback state when playing
    useEffect(() => {
        if (player && isPlaying && isReady) {
            progressIntervalRef.current = setInterval(async () => {
                try {
                    const state = await player.getCurrentState();
                    if (state && state.track_window.current_track) {
                        const progress = (state.position / state.duration) * 100;
                        onProgressUpdate(progress, state.duration);
                    }
                } catch (err) {
                    console.warn('[Spotify] Failed to get current state:', err);
                }
            }, 1000);
        } else if (progressIntervalRef.current) {
            clearInterval(progressIntervalRef.current);
            progressIntervalRef.current = null;
        }

        return () => {
            if (progressIntervalRef.current) {
                clearInterval(progressIntervalRef.current);
            }
        };
    }, [player, isPlaying, isReady, onProgressUpdate]);

    // Volume control
    useEffect(() => {
        if (player && isReady) {
            player.setVolume(volume).catch(console.error);
        }
    }, [volume, player, isReady]);

    // Playback control
    useEffect(() => {
        const controlPlayback = async () => {
            if (!player || !deviceId || !trackUri || !isReady) return;

            const isNewTrack = trackUri !== previousTrackUri.current;

            if (isNewTrack) {
                console.log('[Spotify] Playing new track:', trackUri);
                try {
                    const res = await fetch(`https://api.spotify.com/v1/me/player/play?device_id=${deviceId}`, {
                        method: 'PUT',
                        body: JSON.stringify({ uris: [trackUri] }),
                        headers: {
                            'Content-Type': 'application/json',
                            Authorization: `Bearer ${token}`,
                        },
                    });

                    if (!res.ok) {
                        const errorText = await res.text();
                        console.error('[Spotify] Failed to start track:', errorText);
                        return;
                    }

                    previousTrackUri.current = trackUri;
                    hasPlayedOnce.current = true;
                } catch (err) {
                    console.error('[Spotify] Error starting track:', err);
                }
            } else if (isPlaying && hasPlayedOnce.current) {
                try {
                    console.log('[Spotify] Resuming playback');
                    await player.resume();
                } catch (err) {
                    console.warn('[Spotify] Resume failed:', err);
                }
            } else if (!isPlaying && hasPlayedOnce.current) {
                try {
                    console.log('[Spotify] Pausing playback');
                    await player.pause();
                } catch (err) {
                    console.warn('[Spotify] Pause failed:', err);
                }
            }
        };

        controlPlayback();
    }, [isPlaying, trackUri, player, deviceId, token, isReady]);

    // Cleanup on unmount
    useEffect(() => {
        return () => {
            if (player) {
                player.disconnect();
            }
            if (progressIntervalRef.current) {
                clearInterval(progressIntervalRef.current);
            }
        };
    }, [player]);

    return null;
};

export default WebPlayback;