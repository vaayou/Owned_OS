import React from 'react';

export default function Navbar({
  activeTab,
  setActiveTab,
  searchQuery,
  setSearchQuery,
  cartCount,
  wishlistCount,
  onOpenCart,
  onOpenWallet,
  onOpenAuth,
  walletBalance,
  user
}) {
  return (
    <header className="navbar-container">
      {/* Top Glass Bar */}
      <nav className="navbar-glass">
        {/* Brand Logo */}
        <div className="brand-logo" onClick={() => setActiveTab('catalog')}>
          <div className="logo-icon-glass">
            <span className="logo-sparkle">❖</span>
          </div>
          <div className="logo-text">
            <span className="brand-title">OWNED</span>
            <span className="brand-badge">LUXURY OS</span>
          </div>
        </div>

        {/* Search Bar - Amazon/Flipkart style with glass aesthetic */}
        <div className="navbar-search-wrapper">
          <svg className="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input
            type="text"
            className="glass-input search-input"
            placeholder="Search luxury watches, sneakers, tech & digital vault collectibles..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          {searchQuery && (
            <button className="clear-search-btn" onClick={() => setSearchQuery('')}>✕</button>
          )}
        </div>

        {/* Main Nav Tabs */}
        <div className="nav-tabs">
          <button
            className={`nav-tab-btn ${activeTab === 'catalog' ? 'active' : ''}`}
            onClick={() => setActiveTab('catalog')}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="7" height="7"></rect>
              <rect x="14" y="3" width="7" height="7"></rect>
              <rect x="14" y="14" width="7" height="7"></rect>
              <rect x="3" y="14" width="7" height="7"></rect>
            </svg>
            Catalog
          </button>

          <button
            className={`nav-tab-btn ${activeTab === 'vault' ? 'active' : ''}`}
            onClick={() => setActiveTab('vault')}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
            My Vault
          </button>

          <button
            className={`nav-tab-btn ${activeTab === 'leaderboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('leaderboard')}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
            Leaderboard
          </button>
        </div>

        {/* Action Widgets */}
        <div className="nav-actions">
          {/* Wallet Balance Badge */}
          <button className="wallet-badge-btn" onClick={onOpenWallet} title="Manage Wallet Balance">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="4" width="20" height="16" rx="2"></rect>
              <line x1="6" y1="8" x2="6" y2="8.01"></line>
              <line x1="10" y1="8" x2="18" y2="8"></line>
            </svg>
            <span className="wallet-amount">₹{walletBalance.toLocaleString('en-IN')}</span>
            <span className="plus-icon">+</span>
          </button>

          {/* Wishlist Button */}
          <div className="nav-icon-btn" title="Wishlist">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
            {wishlistCount > 0 && <span className="action-badge">{wishlistCount}</span>}
          </div>

          {/* Cart Button */}
          <button className="nav-icon-btn cart-btn-glass" onClick={onOpenCart} title="View Cart">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            {cartCount > 0 && <span className="action-badge glowing">{cartCount}</span>}
          </button>

          {/* User Account / Profile */}
          <button className="user-profile-btn" onClick={onOpenAuth}>
            <div className="user-avatar-glass">
              {user ? user.username.charAt(0).toUpperCase() : '👤'}
            </div>
            <span className="username-text">{user ? user.username : 'Sign In'}</span>
          </button>
        </div>
      </nav>
    </header>
  );
}
