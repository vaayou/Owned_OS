import React, { useState } from 'react';

export default function WalletModal({ isOpen, onClose, walletBalance, onTopup }) {
  const [topupAmount, setTopupAmount] = useState('50000');
  const [loading, setLoading] = useState(false);
  const [provider, setProvider] = useState('RAZORPAY');

  if (!isOpen) return null;

  const handleTopupSubmit = async (e) => {
    e.preventDefault();
    const amt = parseFloat(topupAmount);
    if (isNaN(amt) || amt <= 0) return;

    setLoading(true);
    await onTopup(amt);
    setLoading(false);
    onClose();
  };

  const QUICK_AMOUNTS = [10000, 25000, 50000, 100000, 250000];

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
          <p className="topup-subtitle">Select or enter top-up amount to charge your digital wallet.</p>

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

          <button type="submit" className="glass-button-primary wallet-submit-btn" disabled={loading}>
            {loading ? 'Simulating Payment...' : `Top-up ₹${parseFloat(topupAmount || 0).toLocaleString('en-IN')}`}
          </button>
        </form>
      </div>
    </div>
  );
}
