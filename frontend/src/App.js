import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegistrationPage from './pages/RegistrationPage';
import Navbar from './components/Navbar';
import LoginRoute from './utils/LoginRoute';
import PrivateRoute from './utils/PrivateRoute';
import HomePage from './pages/HomePage';

function App() {
  return (
    <div className="App">
      <Router>
        <AuthProvider>
          <Navbar />
          <Routes>
            <Route path="/register" element={<RegistrationPage />} />
            <Route path="/login" element={
              <LoginRoute>
                <LoginPage />
              </LoginRoute>
            } />

            <Route path="/" element={
              <PrivateRoute>
                <HomePage />
              </PrivateRoute>
            } />
          </Routes>
        </AuthProvider>
      </Router>
    </div>
  );
}

export default App;
