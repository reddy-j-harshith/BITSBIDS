import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import './LoginPage.css';

const LoginPage = () => {

  let { loginUser } = useContext(AuthContext);
  let navigate = useNavigate();

  const handleRegisterButtonClick = () => {
    navigate("/register");
  };

  return (
    <div className="login-form-container">
      <div className="row">
        <h2 style={{ textAlign: "center", color: 'white' }}>Login</h2>
        <div className="col">
          <form onSubmit={loginUser}>
            <input type="text" name="username" placeholder="Bits ID" required />
            <input type="password" name="password" placeholder="Password" required />
            <input type="submit" value="Login" className='button'/>
          </form>
          <div className="divider">
            <span className="divider-text">New User?</span>
          </div>
          <form onSubmit={handleRegisterButtonClick} className='register-button'>
            <input type="submit" value="Register" className='button'/>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
