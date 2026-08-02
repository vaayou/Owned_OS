import React from 'react';

export default function CategoryPillBar({
  categories,
  selectedCategory,
  onSelectCategory,
  selectedTier,
  onSelectTier
}) {
  const TIERS = [
    { id: 'ALL', label: 'All Tiers' },
    { id: 'EVERYDAY', label: '⚡ Everyday Essentials' },
    { id: 'LUXURY', label: '💎 Luxury Vault' },
    { id: 'ULTRA', label: '🔥 Ultra Collector' },
  ];

  return (
    <div className="category-bar-wrapper">
      {/* Category Pills */}
      <div className="category-pills-row">
        {categories.map((cat) => {
          const isActive = selectedCategory === cat.id;
          return (
            <button
              key={cat.id}
              className={`glass-pill ${isActive ? 'active' : ''}`}
              onClick={() => onSelectCategory(cat.id)}
            >
              <span>{cat.name}</span>
            </button>
          );
        })}
      </div>

      {/* Tier Filter Selector */}
      <div className="tier-filter-row">
        <span className="tier-filter-label">Rarity Tier:</span>
        <div className="tier-pills-group">
          {TIERS.map((t) => (
            <button
              key={t.id}
              className={`tier-badge-btn ${selectedTier === t.id ? 'active' : ''}`}
              onClick={() => onSelectTier(t.id)}
            >
              {t.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
