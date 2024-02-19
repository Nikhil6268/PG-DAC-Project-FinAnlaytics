import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap CSS
import './LoginPage.css'; // Import custom CSS

function LoginPage() {
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (event) => {
        const { name, value } = event.target;
        setCredentials(prevCredentials => ({
            ...prevCredentials,
            [name]: value
        }));
    };

   
  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    try {
      const response = await fetch('http://localhost:8082/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      const responseBody = await response.text();

      try {
        const data = JSON.parse(responseBody);
        if (response.ok) {
          localStorage.setItem('userToken', data.token);
          navigate('/dashboard');
        } else {
          setError(data.message || 'Incorrect username or password');
        }
      } catch (error) {
        setError('Server response was not in valid JSON format. Please try again later.');
      }
    } catch (error) {
      setError('An error occurred during login. Please try again later.');
    }
  };


    return (
        <div className="container-fluid py-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="login-container card shadow">
                        <div className="card-body">
                            <h1 className="card-title text-center">Welcome to Future Bank</h1>
                            {error && <div className="alert alert-danger" role="alert">{error}</div>}
                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label htmlFor="username" className="form-label">Username</label>
                                    <input type="text" className="form-control" id="username" name="username" value={credentials.username} onChange={handleChange} required />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input type="password" className="form-control" id="password" name="password" value={credentials.password} onChange={handleChange} required />
                                </div>
                                <button type="submit" className="btn btn-primary w-100">Log In</button>
                                <div className="mt-3 text-center">
                                    <a href="#forgot" className="d-block">Forgot Password?</a>
                                    <button type="button" className="btn btn-link" onClick={() => navigate('/register')}>Register</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div className="security-tips mt-4">
                        <h3>Security Tips:</h3>
                        <ul>
                            <li>Do not share your username and password with anyone.</li>
                            <li>Always log out after completing your session.</li>
                            <li>Ensure your computer is protected with up-to-date antivirus software.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
