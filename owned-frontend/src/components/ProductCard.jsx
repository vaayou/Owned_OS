import React from 'react';

export default function ProductCard({
  product,
  onOpenDetail,
  onAddToCart,
  onToggleWishlist,
  isWishlisted
}) {
  const getTierClass = (tier) => {
    switch (tier) {
      case 'ULTRA': return 'tier-ultra-badge';
      case 'LUXURY': return 'tier-luxury-badge';
      default: return 'tier-everyday-badge';
    }
  };

  return (
    <div className="product-glass-card">
      {/* Product Image Container */}
      <div className="product-image-frame" onClick={() => onOpenDetail(product)}>
        <img src={product.image} alt={product.title} className="product-image" />

        {/* Tier Rarity Badge */}
        <span className={`product-tier-tag ${getTierClass(product.tier)}`}>
          {product.tier}
        </span>

        {/* Wishlist Floating Button */}
        <button
          className={`wishlist-float-btn ${isWishlisted ? 'active' : ''}`}
          onClick={(e) => {
            e.stopPropagation();
            onToggleWishlist(product);
          }}
          title={isWishlisted ? 'Remove from Wishlist' : 'Add to Wishlist'}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill={isWishlisted ? '#ff007f' : 'none'} stroke={isWishlisted ? '#ff007f' : 'currentColor'} strokeWidth="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
          </svg>
        </button>

        {/* Quick View Glass Overlay */}
        <div className="quick-view-overlay">
          <span>Quick Inspect 🔍</span>
        </div>
      </div>

      {/* Product Info Section */}
      <div className="product-info-body" onClick={() => onOpenDetail(product)}>
        <div className="category-meta-row">
          <span className="product-category-text">{product.categoryName}</span>
          <div className="product-rating-badge">
            <span>★ {product.rating}</span>
            <span className="review-count">({product.reviewCount})</span>
          </div>
        </div>

        <h3 className="product-title">{product.title}</h3>
        <p className="product-subtitle">{product.subtitle}</p>

        {/* Certificate Verification Icon */}
        {product.certificateId && (
          <div className="certificate-pill">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
            <span>Verified Vault Certificate</span>
          </div>
        )}

        {/* Pricing Row */}
        <div className="product-price-row">
          <div className="price-stack">
            <span className="product-price">₹{product.price.toLocaleString('en-IN')}</span>
            {product.originalPrice && (
              <span className="product-original-price">₹{product.originalPrice.toLocaleString('en-IN')}</span>
            )}
          </div>
          {product.discountPercent > 0 && (
            <span className="discount-badge">{product.discountPercent}% OFF</span>
          )}
        </div>

        {/* Action Button */}
        <button
          className="glass-button-primary add-to-cart-btn"
          onClick={(e) => {
            e.stopPropagation();
            onAddToCart(product);
          }}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Add to Cart
        </button>
      </div>
    </div>
  );
}
