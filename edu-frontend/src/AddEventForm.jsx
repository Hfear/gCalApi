import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './AddEventForm.css';

export default function AddEventForm({ onClose, onEventAdded }) {
  const { classCode, groupCode } = useParams();
  const navigate = useNavigate();

  const [summary, setSummary] = useState('');
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [location, setLocation] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        const startISO = new Date(start).toISOString();
        const endISO = new Date(end).toISOString();

        const eventData = {
            summary,
            location,
            description,
            start: { dateTime: startISO, timeZone: 'America/New_York' },
            end:   { dateTime: endISO,   timeZone: 'America/New_York' }
        };


    try {
        const res = await fetch(`/calendar/group/${groupCode}/addevent`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(eventData)
        }
      );
      if (!res.ok) throw new Error(await res.text() || 'Failed To Add');
         setSuccess(true);
         alert('Event Added!');
         setSummary(''); setStart(''); setEnd(''); setLocation(''); setDescription('');

        const newEvt = await res.json();
        onEventAdded(newEvt);
        onClose();

        setLoading(false);

        navigate(`/class/${classCode}/group/${groupCode}`);


    } catch (err) {
      setError(err.message);
    }
  };

    return (
        <div className="ae-overlay" onClick={onClose}>
            <div className="ae-modal" onClick={e => e.stopPropagation()}>
                <h2>Add Group Event</h2>
                <form onSubmit={handleSubmit} className="ae-form">
                    <label>Title</label>
                    <input
                        type="text"
                        value={summary}
                        onChange={e => setSummary(e.target.value)}
                        required
                        className="ae-input"
                    />

                    <label>Start</label>
                    <input
                        type="datetime-local"
                        value={start}
                        onChange={e => setStart(e.target.value)}
                        required
                        className="ae-input"
                    />

                    <label>End</label>
                    <input
                        type="datetime-local"
                        value={end}
                        onChange={e => setEnd(e.target.value)}
                        required
                        className="ae-input"
                    />

                    <label>Location</label>
                    <input
                        type="text"
                        value={location}
                        onChange={e => setLocation(e.target.value)}
                        className="ae-input"
                    />

                    <label>Description</label>
                    <textarea
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        className="ae-textarea"
                    />

                    <button type="submit" className="btn-filled" disabled={loading}>
                        {loading ? 'Adding…' : 'Add Event'}
                    </button>
                </form>
                {error && <p className="error">{error}</p>}
            </div>
        </div>
    );
}
