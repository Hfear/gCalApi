import React, { useEffect, useState } from 'react';
import { useLoaderData } from 'react-router-dom';
import ClassView from './ClassView';
import Loading from './Loading';
import './myClasses.css';

export async function loader() {
    const res = await fetch('/my-classes', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to fetch classes');
    return await res.json();
}

export default function MyClasses() {
    const classes = useLoaderData();
    const [created, setCreated] = useState([]);
    const [joined, setJoined] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        fetch('/profile', { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('Profile fetch failed');
                return res.json();
            })
            .then(profile => {
                const me = profile.email;
                setCreated(classes.filter(c => c.createdBy?.email === me));
                setJoined(classes.filter(c => c.createdBy?.email !== me));
            })
            .catch(err => {
                console.error(err);
                setError('Could not load your profile.');
            })
            .finally(() => setLoading(false));
    }, [classes]);

    if (loading) {
        return <Loading message="Loading your classes..." />;
    }

    if (error) {
        return (
            <div className="myclasses-page">
                <h1 className="page-title">My Classes</h1>
                <p className="error">{error}</p>
            </div>
        );
    }

    return (
        <div className="myclasses-page">
            <h1 className="page-title">My Classes</h1>

            <section>
                <h2 className="section-title">Created Classes</h2>
                {created.length === 0 ? (
                    <p className="empty-state">No Classes Created.</p>
                ) : (
                    <ul className="classes-list">
                        {created.map(c => (
                            <ClassView key={c.id} singleClass={c} />
                        ))}
                    </ul>
                )}
            </section>

            <section>
                <h2 className="section-title">Joined Classes</h2>
                {joined.length === 0 ? (
                    <p className="empty-state">No Classes Joined.</p>
                ) : (
                    <ul className="classes-list">
                        {joined.map(c => (
                            <ClassView key={c.id} singleClass={c} />
                        ))}
                    </ul>
                )}
            </section>
        </div>
    );
}
