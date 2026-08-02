import React, { useState, useEffect } from 'react';

const BANNERS = [
  {
    id: 1,
    tag: 'ULTRA TIER DROP',
    title: 'AeroPulse Tourbillon Titanium',
    description: 'Only 50 units crafted worldwide. Verified with digital certificate of authenticity on Owned OS.',
    discount: '12% OFF • FLASHSALE',
    price: '₹4,85,000',
    originalPrice: '₹5,50,000',
    image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=1000&auto=format&fit=crop&q=80',
    accentColor: '#9d4edd'
  },
  {
    id: 2,
    tag: 'MYNTRA LUXURY STYLE',
    title: 'Genesis Solitaire 2.5 Carat Ring',
    description: 'Architectural white gold floating solitaire diamond ring. Pure luxury redefined.',
    discount: 'VIP DIRECT ACCESS',
    price: '₹8,90,000',
    originalPrice: '₹9,50,000',
    image: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=1000&auto=format&fit=crop&q=80',
    accentColor: '#ffd700'
  },
  {
    id: 3,
    tag: 'FLIPKART BIG DEAL DAY',
    title: 'QuantumBass Studio Pro Noise Cancelling',
    description: 'Planar magnetic spatial audio with transparent glass touch surface.',
    discount: 'FLAT ₹5,000 INSTANT CASHBACK',
    price: '₹29,999',
    originalPrice: '₹34,999',
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1000&auto=format&fit=crop&q=80',
    accentColor: '#00f2fe'
  }
];

export default function HeroBanner({ onSelectBannerProduct }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % BANNERS.length);
    }, 6000);
    return () => clearInterval(timer);
  }, []);

  const banner = BANNERS[currentIndex];

  return (
    <section className="hero-banner-container">
      <div
        className="hero-glass-card"
        style={{
          backgroundImage: `linear-gradient(90deg, rgba(10, 12, 20, 0.92) 0%, rgba(10, 12, 20, 0.65) 50%, rgba(10, 12, 20, 0.2) 100%), url(${banner.image})`
        }}
      >
        <div className="hero-content">
          <div className="hero-badge-pill" style={{ borderColor: banner.accentColor, color: banner.accentColor }}>
            <span className="pulse-dot" style={{ backgroundColor: banner.accentColor }}></span>
            {banner.tag}
          </div>

          <h1 className="hero-title">{banner.title}</h1>
          <p className="hero-description">{banner.description}</p>

          <div className="hero-price-row">
            <div className="price-tag-group">
              <span className="current-hero-price">{banner.price}</span>
              <span className="original-hero-price">{banner.originalPrice}</span>
            </div>
            <span className="hero-discount-pill">{banner.discount}</span>
          </div>

          <div className="hero-cta-buttons">
            <button className="glass-button-primary" onClick={() => onSelectBannerProduct(banner.id)}>
              Explore Vault Item
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="5" y1="12" x2="19" y2="12"></line>
                <polyline points="12 5 19 12 12 19"></polyline>
              </svg>
            </button>
          </div>
        </div>

        {/* Carousel Slide Indicators */}
        <div className="hero-indicators">
          {BANNERS.map((b, idx) => (
            <button
              key={b.id}
              className={`indicator-dot ${idx === currentIndex ? 'active' : ''}`}
              onClick={() => setCurrentIndex(idx)}
            ></button>
          ))}
        </div>
      </div>
    </section>
  );
}
