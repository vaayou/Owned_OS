import React, { useState } from 'react';

export default function VaultView({ vaultItems, netWorthData, onInspectCertificate }) {
  const [selectedCertItem, setSelectedCertItem] = useState(null);

  const totalValue = vaultItems.reduce((acc, item) => acc + (item.price || 0), 0);

  return (
    <div className="vault-view-container">
      {/* Net Worth Top Header */}
      <div className="networth-hero-card glass-panel">
        <div className="networth-main-info">
          <span className="networth-label">MY VAULT VALUATION</span>
          <h1 className="networth-amount">₹{(netWorthData.totalNetWorthInr || totalValue).toLocaleString('en-IN')}</h1>
          <div className="vault-tier-pill">
            <span className="sparkle-icon">✨</span>
            <span>{netWorthData.tierRank || 'LUXURY VIP COLLECTOR'}</span>
          </div>
        </div>

        <div className="networth-stats-grid">
          <div className="stat-card-glass">
            <span className="stat-number">{vaultItems.length}</span>
            <span className="stat-label">Items Owned</span>
          </div>
          <div className="stat-card-glass">
            <span className="stat-number">100%</span>
            <span className="stat-label">Authenticity Verified</span>
          </div>
          <div className="stat-card-glass">
            <span className="stat-number">Rank #4</span>
            <span className="stat-label">Global Leaderboard</span>
          </div>
        </div>
      </div>

      {/* Owned Items Collection */}
      <div className="vault-collection-section">
        <h2 className="section-heading">Digital Vault Inventory ({vaultItems.length})</h2>

        {vaultItems.length === 0 ? (
          <div className="empty-vault-card glass-panel">
            <div className="empty-vault-icon">🏛️</div>
            <h3>Your Vault is Currently Empty</h3>
            <p>Purchase items from the catalog to mint verified certificates of authenticity in your vault.</p>
          </div>
        ) : (
          <div className="vault-items-grid">
            {vaultItems.map((item) => (
              <div key={item.id} className="vault-item-card glass-panel">
                <div className="vault-item-image-box">
                  <img src={item.image} alt={item.title} />
                  <span className="vault-tier-tag">{item.tier}</span>
                </div>

                <div className="vault-item-body">
                  <span className="item-cat-name">{item.categoryName}</span>
                  <h3 className="item-vault-title">{item.title}</h3>
                  <div className="item-value-row">
                    <span className="item-value-label">Current Value</span>
                    <span className="item-value-amount">₹{item.price.toLocaleString('en-IN')}</span>
                  </div>

                  <button
                    className="glass-button-secondary cert-inspect-btn"
                    onClick={() => setSelectedCertItem(item)}
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                    </svg>
                    View Ownership Certificate
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Certificate Modal */}
      {selectedCertItem && (
        <div className="modal-backdrop-glass" onClick={() => setSelectedCertItem(null)}>
          <div className="modal-glass-container certificate-modal-glass" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={() => setSelectedCertItem(null)}>✕</button>

            <div className="certificate-frame-glass">
              <div className="cert-watermark-bg">OWNED OS</div>
              
              <div className="cert-header-gold">
                <span className="cert-seal">🛡️</span>
                <h2>CERTIFICATE OF AUTHENTICITY</h2>
                <span className="cert-subtitle">OFFICIAL IMMUTABLE PROOF OF OWNERSHIP</span>
              </div>

              <div className="cert-item-preview">
                <img src={selectedCertItem.image} alt={selectedCertItem.title} />
                <div className="cert-item-details">
                  <h3>{selectedCertItem.title}</h3>
                  <p>Category: {selectedCertItem.categoryName}</p>
                  <p>Valuation: ₹{selectedCertItem.price.toLocaleString('en-IN')}</p>
                  <p>Rarity Tier: <strong style={{ color: '#00f2fe' }}>{selectedCertItem.tier}</strong></p>
                </div>
              </div>

              <div className="cert-meta-grid">
                <div>
                  <span className="meta-lbl">Certificate ID</span>
                  <span className="meta-val">{selectedCertItem.certificateId || 'CERT-OWNED-2026-X88'}</span>
                </div>
                <div>
                  <span className="meta-lbl">Verification Protocol</span>
                  <span className="meta-val">Owned OS Gateway v2</span>
                </div>
                <div>
                  <span className="meta-lbl">Issued To</span>
                  <span className="meta-val">Verified Collector Profile</span>
                </div>
              </div>

              <div className="cert-signature-row">
                <span className="signature-text">Owned Protocol Authority</span>
                <span className="timestamp-text">Minted: {new Date().toLocaleDateString('en-IN')}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
