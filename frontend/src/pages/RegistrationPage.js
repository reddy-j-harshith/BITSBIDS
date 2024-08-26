import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import './RegistrationPage.css';
import Config from '../Config';

const RegistrationPage = () => {
  const [formData, setFormData] = useState({
    bitsMail: '',
    password: '',
    firstName: '',
    lastName: '',
    hostel: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  let navigate = useNavigate();
  let { logoutUser } = useContext(AuthContext);
  let { authTokens } = useContext(AuthContext);
  let baseURL = Config.baseURL;

  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    try {
      e.preventDefault();
      let response = await fetch(`${baseURL}/api/register/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.status === 201) {
        alert('Registration successful');
        if (authTokens) {
          logoutUser();
        }
        navigate('/login');
      }
    } catch (error) {
      setError("your credentials are not unique");
    }
  };

  const handleLoginButtonClick = () => {
    navigate("/"); // Navigate to login page
  };

  return (
    <div className="registration-form-container">
      <h2 style={{ textAlign: "center", color: 'white' }}>Register</h2>
      {error && <p className="error-message">{error}</p>}
      <div className='col'>
        <form onSubmit={handleSubmit}>
          <input
            type="email"
            name="bitsMail"
            placeholder='BITS Mail'
            value={formData.bitsMail}
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="password"
            placeholder='Password'
            value={formData.password}
            onChange={handleChange}
            required
          />

          <input
            type="text"
            name="firstName"
            placeholder='First Name'
            value={formData.firstName}
            onChange={handleChange}
            required
          />

          <input
            type="text"
            name="lastName"
            placeholder='Last Name'
            value={formData.lastName}
            onChange={handleChange}
            required
          />

          <input
            type="text"
            name="hostel"
            placeholder='Hostel'
            value={formData.hostel}
            onChange={handleChange}
            required
          />

          <input type="submit" value="Register" className='button' />
        </form>

        <div className="divider">
          <span className="divider-text">Already have an account?</span>
        </div>
        <form onSubmit={handleLoginButtonClick}>
          <input type="submit" value="Login" className='button' />
        </form>
      </div>
    </div>
  );
};

export default RegistrationPage;
