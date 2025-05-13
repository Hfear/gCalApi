import { useLoaderData, useNavigate } from 'react-router-dom';
import "./Profile.css";
import {authCheck} from './authCheck.jsx';

export async function loader() {
    await authCheck();
    const res = await fetch("/profile", { credentials: "include" });
    if (!res.ok) throw new Error("Failed to fetch user data");
    return res.json();
}

export default function Profile() {
    const { name, email } = useLoaderData();

    // getting initals
    const initials = name
        .split(" ")
        .map((word) => word[0])
        .join("")
        .toUpperCase();

        const handleLogOut = async () => {
            await fetch('/logout', {
              method: 'POST',
              credentials: 'include'
            });
            navigate("/signin", { replace: true });
          };

    return (
        <div className="profile-page">
          <div className="profile-card">
            <div className="profile-avatar">{initials}</div>
            <h2 className="profile-name">{name}</h2>
            <p className="profile-email">{email}</p>

            <button className="btn btn-outline">Edit Profile</button>
            <button
              className="btn btn-filled"
              onClick={handleLogOut}
            >
              Sign Out
            </button>
          </div>
        </div>
      );
    }
