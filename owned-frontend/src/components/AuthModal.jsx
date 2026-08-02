import React, { useState } from 'react';

export default function AuthModal({ isOpen, onClose, onAuthSuccess, user, onLogout }) {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    setLoading(true);

    try {
      if (isLogin) {
        await onAuthSuccess('login', { username, password });
      } else {
        await onAuthSuccess('register', { username, email, password });
      }
      onClose();
    } catch (err) {
      setErrorMsg('Authentication failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop-glass" onClick={onClose}>
      <div className="modal-glass-container auth-modal-glass" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close-btn" onClick={onClose}>✕</button>

        {user ? (
          /* User Profile Logged In View */
          <div className="logged-in-profile-view">
            <div className="avatar-big-glass">
              {user.username.charAt(0).toUpperCase()}
            </div>
            <h2>Welcome back, {user.username}!</h2>
            <p className="user-email-text">{user.email || 'Collector Account'}</p>
            <div className="profile-status-badge">✓ Verified Owned OS Collector</div>

            <button className="glass-button-secondary logout-btn" onClick={onLogout}>
              Sign Out
            </button>
          </div>
        ) : (
          /* Auth Form View */
          <div className="auth-form-container">
            <div className="auth-tabs">
              <button
                className={`auth-tab-btn ${isLogin ? 'active' : ''}`}
                onClick={() => setIsLogin(true)}
              >
                Sign In
              </button>
              <button
                className={`auth-tab-btn ${!isLogin ? 'active' : ''}`}
                onClick={() => setIsLogin(false)}
              >
                Register Collector
              </button>
            </div>

            {errorMsg && <div className="auth-error-banner">{errorMsg}</div>}

            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <label>Username</label>
                <input
                  type="text"
                  className="glass-input"
                  placeholder="Enter username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

              {!isLogin && (
                <div className="form-group">
                  <label>Email Address</label>
                  <input
                    type="email"
                    className="glass-input"
                    placeholder="Enter email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              )}

              <div className="form-group">
                <label>Password</label>
                <input
                  type="password"
                  className="glass-input"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <button type="submit" className="glass-button-primary auth-submit-btn" disabled={loading}>
                {loading ? 'Processing...' : isLogin ? 'Sign In' : 'Create Collector Account'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
