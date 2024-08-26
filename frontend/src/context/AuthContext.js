import { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { useNavigate } from 'react-router-dom';
import Config from '../Config';

const AuthContext = createContext();

export default AuthContext;

export const AuthProvider = ({children}) => {
    let baseURL = Config.baseURL;
    let [authTokens, setAuthTokens] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? JSON.parse(token) : null;
    });
    let [user, setUser] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? jwtDecode(token) : null;
    });
    let [bitsId, setBitsId] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? jwtDecode(token).bitsId : null;
    });
    let [bitsMail, setBitsMail] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? jwtDecode(token).bitsMail : null;
    });
    let [isManager, setIsManager] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? jwtDecode(token).authorities.some(auth => auth.authority === 'ROLE_MANAGER') : false;
    });
    let [isAdmin, setIsAdmin] = useState(() => {
        const token = localStorage.getItem('authTokens');
        return token ? jwtDecode(token).authorities.some(auth => auth.authority === 'ROLE_ADMIN') : false;
    });
    let [loading, setLoading] = useState(true);

    const navigate = useNavigate();

    const loginUser = async (e) => {
        e.preventDefault();
        const response = await fetch(`${baseURL}/auth/authenticate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'username': e.target.username.value,
                'password': e.target.password.value
            })
        });
        const data = await response.json();

        if (response.status === 200) {
            setAuthTokens(data);
            const decodedToken = jwtDecode(data.access);
            setUser(decodedToken);
            setBitsId(decodedToken.bitsId);
            setBitsMail(decodedToken.bitsMail);
            setIsManager(decodedToken.authorities.some(auth => auth.authority === 'ROLE_MANAGER'));
            setIsAdmin(decodedToken.authorities.some(auth => auth.authority === 'ROLE_ADMIN'));
            localStorage.setItem('authTokens', JSON.stringify(data));
            navigate('/');
        } else if (response.status === 401) {
            alert('Invalid credentials');
        }
    };

    const logoutUser = () => {
        setAuthTokens(null);
        setUser(null);
        setBitsId(null);
        setBitsMail(null);
        setIsManager(false);
        setIsAdmin(false);
        localStorage.removeItem('authTokens');
        navigate('/login');
    };

    const updateToken = async () => {
        const response = await fetch(`${baseURL}/auth/refresh-token`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({'refresh': authTokens?.refresh})
        });

        const data = await response.json();

        if (response.status === 200) {
            setAuthTokens(data);
            const decodedToken = jwtDecode(data.access);
            setUser(decodedToken);
            setBitsId(decodedToken.bitsId);
            setBitsMail(decodedToken.bitsMail);
            setIsManager(decodedToken.authorities.some(auth => auth.authority === 'ROLE_MANAGER'));
            setIsAdmin(decodedToken.authorities.some(auth => auth.authority === 'ROLE_ADMIN'));
            localStorage.setItem('authTokens', JSON.stringify(data));
        } else {
            logoutUser();
        }
    };

    useEffect(() => {
        if (authTokens) {
            const tokenData = jwtDecode(authTokens.access);
            const expirationTime = (tokenData.exp * 1000) - 60000;
            const now = Date.now();

            if (expirationTime < now) {
                logoutUser();
            } else {
                const timeLeft = expirationTime - now;
                const interval = setInterval(updateToken, timeLeft);
                setLoading(false);
                return () => clearInterval(interval);
            }
        } else {
            setLoading(false);
        }
    }, [authTokens]);

    const contextData = {
        authTokens: authTokens,
        user: user,
        bitsId: bitsId,
        bitsMail: bitsMail,
        isManager: isManager,
        isAdmin: isAdmin,
        loginUser: loginUser,
        logoutUser: logoutUser,
        loading: loading
    };

    return (
        <AuthContext.Provider value={contextData}>
            {children}
        </AuthContext.Provider>
    );
};
