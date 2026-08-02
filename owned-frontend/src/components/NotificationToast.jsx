import React, { useEffect } from 'react';

export default function NotificationToast({ message, type, onClose }) {
  useEffect(() => {
    if (!message) return;
    const timer = setTimeout(() => {
      onClose();
    }, 4000);
    return () => clearTimeout(timer);
  }, [message, onClose]);

  if (!message) return null;

  return (
    <div className={`notification-toast-glass ${type || 'info'}`}>
      <span className="toast-icon">
        {type === 'success' ? '✨' : type === 'warning' ? '⚠️' : 'ℹ️'}
      </span>
      <span className="toast-text">{message}</span>
      <button className="toast-close-btn" onClick={onClose}>✕</button>
    </div>
  );
}
