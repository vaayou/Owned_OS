import React from 'react';
import ProductCard from './ProductCard';

export default function ProductGrid({
  products,
  loading,
  onOpenDetail,
  onAddToCart,
  onToggleWishlist,
  wishlistIds
}) {
  if (loading) {
    return (
      <div className="product-grid-loading">
        <div className="glass-spinner"></div>
        <p>Loading Luxury Vault Items...</p>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="empty-catalog-glass">
        <div className="empty-icon">💎</div>
        <h2>No Items Found</h2>
        <p>Try resetting your category or search query to explore more collectibles.</p>
      </div>
    );
  }

  return (
    <section className="product-grid-section">
      <div className="grid-header-meta">
        <h2 className="section-heading">Featured Vault Items</h2>
        <span className="results-count-pill">{products.length} Products Available</span>
      </div>

      <div className="product-cards-grid">
        {products.map((product, idx) => (
          <ProductCard
            key={product.id}
            index={idx}
            product={product}
            onOpenDetail={onOpenDetail}
            onAddToCart={onAddToCart}
            onToggleWishlist={onToggleWishlist}
            isWishlisted={wishlistIds.includes(product.id)}
          />
        ))}
      </div>
    </section>
  );
}
