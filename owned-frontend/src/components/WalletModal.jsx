import React, { useState } from 'react';

export default function WalletModal({ isOpen, onClose, walletBalance, onTopup }) {
  const [topupAmount, setTopupAmount] = useState('10');
  const [loading, setLoading] = useState(false);
  const [provider, setProvider] = useState('UPI');
  const [upiApp, setUpiApp] = useState('gpay');
  const [upiId, setUpiId] = useState('9504727218-2@ybl');

  if (!isOpen) return null;

  const handleTopupSubmit = async (e) => {
    e.preventDefault();
    const amt = parseFloat(topupAmount);
    if (isNaN(amt) || amt <= 0) return;

    setLoading(true);
    await onTopup(amt, provider);
    setLoading(false);
    onClose();
  };

  const QUICK_AMOUNTS = [10, 50, 100, 500, 1000];

  const UPI_APPS = [
    { id: 'gpay', name: 'Google Pay', icon: '⚡' },
    { id: 'phonepe', name: 'PhonePe', icon: '🟣' },
    { id: 'paytm', name: 'Paytm', icon: '🔹' },
    { id: 'bhim', name: 'BHIM UPI', icon: '🇮🇳' },
  ];

  return (
    <div className="modal-backdrop-glass" onClick={onClose}>
      <div className="modal-glass-container wallet-modal-glass" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close-btn" onClick={onClose}>✕</button>

        <div className="wallet-header-card">
          <span className="wallet-label-mini">OWNED VAULT WALLET</span>
          <h1 className="wallet-balance-big">₹{walletBalance.toLocaleString('en-IN')}</h1>
          <span className="currency-tag">INR • Active Vault Funds</span>
        </div>

        <form onSubmit={handleTopupSubmit} className="topup-form-section">
          <h3>Add Instant Funds</h3>
          <p className="topup-subtitle">🔥 Rate: <strong>₹10 = 1,000,000 Vault Coins</strong> (100,000 Coins / ₹1)</p>

          <div className="quick-amount-pills">
            {QUICK_AMOUNTS.map((amt) => (
              <button
                type="button"
                key={amt}
                className={`amount-pill ${topupAmount === String(amt) ? 'active' : ''}`}
                onClick={() => setTopupAmount(String(amt))}
              >
                +₹{amt.toLocaleString('en-IN')}
              </button>
            ))}
          </div>

          <div className="form-group">
            <label>Custom Amount (₹)</label>
            <input
              type="number"
              className="glass-input"
              value={topupAmount}
              onChange={(e) => setTopupAmount(e.target.value)}
              placeholder="50000"
              required
            />
          </div>

          <div className="provider-selector-row">
            <span className="provider-label">Payment Gateway:</span>
            <div className="provider-options">
              <button
                type="button"
                className={`provider-btn ${provider === 'UPI' ? 'active' : ''}`}
                onClick={() => setProvider('UPI')}
              >
                📱 UPI
              </button>
              <button
                type="button"
                className={`provider-btn ${provider === 'RAZORPAY' ? 'active' : ''}`}
                onClick={() => setProvider('RAZORPAY')}
              >
                Razorpay
              </button>
              <button
                type="button"
                className={`provider-btn ${provider === 'STRIPE' ? 'active' : ''}`}
                onClick={() => setProvider('STRIPE')}
              >
                Stripe
              </button>
            </div>
          </div>

          {provider === 'UPI' && (
            <div className="upi-details-section" style={{
              margin: '12px 0',
              padding: '12px',
              background: 'rgba(255, 255, 255, 0.05)',
              borderRadius: '12px',
              border: '1px solid rgba(255, 255, 255, 0.1)'
            }}>
              <label style={{ display: 'block', marginBottom: '8px', fontSize: '0.85rem', color: '#a0aec0' }}>
                Select UPI App / Method:
              </label>
              <div style={{ display: 'flex', gap: '8px', marginBottom: '12px', flexWrap: 'wrap' }}>
                {UPI_APPS.map((app) => (
                  <button
                    type="button"
                    key={app.id}
                    className={`provider-btn ${upiApp === app.id ? 'active' : ''}`}
                    onClick={() => setUpiApp(app.id)}
                    style={{ fontSize: '0.82rem', padding: '6px 12px' }}
                  >
                    {app.icon} {app.name}
                  </button>
                ))}
              </div>

              <div className="form-group" style={{ marginBottom: 0 }}>
                <label style={{ fontSize: '0.8rem', color: '#a0aec0' }}>Virtual Payment Address (VPA / UPI ID)</label>
                <input
                  type="text"
                  className="glass-input"
                  value={upiId}
                  onChange={(e) => setUpiId(e.target.value)}
                  placeholder="9504727218-2@ybl"
                  style={{ fontSize: '0.88rem' }}
                />
              </div>
            </div>
          )}

          <button type="submit" className="glass-button-primary wallet-submit-btn" disabled={loading}>
            {loading ? 'Processing Payment...' : `Top-up ₹${parseFloat(topupAmount || 0).toLocaleString('en-IN')} (+${(parseFloat(topupAmount || 0) * 100000).toLocaleString('en-IN')} Coins) via ${provider}`}
          </button>
        </form>
      </div>
    </div>
  );
}
