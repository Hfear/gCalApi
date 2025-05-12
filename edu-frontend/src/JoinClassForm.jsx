import React, { useState } from 'react';
import './JoinClass.css';

export default function JoinClassModal({ onClose }) {
    const [code,    setCode]    = useState('');
    const [message, setMessage] = useState('');

    const handleJoin = async e => {
        e.preventDefault();
        try {
            const res = await fetch('/join', {
                method:      'POST',
                headers:     { 'Content-Type': 'application/json' },
                credentials: 'include',
                body:        JSON.stringify({ code }),
            });
            const joined = await res.json();
            setMessage(
                joined
                    ? 'Joined Successfully!'
                    : 'Failed to Join .'
            );
        } catch {
            setMessage('Server error.');
        }
    };

    return (
        <div className="join-modal-overlay" onClick={onClose}>
            <div className="join-modal" onClick={e => e.stopPropagation()}>
                <form onSubmit={handleJoin}>
                    <label htmlFor="classCode">Enter Code:</label>
                    <input
                        id="classCode"
                        type="text"
                        value={code}
                        onChange={e => setCode(e.target.value)}
                        required
                    />

                    <button type="submit" className="btn-filled">
                        Join
                    </button>
                    {message && <p className="message">{message}</p>}
                </form>
            </div>
        </div>
    );
}
