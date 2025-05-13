import React from 'react';

export default function Loading({ message = "Loading..." }) {
    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 20,
            fontSize: '1.2em',
            fontWeight: 'bold'
        }}>
            <div className="spinner" style={{
                marginRight: 10,
                border: '4px solid #ccc',
                borderTop: '4px solid #333',
                borderRadius: '50%',
                width: 24,
                height: 24,
                animation: 'spin 1s linear infinite'
            }} />
            {message}
            <style>
                {`@keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }`}
            </style>
        </div>
    );
}
