import React, { useContext, useState } from 'react';
import { NavLink } from 'react-router-dom';
import AuthContext from '../context/AuthContext';
import './Navbar.css';

const Header = () => {
  let { user, logoutUser, admin, loading } = useContext(AuthContext);
  const [menuOpen, setMenuOpen] = useState(false);

  if (loading) {
    return <div>Loading...</div>;
  }

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <div>
      <div className="navbar">
        <NavLink to="/" className="logo">
          <img src="/Algorithmix1.png" alt="Algorithmix" className="logo-img" />
        </NavLink>
        <div className="navbar-center">
          <h2 className='title-name'>
            Algorithmix
          </h2>
        </div>
        <button className="menu-button" onClick={toggleMenu}>
          &#9776; {/* This is a hamburger icon */}
        </button>
        <div className={`navbar-right ${menuOpen ? 'show' : ''}`}>
          {(admin && user) && (
            <NavLink to="/admin" className={({ isActive }) => isActive ? "active" : ""}>Panel</NavLink>
          )}
          {(admin && user) && (
            <NavLink to="/problem" className={({ isActive }) => isActive ? "active" : ""}>Problem</NavLink>
          )}
          <NavLink to="/" className={({ isActive }) => isActive ? "active" : ""} end>Home</NavLink>
          <NavLink to="/ide" className={({ isActive }) => isActive ? "active" : ""}>IDE</NavLink>
          {user ? (
            <NavLink to="/Login" onClick={logoutUser} className={({ isActive }) => isActive ? "active" : ""}>Logout</NavLink>
          ) : (
            <NavLink to="/login" className={({ isActive }) => isActive ? "active" : ""}>Login</NavLink>
          )}
          {user ? (
            null
          ) : (
            <NavLink to="/register" className={({ isActive }) => isActive ? "active" : ""}>Register</NavLink>
          )}
        </div>
      </div>
      <div className='space'>
      </div>
    </div>
  );
}

export default Header;
