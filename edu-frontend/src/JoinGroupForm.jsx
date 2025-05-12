import React, { useState } from 'react';
import { useParams }    from 'react-router-dom';
import Loading          from './Loading.jsx';
import './JoinGroupForm.css';

export default function JoinGroupForm({ onClose, onJoin }) {
  const { classCode } = useParams();
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState(null);
  const [inputCode, setInputCode]   = useState('');

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    if (!inputCode.trim()) {
      setError('Please enter a group code.');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(
        `/class/${classCode}/groups/group/${encodeURIComponent(inputCode.trim())}/join`,
        { method: 'POST', credentials: 'include' }
      );
      if (!res.ok) {
        // assume 400 on invalid code
        throw new Error('Invalid group code');
      }
      onJoin();
    } catch (err) {
      setError(err.message || 'Could not join group.');
      setLoading(false);
    }
  };

  return (
    <div className="join-group-overlay" onClick={onClose}>
      <div className="join-group-modal" onClick={e => e.stopPropagation()}>
        <h2>Join Group</h2>
        <form onSubmit={handleSubmit} className="jg-form">
          <label>
            Enter group code:
            <input
              type="text"
              value={inputCode}
              onChange={e => setInputCode(e.target.value.toUpperCase())}
              maxLength={12}
              autoFocus
            />
          </label>
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
