import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './CreateGroupForm.css';
import Loading from "./Loading.jsx";

export default function CreateGroupForm({ onClose }) {
    const { classCode } = useParams();
    const [title, setTitle] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const res = await fetch(
                `/class/${classCode}/groups/creategroup`,
                {
                    method:      'POST',
                    headers:     { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body:        JSON.stringify({ title })
                }
            );
            if (!res.ok) throw new Error('Failed to create group');

            const group = await res.json();

            onClose();

            navigate(`/class/${classCode}/group/${group.code}`);
        } catch (err) {
            console.error(err);
            setError('Could not create group.');
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="create-group-overlay" onClick={onClose} style={{ padding: 20 }}>
            <div className="create-group-modal" onClick={e => e.stopPropagation()}>
                <h2>Create Group</h2>

                {loading ? (
                    <Loading message="Creating group..." />
                ) : (
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="groupTitle">Group Title:</label>
                        <input
                            id="groupTitle"
                            type="text"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            required
                        />
                        <button type="submit" className="btn-filled">
                            Create
                        </button>
                    </form>
                )}

                {error && <p className="error" style={{ color: 'red' }}>{error}</p>}
            </div>
        </div>
    );
}
