import React from 'react';


export default function Error({ code, message }) {
    const containerStyle = {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        color: '#b00020',
        backgroundColor: '#f9eaea',
        padding: '1rem',
    };

    const codeStyle = {
        fontSize: '3rem',
        margin: '0.5rem 0',
    };

    const messageStyle = {
        fontSize: '1.25rem',
        textAlign: 'center',
        maxWidth: '600px'
    };

    return (
        <div style={containerStyle}>
            <h1 style={codeStyle}>Error {code}</h1>
            {message && <p style={messageStyle}>{message}</p>}
        </div>
    );
}
