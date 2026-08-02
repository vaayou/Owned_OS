import React from 'react';

export default function CartDrawer({
  isOpen,
  onClose,
  cartItems,
  onUpdateQuantity,
  onRemoveItem,
  onCheckout,
  walletBalance
}) {
  if (!isOpen) return null;

  const totalAmount = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const totalOriginal = cartItems.reduce((sum, item) => sum + ((item.originalPrice || item.price) * item.quantity), 0);
  const savings = totalOriginal - totalAmount;

  return (
    <div className="cart-backdrop-glass" onClick={onClose}>
      <div className="cart-drawer-glass" onClick={(e) => e.stopPropagation()}>
        {/* Drawer Header */}
        <div className="drawer-header">
          <div className="drawer-title-group">
            <h2>Your Order Bag</h2>
            <span className="items-count-badge">{cartItems.length} Items</span>
          </div>
          <button className="drawer-close-btn" onClick={onClose}>✕</button>
        </div>

        {/* Cart Items List */}
        <div className="cart-items-scroll">
          {cartItems.length === 0 ? (
            <div className="empty-cart-view">
              <div className="empty-bag-icon">🛍️</div>
              <h3>Your cart is empty</h3>
              <p>Discover luxury products and digital collectibles to start collecting.</p>
            </div>
          ) : (
            cartItems.map((item) => (
              <div key={item.id} className="cart-item-card-glass">
                <img src={item.image} alt={item.title} className="cart-item-thumb" />
                
                <div className="cart-item-details">
                  <span className="cart-tier-tag">{item.tier}</span>
                  <h4 className="cart-item-title">{item.title}</h4>
                  
                  <div className="cart-item-price-row">
                    <span className="cart-price">₹{item.price.toLocaleString('en-IN')}</span>
                    {item.originalPrice && (
                      <span className="cart-orig-price">₹{item.originalPrice.toLocaleString('en-IN')}</span>
                    )}
                  </div>

                  {/* Quantity Controller */}
                  <div className="cart-qty-row">
                    <div className="mini-qty-picker">
                      <button onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}>-</button>
                      <span>{item.quantity}</span>
                      <button onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}>+</button>
                    </div>

                    <button className="remove-item-btn" onClick={() => onRemoveItem(item.id)}>
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer Summary & Checkout */}
        {cartItems.length > 0 && (
          <div className="cart-drawer-footer">
            <div className="summary-row">
              <span>Subtotal</span>
              <span>₹{totalOriginal.toLocaleString('en-IN')}</span>
            </div>
            {savings > 0 && (
              <div className="summary-row discount">
                <span>Discount Savings</span>
                <span>-₹{savings.toLocaleString('en-IN')}</span>
              </div>
            )}
            <div className="summary-row">
              <span>Verified Certificate Minting</span>
              <span className="free-badge">FREE</span>
            </div>
            <div className="summary-divider"></div>
            <div className="summary-row total-row">
              <span>Total Payable</span>
              <span className="total-price">₹{totalAmount.toLocaleString('en-IN')}</span>
            </div>

            {/* Wallet Balance Verification Indicator */}
            <div className="wallet-check-box">
              <span className="wallet-check-text">Wallet Balance: ₹{walletBalance.toLocaleString('en-IN')}</span>
              {walletBalance >= totalAmount ? (
                <span className="sufficient-pill">✓ Sufficient Funds</span>
              ) : (
                <span className="insufficient-pill">⚠️ Top-up Required</span>
              )}
            </div>

            <button
              className="glass-button-primary checkout-full-btn"
              onClick={onCheckout}
            >
              Checkout & Mint Ownership Certificate
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
