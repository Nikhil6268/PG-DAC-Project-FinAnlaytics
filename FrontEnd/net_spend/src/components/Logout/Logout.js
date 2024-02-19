import React from 'react';
import { useNavigate } from 'react-router-dom';

const Logout = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Perform any necessary cleanup here, like clearing user data from state or localStorage
    // For example: localStorage.removeItem('userToken');

    // Redirect to the login page
    navigate('/login');
  };

  return (
    <div className="logout-container">
      <h2>Are you sure you want to logout?</h2>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Logout;
