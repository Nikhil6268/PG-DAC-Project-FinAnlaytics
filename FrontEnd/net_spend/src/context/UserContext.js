import React, { createContext, useContext, useState, useEffect } from 'react';
import { getCurrentUser } from '../Services/authService';

// Create Context
const UserContext = createContext();

export const useUser = () => useContext(UserContext);

// Provider Component
export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null); // Add error state

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const currentUser = await getCurrentUser();
                setUser(currentUser);
                setError(null); // Clear any previous errors on successful fetch
            } catch (error) {
                console.error('Error fetching user data:', error);
                setError(error); // Set error state
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, []);

    // Provide error state along with user and loading states
    return (
        <UserContext.Provider value={{ user, loading, error }}>
            {children}
        </UserContext.Provider>
    );
};
