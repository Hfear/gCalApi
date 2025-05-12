import React, { useState } from 'react';
import { useParams }    from 'react-router-dom';
import Loading          from './Loading.jsx';
import './JoinGroupForm.css';

export default function JoinGroupForm({ onClose, onJoin }) {
    const { classCode, groupCode } = useParams();
    const [loading, setLoading] = useState(false);
    const [error,   setError]   = useState(null);

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);
        setLoading(true);
        try {
            const res = await fetch(
                `/class/${classCode}/groups/group/${groupCode}/join`,
                { method: 'POST', credentials: 'include' }
            );
            if (!res.ok) throw new Error('Join failed');
            onJoin();
        } catch (err) {
            setError('Could not join group.');
            setLoading(false);
        }
    };

    return (
        <div className="join-group-overlay" onClick={onClose}>
            <div className="join-group-modal" onClick={e => e.stopPropagation()}>
                <h2>Join Group</h2>
                <form onSubmit={handleSubmit} className="jg-form">
                    <p>You’re about to join <strong>{groupCode}</strong>. Proceed?</p>
                    <button type="submit" className="btn-filled" disabled={loading}>
                        {loading ? <Loading message="Joining…" /> : '🤝 Join Group'}
                    </button>
                    <button type="button" className="btn-outline" onClick={onClose}>
                        Cancel
                    </button>
                </form>
                {error && <p className="error">{error}</p>}
            </div>
        </div>
    );
}
