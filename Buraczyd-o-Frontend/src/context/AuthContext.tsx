import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { useIsLogged } from '../hooks/useIsLogged';

interface AuthContextType {
    isLogged: boolean | null;
    setLoggedIn: (value: boolean) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const isLogged = useIsLogged();
    const [loggedIn, setLoggedIn] = useState<boolean | null>(isLogged);

    useEffect(() => {
        setLoggedIn(isLogged);
    }, [isLogged]);

    return (
        <AuthContext.Provider value={{ isLogged: loggedIn, setLoggedIn }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};