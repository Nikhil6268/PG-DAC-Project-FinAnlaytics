// authService.js
const API_BASE_URL = 'http://localhost:8082/api/auth';

// Function to log in a user
export async function loginUser(credentials) {
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });
        if (!response.ok) {
            throw new Error('Login failed');
        }
        const data = await response.json();
        saveUserDetails(data.user, data.token); // Saves user details and token in localStorage
        return data.user; // Returns the user object
    } catch (error) {
        console.error('Login error:', error);
        throw error;
    }
}

// Function to get the current user's details
export async function getCurrentUser() {
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            throw new Error('No token found');
        }
        const response = await fetch(`${API_BASE_URL}/current_user`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
        if (!response.ok) {
            throw new Error('Failed to fetch user details');
        }
        const user = await response.json();
        return user; // Returns the current user details
    } catch (error) {
        console.error('Error fetching user details:', error);
        throw error;
    }
}

// Function to update high security settings
export async function updateSecuritySettings(securitySettings) {
    try {
        const token = localStorage.getItem('userToken');
        if (!token) {
            throw new Error('Authentication token not found');
        }
        const response = await fetch(`${API_BASE_URL}/update-security-settings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(securitySettings),
        });
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to update security settings');
        }
        const data = await response.json();
        alert("High security options updated successfully.");
        return data; // Optionally return data if needed for further processing
    } catch (error) {
        console.error('Error updating high security settings:', error);
        alert("Failed to update high security options. Please try again.");
        throw error;
    }
}

// Save user details and token in localStorage
export function saveUserDetails(user, token) {
    localStorage.setItem('userDetails', JSON.stringify(user));
    localStorage.setItem('userToken', token);
}

// Retrieve user details from localStorage
export function getUserDetails() {
    const userDetails = localStorage.getItem('userDetails');
    return userDetails ? JSON.parse(userDetails) : null;
}

// Check if the user is logged in
export function isLoggedIn() {
    const token = localStorage.getItem('userToken');
    return !!token; // Converts to boolean - true if token exists, false otherwise
}

// Logout the user
export function logoutUser() {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userDetails');
    // Optionally redirect the user to the login page
    // window.location.href = '/login';
}

// Placeholder for refresh token functionality
async function refreshToken() {
    // Implement token refresh logic here
    // This will depend on your backend's token refresh mechanism
    throw new Error('refreshToken function not implemented');
}

// Conceptual method for automatic token refresh and retry on API calls
export async function autoRefreshTokenAndRetry(originalRequest) {
    try {
        const newToken = await refreshToken(); // Assumes refreshToken() is implemented
        if (newToken) {
            // Update the original request with new token and retry
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
            return fetch(originalRequest.url, originalRequest.options);
        }
    } catch (error) {
        console.error('Auto-refresh token and retry error:', error);
        throw error;
    }
}
