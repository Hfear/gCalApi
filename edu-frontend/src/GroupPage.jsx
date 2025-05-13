import React, { useState, useEffect, useCallback } from 'react';
import { useLoaderData, useParams } from 'react-router-dom';
import Loading from './Loading.jsx';
import AddEventForm from './AddEventForm.jsx';
import JoinGroupForm from './JoinGroupForm.jsx';
import './GroupPage.css';

export async function loader({ params }) {
    const { classCode, groupCode } = params;

    const res = await fetch(
        `/class/${classCode}/groups/${groupCode}/details`,
        { credentials: 'include' }
    );
    if (!res.ok) throw new Error('Failed to load group details');
    const group = await res.json();

    const profileRes = await fetch('/profile', { credentials: 'include' });
    if (!profileRes.ok) throw new Error('Failed to load profile');
    const profile = await profileRes.json();

    return { group, profile };
}

export default function GroupPage() {
    const { group, profile }       = useLoaderData();
    const { classCode, groupCode } = useParams();

    const [userInGroup, setUserInGroup]     = useState(
        profile.email === group.createdBy ||
        group.memberNames.includes(profile.name)
    );
    const [events, setEvents]               = useState([]);
    const [loadingEvents, setLoadingEvents] = useState(true);
    const [eventsError, setEventsError]     = useState(null);
    const [calendarId, setCalendarId]       = useState(null);
    const [reloadCalKey, setReloadCalKey]   = useState(0);
    const [showAddModal, setShowAddModal]   = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);

    const fetchEvents = useCallback(async () => {
        setLoadingEvents(true);
        setEventsError(null);
        try {
            const res = await fetch(
                `/calendar/group/${groupCode}/display`,
                { credentials: 'include' }
            );
            if (!res.ok) throw new Error('Failed to fetch calendar');
            const data = await res.json();
            setEvents(data || []);
        } catch (err) {
            console.error('Calendar fetch error:', err);
            setEventsError('Error loading events');
        } finally {
            setLoadingEvents(false);
        }
    }, [groupCode]);

    const fetchCalId = useCallback(async () => {
        try {
            const res = await fetch(
                `/calendar/group/${groupCode}/info`,
                { credentials: 'include' }
            );
            if (!res.ok) throw new Error('Failed to load calendar ID');
            const data = await res.json();
            setCalendarId(data.calendarId);
        } catch (err) {
            console.error('Failed to load calendar ID:', err);
        }
    }, [groupCode]);

    useEffect(() => {
        fetchEvents();
        fetchCalId();
    }, [fetchEvents, fetchCalId]);

    const handleEventAdded = (newEvt) => {
        setEvents((es) => [newEvt, ...es]);
        setReloadCalKey((k) => k + 1);
    };

    const handleShare = () => {
        navigator.clipboard.writeText(group.code);
        alert('Group code copied!');
    };

    const handleLeave = async () => {
        try {
            const res = await fetch(
                `/class/${classCode}/groups/group/${groupCode}/leave`,
                { method: 'POST', credentials: 'include' }
            );
            if (res.ok) {
                setUserInGroup(false);
                alert('You left the group.');
            } else {
                alert('Failed to leave group.');
            }
        } catch (err) {
            console.error('Leave group error:', err);
            alert('An error occurred.');
        }
    };

    const handleJoined = () => {
        setUserInGroup(true);
        setShowJoinModal(false);
        fetchEvents();
        setReloadCalKey((k) => k + 1);
    };

    return (
        <>
            <div
                className={`group-page${(showAddModal || showJoinModal) ? ' blur' : ''}`}
            >
                <header className="group-header">
                    <h1 className="group-title">{group.title}</h1>
                    <p className="group-subtitle">
                        Created by <strong>{group.createdBy}</strong>
                    </p>
                </header>

                <div className="group-content">
                    <div className="group-main">

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
                            {loadingEvents ? (
                                <Loading message="Loading events…" />
                            ) : eventsError ? (
                                <p className="error">{eventsError}</p>
                            ) : events.length === 0 ? (
                                <p>No upcoming events.</p>
                            ) : (
                                <ul className="events-list">
                                    {events.map((ev) => {
                                        const rs = ev.start?.dateTime || ev.start?.date || '';
                                        const re = ev.end?.dateTime   || ev.end?.date   || '';
                                        return (
                                            <li key={ev.id || ev.summary + rs} className="event-item">
                                                <strong>{ev.summary}</strong>
                                                <div className="event-times">
                                                    Start: {new Date(rs).toLocaleString()}<br />
                                                    End: {new Date(re).toLocaleString()}
                                                </div>
                                                {ev.location && (
                                                    <div className="event-location">{ev.location}</div>
                                                )}
                                            </li>
                                        );
                                    })}
                                </ul>
                            )}
                        </div>
                    </div>

                    <aside className="group-sidebar">
                        <div className="panel-card">
                            <h2 className="section-title">Members</h2>
                            <ul className="members-list">
                                {group.memberNames.map((n, i) => <li key={i}>{n}</li>)}
                            </ul>
                            <div className="sidebar-actions">
                                {userInGroup ? (
                                    <button className="btn-leave" onClick={handleLeave}>
                                        🚪 Leave Group
                                    </button>
                                ) : (
                                    <button
                                        className="btn-join"
                                        onClick={() => setShowJoinModal(true)}
                                    >
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
