import React, { useState } from 'react';

export default function ProductDetailModal({
  product,
  onClose,
  onAddToCart,
  onBuyNow
}) {
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState('specs');

  if (!product) return null;

  return (
    <div className="modal-backdrop-glass" onClick={onClose}>
      <div className="modal-glass-container product-detail-glass" onClick={(e) => e.stopPropagation()}>
        {/* Close Button */}
        <button className="modal-close-btn" onClick={onClose}>✕</button>

        <div className="detail-modal-layout">
          {/* Left Column - Product Graphic */}
          <div className="detail-image-box">
            <img src={product.image} alt={product.title} className="detail-hero-image" />
            <div className="tier-watermark">{product.tier}</div>

            {/* Certificate Preview Badge */}
            <div className="certificate-badge-card">
              <div className="cert-header">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#00f2fe" strokeWidth="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                </svg>
                <span>AUTHENTICITY GUARANTEED</span>
              </div>
              <p className="cert-id">Cert ID: {product.certificateId || 'CERT-OWNED-2026'}</p>
            </div>
          </div>

          {/* Right Column - Product Meta & Purchase */}
          <div className="detail-content-box">
            <div className="detail-header-meta">
              <span className="category-tag">{product.categoryName}</span>
              <span className="stock-status-pill">{product.stock} Units Remaining</span>
            </div>

            <h1 className="detail-title">{product.title}</h1>
            <p className="detail-subtitle">{product.subtitle}</p>

            <div className="detail-rating-row">
              <span className="stars-fill">★ {product.rating}</span>
              <span className="reviews-text">({product.reviewCount} Verified Buyer Reviews)</span>
              <span className="seller-name">Seller: <strong>{product.seller}</strong></span>
            </div>

            {/* Price Row */}
            <div className="detail-price-box">
              <div className="price-big-group">
                <span className="detail-price">₹{product.price.toLocaleString('en-IN')}</span>
                {product.originalPrice && (
                  <span className="detail-original-price">₹{product.originalPrice.toLocaleString('en-IN')}</span>
                )}
              </div>
              {product.discountPercent > 0 && (
                <span className="detail-discount-badge">{product.discountPercent}% Instant Savings</span>
              )}
            </div>

            <p className="detail-description">{product.description}</p>

            {/* Tabs for Specs & Verification */}
            <div className="detail-tab-row">
              <button
                className={`detail-tab-btn ${activeTab === 'specs' ? 'active' : ''}`}
                onClick={() => setActiveTab('specs')}
              >
                Specifications
              </button>
              <button
                className={`detail-tab-btn ${activeTab === 'guarantee' ? 'active' : ''}`}
                onClick={() => setActiveTab('guarantee')}
              >
                Ownership Rights
              </button>
            </div>

            <div className="detail-tab-content">
              {activeTab === 'specs' ? (
                <ul className="specs-list">
                  {product.specs ? product.specs.map((spec, i) => (
                    <li key={i}>✓ {spec}</li>
                  )) : (
                    <li>✓ High-grade materials & precision testing</li>
                  )}
                </ul>
              ) : (
                <div className="guarantee-box">
                  <p>🛡️ Every item purchased on Owned OS mints an immutable Digital Certificate of Authenticity tied to your profile vault.</p>
                </div>
              )}
            </div>

            {/* Quantity Selector */}
            <div className="quantity-selector-row">
              <span className="qty-label">Quantity:</span>
              <div className="qty-picker">
                <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>
                <span>{quantity}</span>
                <button onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}>+</button>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="detail-action-buttons">
              <button
                className="glass-button-secondary flex-1"
                onClick={() => {
                  onAddToCart(product, quantity);
                  onClose();
                }}
              >
                🛒 Add to Cart
              </button>
              <button
                className="glass-button-primary flex-1"
                onClick={() => {
                  onBuyNow(product, quantity);
                  onClose();
                }}
              >
                ⚡ Buy Now with Wallet
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
