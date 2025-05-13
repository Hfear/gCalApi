// MyGroups.jsx
import React from 'react';
import { useLoaderData } from 'react-router-dom';
import GroupView from './GroupView'
import {authCheck} from './authCheck.jsx';;
import './myGroups.css';

export async function loader() {
    await authCheck();
    const res = await fetch('/my-groups', { credentials: 'include' });
    if (!res.ok) throw new Error('Failed to fetch groups');
    return res.json();
}

export default function MyGroups() {
    const groups = useLoaderData();

    return (
        <div className="mygroups-page">
            <h1 className="page-title">My Groups: </h1>

            {groups.length === 0 ? (
                <p className="empty-state">You are not in any groups.</p>
            ) : (
                <ul className="groups-list">
                    {groups.map(g => (
                        <GroupView
                            key={g.code}
                            singleGroup={g}
                            classCode={g.classCode}
                        />
                    ))}
                </ul>
            )}
        </div>
    );
}
