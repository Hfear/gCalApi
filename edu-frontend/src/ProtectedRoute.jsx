import React, {useEffect,useState} from 'react';
import { Navigate, useLocation, Outlet } from 'react-router-dom';

export default function ProtectedRoute({children}) {

    const [isAuth, setIsAuth] = useState(null);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await fetch('/oauth/isAuth');
                const data = await response.json();
                setIsAuth(data);
            } catch (error) {
                console.error("Error checking authentication:", error);
                setIsAuth(false);
                console.log("/isAuth is false");
            }
        };

        checkAuth();
    }, []);


    if (isAuth === null) {
        return <div>Loading...</div>;
    }
    else if (!isAuth)
    {
        alert("you need to be authenticated");
        return <Navigate to="/SignIn" />;
    }

    return children;

}