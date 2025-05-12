import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { FaUserCircle } from 'react-icons/fa';
import './Navbar.css';

export default function Navbar() {
    return (
        <>
            <nav className="navbar navbar-expand-sm navbar-royal">
                <div className="container-fluid">
                    <Link className="navbar-brand mx-auto" to="/homepage">
                        ED Group  |
                    </Link>

                    <button
                        className="navbar-toggler"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapsibleNavbar"
                    >
                        <span className="navbar-toggler-icon" />
                    </button>

                    <div className="collapse navbar-collapse" id="collapsibleNavbar">
                        <ul className="navbar-nav me-auto">
                            <li className="nav-item">
                                <Link className="nav-link" to="/my-classes">
                                    My Classes  |
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/my-groups">
                                    My Groups  |
                                </Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/link">
                                    Link  |
                                </Link>
                            </li>
                        </ul>

                        <ul className="navbar-nav ms-auto">
                            <li className="nav-item">
                                <Link
                                    className="nav-link profile-icon"
                                    to="/profile"
                                    aria-label="My Profile"
                                >
                                    <FaUserCircle size={28} />
                                </Link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div className="navbar-bottom" />
            <Outlet />
        </>
    );
}
