import { Navigate } from 'react-router-dom';
import { useContext } from 'react'
import AuthContext from '../context/AuthContext';


const LoginRoute = ({ children }) => {
    let {user} = useContext(AuthContext)
    return !user ? children : <Navigate to="/" replace />;
};

export default LoginRoute;