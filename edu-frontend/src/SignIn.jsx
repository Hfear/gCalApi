import { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './SignIn.css';

export default function SignIn() {
    const [authUrl, setAuthUrl] = useState("");

    useEffect(() => {
        fetch('/auth/url')
            .then(r => r.text())
            .then(url => setAuthUrl(url))
            .catch(e => console.error("Error fetching auth URL", e));
    }, []);

    return (
        <div className="app-container">
            <h1 className="logo-title">ED Group</h1>
            <div className="card-container">
                <h2>Sign in</h2>
                {authUrl ? (
                    <a href={authUrl} className="google-btn">
                        <img
                            src="https://developers.google.com/identity/images/g-logo.png"
                            alt="Google logo"
                        />
                        Sign in with Google
                    </a>
                ) : (
                    <p>Loading sign-in button…</p>
                )}
            </div>
        </div>
    );
}