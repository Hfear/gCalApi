import React, { useState } from 'react';
import { useLoaderData } from 'react-router-dom';
import GroupView from './GroupView';
import CreateGroupForm from './CreateGroupForm';
import './ClassPage.css';

export async function loader({ params }) {
    const { code } = params;

    const [ classRes, groupsRes ] = await Promise.all([
        fetch(`/class/${code}`,                   { credentials: 'include' }),
        fetch(`/class/${code}/groups/listgroups`, { credentials: 'include' })
    ]);
    if (!classRes.ok)  throw new Error('Failed to load class');
    if (!groupsRes.ok) throw new Error('Failed to load groups');

    const classroom = await classRes.json();
    const groups    = await groupsRes.json();

    const studentIds = classroom.studentIds || [];
    const students = await Promise.all(
        studentIds.map(id =>
            fetch(`/user/${id}`, { credentials: 'include' }).then(res => {
                if (!res.ok) throw new Error(`Failed to load user ${id}`);
                return res.json();
            })
        )
    );

    return { classroom, students, groups };
}

export default function ClassPage() {
    const { classroom, students, groups } = useLoaderData();
    const [showCreateModal, setShowCreateModal] = useState(false);

    return (
        <>
            <div className={`class-page${showCreateModal ? ' blur' : ''}`}>
                <header className="class-header">
                    <h1 className="class-title">{classroom.title}</h1>
                    <div className="class-code-row">
            <span>
              Class Code: <strong>{classroom.code}</strong>
            </span>
                        <button
                            className="copy-btn"
                            onClick={() => navigator.clipboard.writeText(classroom.code)}
                            title="Copy class code"
                        >📋</button>
                    </div>
                </header>

                <div className="class-actions">
                    <button
                        className="btn-create-group"
                        onClick={() => setShowCreateModal(true)}
                    >👥 Create Group</button>
                </div>

                <main className="class-content">
                    <section className="groups-panel">
                        <h2 className="groups-title">Groups:</h2>

                        {groups.length > 0 ? (
                            <ul className="groups-list">
                                {groups.map(g => (
                                    <GroupView
                                        key={g.code}
                                        singleGroup={g}
                                        classCode={classroom.code}
                                    />
                                ))}
                            </ul>
                        ) : (
                            <p style={{ padding: '1rem', color: 'var(--color-gray)' }}>
                                No groups have been created yet...
                            </p>
                        )}
                    </section>

                    <aside className="students-panel">
                        <h2 className="panel-title">Students</h2>
                        {students.length > 0 ? (
                            <ul className="students-list">
                                {students.map(s => (
                                    <li key={s.userId}>{s.name}</li>
                                ))}
                            </ul>
                        ) : (
                            <p>No students enrolled.</p>
                        )}
                    </aside>
                </main>
            </div>

            {showCreateModal && (
                <CreateGroupForm onClose={() => setShowCreateModal(false)} />
            )}
        </>
    );
}
