import { useEffect, useState } from 'react';

export const useIsLogged = () => {
  const [isLogged, setIsLogged] = useState<boolean | null>(null); // null = loading

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/auth/me', {
          method: "GET",
          credentials: 'include',
        });
        console.log('Response status:', res.status);
        // console.log('Response body:', await res.text());
        if (res.ok) {
          setIsLogged(true);
        } else {
          setIsLogged(false);
        }
      } catch (error) {
        console.error('Auth check failed:', error);
        setIsLogged(false);
      }
    };

    checkAuth();
  }, []);

  return isLogged;
};
