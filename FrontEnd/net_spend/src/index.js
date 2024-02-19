// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client'; 
//import './index.css'; // Make sure to have this file for your styles
import App from './App';
import { Provider } from 'react-redux';
import {store} from '../src/store';
import { UserProvider } from './context/UserContext'; // Adjusted import path
import reportWebVitals from './reportWebVitals'; // If you're using Create React App, this might be included by default

const container = document.getElementById('root');
const root = ReactDOM.createRoot(container); // Create a root.

// Wrap App with both UserProvider and Redux Provider
root.render(
  <React.StrictMode>
    <Provider store={store}>
      <UserProvider>
        <App />
      </UserProvider>
    </Provider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
