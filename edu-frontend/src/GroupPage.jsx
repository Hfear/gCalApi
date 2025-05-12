// GroupPage.jsx
import React, { useState, useEffect, useCallback } from 'react';
import { useLoaderData, useParams } from 'react-router-dom';
import Loading from './Loading.jsx';
import AddEventForm from './AddEventForm.jsx';
import JoinGroupForm from './JoinGroupForm.jsx';
import './GroupPage.css';

export async function loader({ params }) {
    const { classCode, groupCode } = params;

    // 1) Load group details (includes memberNames)
    const res = await fetch(`/class/${classCode}/groups/${groupCode}/details`, {
        credentials: 'include'
    });
    if (!res.ok) throw new Error('Failed to load group details');
    const group = await res.json();

    // 2) Load user profile
    const profileRes = await fetch('/profile', { credentials: 'include' });
    if (!profileRes.ok) throw new Error('Failed to load profile');
    const profile = await profileRes.json();

    return { group, profile };
}

export default function GroupPage() {
    const { group, profile }       = useLoaderData();
    const { classCode, groupCode } = useParams();

    // State
    const [userInGroup, setUserInGroup]       = useState(
        profile.email === group.createdBy ||
        group.memberNames.includes(profile.name)
    );
    const [calendarEvents, setCalendarEvents] = useState([]);
    const [loading, setLoading]               = useState(true);
    const [error, setError]                   = useState(null);
    const [calendarId, setCalendarId]         = useState(null);
    const [reloadCalKey, setReloadCalKey]     = useState(0);
    const [showAddModal, setShowAddModal]     = useState(false);
    const [showJoinModal, setShowJoinModal]   = useState(false);

    // Helper to pull out whatever shape Google gives us
    const getRaw = dt => {
        if (!dt) return '';
        if (dt.dateTime != null) {
            if (typeof dt.dateTime === 'object' && 'value' in dt.dateTime) {
                return dt.dateTime.value;
            }
            return dt.dateTime;
        }
        if (dt.date != null) {
            if (typeof dt.date === 'object' && 'value' in dt.date) {
                return dt.date.value;
            }
            return dt.date;
        }
        return '';
    };

    // Fetch upcoming events
    const fetchEvents = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const res = await fetch(`/calendar/group/${groupCode}/display`, {
                credentials: 'include'
            });
            if (!res.ok) throw new Error('Failed to load events');
            const data = await res.json();
            setCalendarEvents(data || []);
        } catch (err) {
            console.error('Calendar fetch error:', err);
            setError('Could not load events');
        } finally {
            setLoading(false);
        }
    }, [groupCode]);

    // Fetch embed calendar ID
    const fetchCalId = useCallback(async () => {
        try {
            const res = await fetch(`/calendar/group/${groupCode}/info`, {
                credentials: 'include'
            });
            if (!res.ok) throw new Error('Failed to load calendar info');
            const { calendarId } = await res.json();
            setCalendarId(calendarId);
        } catch (err) {
            console.error('Failed to load calendar ID:', err);
        }
    }, [groupCode]);

    // Initial load
    useEffect(() => {
        fetchEvents();
        fetchCalId();
    }, [fetchEvents, fetchCalId]);

    // Handlers
    const handleEventAdded = newEvt => {
        setCalendarEvents(es => [newEvt, ...es]);
        setReloadCalKey(k => k + 1);
    };

    const handleShare = () => {
        navigator.clipboard.writeText(group.code);
        alert('Copied group code!');
    };

    const handleLeave = async () => {
        try {
            const res = await fetch(
                `/class/${classCode}/groups/${groupCode}/leave`,
                { method: 'POST', credentials: 'include' }
            );
            if (res.ok) {
                setUserInGroup(false);
                alert('You left the group.');
            } else {
                alert('Failed to leave group.');
            }
        } catch {
            alert('Error leaving group.');
        }
    };

    const handleJoined = () => {
        setUserInGroup(true);
        setShowJoinModal(false);
        fetchEvents();
        setReloadCalKey(k => k + 1);
    };

    // Render
    return (
        <>
            <div className={`group-page${(showAddModal||showJoinModal)?' blur':''}`}>
                <header className="group-header">
                    <h1 className="group-title">{group.title}</h1>
                    <p className="group-subtitle">
                        Created by <strong>{group.createdBy}</strong>
                    </p>
                </header>

                <div className="group-content">
                    {/* ── MAIN COLUMN ── */}
                    <div className="group-main">

                        {/* Calendar View */}
                        <div className="panel-card">
                            <div className="panel-header">
                                <h2 className="section-title">Calendar View</h2>
                            </div>
                            {calendarId ? (
                                <iframe
                                    key={reloadCalKey}
                                    className="calendar-iframe"
                                    src={`https://calendar.google.com/calendar/embed?src=${encodeURIComponent(calendarId)}&ctz=America/New_York`}
                                    frameBorder="0"
                                    scrolling="no"
                                    title="Group Calendar"
                                />
                            ) : (
                                <Loading message="Loading calendar…" />
                            )}
                        </div>

                        {/* Upcoming Events */}
                        <div className="panel-card">
                            <div className="panel-header">
                                <h2 className="section-title">Upcoming Events</h2>
                                {userInGroup && (
                                    <button
                                        className="btn-create-event"
                                        onClick={() => setShowAddModal(true)}
                                    >
                                        ➕ Add Event
                                    </button>
                                )}
                            </div>

                            {loading ? (
                                <Loading message="Loading events…" />
                            ) : error ? (
                                <p className="error">{error}</p>
                            ) : calendarEvents.length > 0 ? (
                                <ul className="events-list">
                                    {calendarEvents.map(ev => {
                                        const rawStart = getRaw(ev.start);
                                        const rawEnd   = getRaw(ev.end);
                                        const sDate = new Date(rawStart);
                                        const eDate = new Date(rawEnd);
                                        const startDisplay = isNaN(sDate) ? rawStart : sDate.toLocaleString();
                                        const endDisplay   = isNaN(eDate) ? rawEnd   : eDate.toLocaleString();

                                        return (
                                            <li key={ev.id} className="event-item">
                                                <strong>{ev.summary}</strong>
                                                <div className="event-times">
                                                    Starts: {startDisplay}<br/>
                                                    Ends:   {endDisplay}
                                                </div>
                                                {ev.location && (
                                                    <div className="event-location">{ev.location}</div>
                                                )}
                                            </li>
                                        );
                                    })}
                                </ul>
                            ) : (
                                <p>No upcoming events.</p>
                            )}
                        </div>
                    </div>

                    {/* ── SIDEBAR ── */}
                    <aside className="group-sidebar">
                        <div className="panel-card">
                            <h2 className="section-title">Members</h2>
                            <ul className="members-list">
                                {group.memberNames.map((n,i) => (
                                    <li key={i}>{n}</li>
                                ))}
                            </ul>
                            <div className="sidebar-actions">
                                {userInGroup ? (
                                    <button className="btn-leave" onClick={handleLeave}>
                                        🚪 Leave Group
                                    </button>
                                ) : (
                                    <button className="btn-join" onClick={() => setShowJoinModal(true)}>
                                        🤝 Join Group
                                    </button>
                                )}
                                <button className="btn-share" onClick={handleShare}>
                                    📋 Copy Code
                                </button>
                            </div>
                        </div>
                    </aside>
                </div>
            </div>

            {showAddModal && (
                <AddEventForm
                    onClose={() => setShowAddModal(false)}
                    onEventAdded={handleEventAdded}
                />
            )}
            {showJoinModal && (
                <JoinGroupForm
                    onClose={() => setShowJoinModal(false)}
                    onJoin={handleJoined}
                />
            )}
        </>
    );
}
