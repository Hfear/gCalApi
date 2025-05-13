// ClassPage.jsx
import React, { useState } from 'react';
import { useLoaderData, useNavigate, useParams } from 'react-router-dom';
import GroupView from './GroupView';
import CreateGroupForm from './CreateGroupForm';
import {authCheck} from './authCheck.jsx';
import './ClassPage.css';

export async function loader({ params }) {
    await authCheck();
    const { code } = params;

    // 1) fetch student IDs
    const idsRes = await fetch('/class/student-ids', {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ code }),
    });
    if (!idsRes.ok) throw new Error('Failed to load student IDs');
    const ids = await idsRes.json();

    // 2) fetch each student
    const students = await Promise.all(
        ids.map(id =>
            fetch(`/user/${id}`, { credentials: 'include' }).then(res => {
                if (!res.ok) throw new Error(`Failed to load user ${id}`);
                return res.json();
            })
        )
    );

    // 3) fetch class + groups
    const [classRes, groupsRes] = await Promise.all([
        fetch(`/class/${code}`,                   { credentials: 'include' }),
        fetch(`/class/${code}/groups/listgroups`, { credentials: 'include' })
    ]);
    if (!classRes.ok)  throw new Error('Failed to load class');
    if (!groupsRes.ok) throw new Error('Failed to load groups');

    const classroom = await classRes.json();
    const groups    = await groupsRes.json();
    return { classroom, students, groups };
}

export default function ClassPage() {
    const { classroom, students, groups } = useLoaderData();
    const navigate = useNavigate();
    const { code } = useParams();
    const [showCreateModal, setShowCreateModal] = useState(false);

    return (
        <>
            <div className={`class-page${showCreateModal ? ' blur' : ''}`}>
                {/* HEADER */}
                <header className="class-header">
                    <h1 className="class-title">{classroom.title}</h1>
                    <div className="class-code-row">
                        <span>Class Code: <strong>{classroom.code}</strong></span>
                        <button
                            className="copy-btn"
                            onClick={() => navigator.clipboard.writeText(classroom.code)}
                            title="Copy class code"
                        >📋</button>
                    </div>
                </header>

                {/* CREATE GROUP BUTTON */}
                <div className="class-actions">
                    <button
                        className="btn-create-group"
                        onClick={() => setShowCreateModal(true)}
                    >
                        👥 Create Group
                    </button>
                </div>

                {/* GROUPS TITLE (above the white box) */}
                <h2 className="groups-title">Groups:</h2>

                {/* MAIN CONTENT */}
                <main className="class-content">
                    {/* GROUPS PANEL */}
                    <section className="groups-panel">
                        <ul className="groups-list">
                            {groups.map(g => (
                                <GroupView
                                    key={g.code}
                                    singleGroup={g}
                                    classCode={classroom.code}
                                />
                            ))}
                        </ul>
                    </section>

                    {/* STUDENTS PANEL */}
                    <aside className="students-panel">
                        <h2 className="panel-title">Students</h2>
                        <ul className="students-list">
                            {students.map(s => (
                                <li key={s.userId}>{s.name}</li>
                            ))}
                        </ul>
                    </aside>
                </main>
            </div>

            {/* MODAL */}
            {showCreateModal && (
                <CreateGroupForm onClose={() => setShowCreateModal(false)} />
            )}
        </>
    );
}
