import React, { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Typ dla propsów komponentu
interface PrivateRouteProps {
    children: ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
    const { isLogged } = useAuth();

    if (isLogged === null) {
        // Możesz dodać loader lub placeholder, gdy status logowania jest ładowany
        return <div>Loading...</div>;
    }

    return isLogged ? (
        <>{children}</> // Jeśli użytkownik jest zalogowany, renderuj dzieci
    ) : (
        <Navigate to="/" replace /> // W przeciwnym razie przekieruj na stronę główną
    );
};

export default PrivateRoute;
