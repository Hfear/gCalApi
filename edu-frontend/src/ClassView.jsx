import React from 'react';
import { Link } from 'react-router-dom';
import './ClassView.css';

export default function ClassView({ singleClass }) {
    if (!singleClass) return null;

    return (
        <li className="class-item">
            <Link to={`/class/${singleClass.code}`} className="class-link">
                <div className="class-item__content">
                    {singleClass.title}: {singleClass.description}
                </div>
            </Link>
        </li>
    );
}
