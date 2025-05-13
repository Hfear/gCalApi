import React, { useState } from 'react';
import JoinClassModal from './JoinClassForm';
import CreateClassModal from './CreateClassForm';
import './HomePage.css';

export default function HomePage() {
    const [showJoinModal,   setShowJoinModal]   = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [events,          setEvents]          = useState([]);

    const fetchEvents = async () => {
        try {
            const res = await fetch('http://localhost:8080/events', {
                method:      'GET',
                credentials: 'include',
            });
            const text = await res.text();
            console.log('🔍 raw response:', text);

            const lines = text
                .split('\n')
                .slice(1)
                .filter(line => line.trim().length > 0);

            const list = lines.map(line => {
                const m = line.match(/^(.*) \((.*)\)$/);
                return {
                    summary: m ? m[1] : line,
                    start:   { dateTime: m ? m[2] : '' },
                };
            });

            console.log('✅ mapped events:', list);
            setEvents(list);
        } catch (e) {
            console.error('Error fetching events:', e);
        }
    };

    return (
        <>

            <div
                className={
                    `homepage-container${(showJoinModal || showCreateModal) ? ' blur' : ''}`
                }
            >

                <section className="welcome-card">

                    <h1 className="card-title">Welcome to ED Group</h1>
                    <p className="card-subtext">
                        Organize, join, and collaborate with your class teams easily.
                    </p>
                    <div className="button-group">
                        <button
                            className="button-primary"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Create Class
                        </button>
                        <button
                            className="button-outline"
                            onClick={() => setShowJoinModal(true)}
                        >
                            Join Class
                        </button>
                    </div>
                </section>


                <section className="events-section">
                    <h2>Calendar Events</h2>
                    <button onClick={fetchEvents} className="button-small">
                        Fetch Events
                    </button>

                    {events.length > 0 ? (
                        <div className="events-list">
                            {events.map(evt => (
                                <div
                                    className="event-item"
                                    key={evt.summary + evt.start.dateTime}
                                >
                                    <h3>{evt.summary}</h3>
                                    <p>{new Date(evt.start.dateTime).toLocaleString()}</p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p className="no-events">No events yet.</p>
                    )}
                </section>
            </div>


            {showJoinModal && (
                <JoinClassModal onClose={() => setShowJoinModal(false)} />
            )}
            {showCreateModal && (
                <CreateClassModal onClose={() => setShowCreateModal(false)} />
            )}
        </>
    );
}
