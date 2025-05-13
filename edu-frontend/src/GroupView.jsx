import React from 'react';
import { Link } from 'react-router-dom';
import './GroupView.css';

export default function GroupView({ singleGroup, classCode }) {
    if (!singleGroup) return null;

    const memberCount = singleGroup.memberIds?.length ?? 0;

    return (
        <li className="group-item">
            <Link
                to={`/class/${classCode}/group/${singleGroup.code}`}
                className="group-link"
            >
                <div className="group-item__content">
                    {singleGroup.title} — {memberCount}{' '}
                    {memberCount === 1 ? 'member' : 'members'}
                </div>
            </Link>
        </li>
    );
}
