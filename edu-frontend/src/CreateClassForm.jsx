import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CreateClassForm.css';

export default function CreateClassForm({ onClose }) {
    const [className, setClassName] = useState('');
    const [error,     setError]     = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);

        try {
            const res = await fetch('/class/create', {
                method:      'POST',
                headers:     { 'Content-Type': 'application/json' },
                credentials: 'include',
                body:        JSON.stringify({ title: className }),
            });

            if (!res.ok) {
                // Optionally read a message from the response:
                const text = await res.text();
                throw new Error(text || 'Failed to create class');
            }

            const newClass = await res.json();
            onClose();                               // close the modal
            navigate(`/class/${newClass.code}`);     // then navigate
        } catch (err) {
            console.error(err);
            setError(err.message || 'Could not create class');
        }
    };

    return (
        <div className="create-modal-overlay" onClick={onClose}>
            <div className="create-modal" onClick={e => e.stopPropagation()}>
                <h2>Create a Class</h2>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="className">Class Name:</label>
                    <input
                        id="className"
                        type="text"
                        placeholder="Enter class name"
                        value={className}
                        onChange={e => setClassName(e.target.value)}
                        required
                    />
                    <button type="submit" className="btn-filled">
                        Submit
                    </button>
                </form>
                {error && <p className="message">{error}</p>}
            </div>
        </div>
    );
}
