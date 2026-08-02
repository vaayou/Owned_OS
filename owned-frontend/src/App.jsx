import React, { useState, useEffect } from 'react';
import './App.css';
import { api, getStoredUser, removeAuthToken } from './services/api';

import Navbar from './components/Navbar';
import HeroBanner from './components/HeroBanner';
import CategoryPillBar from './components/CategoryPillBar';
import ProductGrid from './components/ProductGrid';
import ProductDetailModal from './components/ProductDetailModal';
import CartDrawer from './components/CartDrawer';
import AuthModal from './components/AuthModal';
import WalletModal from './components/WalletModal';
import VaultView from './components/VaultView';
import LeaderboardView from './components/LeaderboardView';
import NotificationToast from './components/NotificationToast';

export default function App() {
  // Navigation & View state
  const [activeTab, setActiveTab] = useState('catalog'); // 'catalog' | 'vault' | 'leaderboard'

  // Data state
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [leaderboard, setLeaderboard] = useState([]);
  const [vaultItems, setVaultItems] = useState([]);
  const [netWorthData, setNetWorthData] = useState({});

  // Filter state
  const [selectedCategory, setSelectedCategory] = useState(0);
  const [selectedTier, setSelectedTier] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  // Cart & Wishlist state
  const [cartItems, setCartItems] = useState([]);
  const [wishlistIds, setWishlistIds] = useState([]);

  // Modals state
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [isWalletOpen, setIsWalletOpen] = useState(false);
  const [selectedProductDetail, setSelectedProductDetail] = useState(null);

  // User & Wallet state
  const [user, setUser] = useState(getStoredUser());
  const [walletBalance, setWalletBalance] = useState(0);

  // Toast Notification state
  const [toast, setToast] = useState({ message: '', type: 'info' });

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
  };

  // Initial Load - Strictly from Backend
  useEffect(() => {
    loadCategories();
    loadWallet();
    loadVault();
    loadLeaderboard();
  }, []);

  // Reload products whenever filters change
  useEffect(() => {
    loadProducts();
  }, [selectedCategory, selectedTier, searchQuery]);

  const loadCategories = async () => {
    try {
      const cats = await api.getCategories();
      setCategories(cats);
    } catch (err) {
      console.error('Failed to fetch categories from backend:', err);
      showToast('Could not connect to Backend Category Service at http://localhost:8080/api/categories', 'warning');
    }
  };

  const loadProducts = async () => {
    setLoadingProducts(true);
    try {
      const data = await api.getProducts({
        categoryId: selectedCategory,
        tier: selectedTier,
        search: searchQuery
      });
      setProducts(data);
    } catch (err) {
      console.error('Failed to fetch products from backend:', err);
      setProducts([]);
      showToast('Could not fetch products from Gateway (http://localhost:8080/api/products)', 'warning');
    } finally {
      setLoadingProducts(false);
    }
  };

  const loadWallet = async () => {
    try {
      const w = await api.getWallet();
      if (w && w.balanceInr !== undefined) {
        setWalletBalance(w.balanceInr);
      }
    } catch (err) {
      console.warn('Wallet Service offline or unauthenticated');
    }
  };

  const loadVault = async () => {
    try {
      const items = await api.getVaultCollection();
      const net = await api.getNetWorth();
      setVaultItems(items);
      setNetWorthData(net);
    } catch (err) {
      console.warn('Collection Service offline or unauthenticated');
    }
  };

  const loadLeaderboard = async () => {
    try {
      const lb = await api.getLeaderboard();
      setLeaderboard(lb);
    } catch (err) {
      console.warn('Leaderboard Service offline');
    }
  };

  // Cart Handlers
  const handleAddToCart = (product, qty = 1) => {
    setCartItems(prev => {
      const existing = prev.find(item => item.id === product.id);
      if (existing) {
        return prev.map(item =>
          item.id === product.id ? { ...item, quantity: item.quantity + qty } : item
        );
      }
      return [...prev, { ...product, quantity: qty }];
    });
    showToast(`Added "${product.title}" to cart! 🛍️`, 'success');
  };

  const handleUpdateQuantity = (id, newQty) => {
    if (newQty <= 0) {
      handleRemoveFromCart(id);
      return;
    }
    setCartItems(prev => prev.map(item => item.id === id ? { ...item, quantity: newQty } : item));
  };

  const handleRemoveFromCart = (id) => {
    setCartItems(prev => prev.filter(item => item.id !== id));
    showToast('Item removed from cart.', 'info');
  };

  const handleToggleWishlist = (product) => {
    setWishlistIds(prev => {
      const exists = prev.includes(product.id);
      if (exists) {
        showToast(`Removed "${product.title}" from wishlist.`, 'info');
        return prev.filter(id => id !== product.id);
      } else {
        showToast(`Added "${product.title}" to wishlist! ❤️`, 'success');
        return [...prev, product.id];
      }
    });
  };

  // Checkout Handler (Strict Backend POST /api/orders)
  const handleCheckout = async () => {
    const total = cartItems.reduce((acc, i) => acc + (i.price * i.quantity), 0);
    if (walletBalance < total) {
      showToast('Insufficient wallet balance. Please top up your wallet.', 'warning');
      setIsWalletOpen(true);
      return;
    }

    try {
      const res = await api.checkout(cartItems);
      if (res) {
        setCartItems([]);
        setIsCartOpen(false);
        showToast(`🎉 Order Placed on Backend! Order ID: ${res.orderId || res.id}`, 'success');
        loadWallet();
        loadVault();
        setActiveTab('vault');
      }
    } catch (err) {
      showToast(`Checkout Failed on Backend: ${err.message}`, 'warning');
    }
  };

  // Wallet Topup Handler (Strict Backend POST /api/wallet/topup)
  const handleTopupWallet = async (amount) => {
    try {
      const res = await api.topupWallet(amount);
      if (res) {
        showToast(`⚡ Top-up request sent to Backend for ₹${amount.toLocaleString('en-IN')}!`, 'success');
        loadWallet();
      }
    } catch (err) {
      showToast(`Wallet Top-up Failed on Backend: ${err.message}`, 'warning');
    }
  };

  // Auth Handlers (Strict Backend POST /api/auth/*)
  const handleAuthSuccess = async (mode, credentials) => {
    try {
      let res;
      if (mode === 'login') {
        res = await api.login(credentials.username, credentials.password);
      } else {
        res = await api.register(credentials.username, credentials.email, credentials.password);
      }

      if (res && res.user) {
        setUser(res.user);
        showToast(`Welcome ${res.user.username}! Connected to Backend API.`, 'success');
        loadWallet();
        loadVault();
      }
    } catch (err) {
      showToast(`Authentication Error: ${err.message}`, 'warning');
      throw err;
    }
  };

  const handleLogout = () => {
    removeAuthToken();
    localStorage.removeItem('owned_user');
    setUser(null);
    setWalletBalance(0);
    setVaultItems([]);
    showToast('Signed out of Owned OS session.', 'info');
    setIsAuthOpen(false);
  };

  const handleSelectBannerProduct = (bannerId) => {
    const prod = products.find(p => p.id === 101) || products[0];
    if (prod) setSelectedProductDetail(prod);
  };

  return (
    <div className="app-container">
      {/* Glass Navigation Header */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        cartCount={cartItems.reduce((acc, i) => acc + i.quantity, 0)}
        wishlistCount={wishlistIds.length}
        onOpenCart={() => setIsCartOpen(true)}
        onOpenWallet={() => setIsWalletOpen(true)}
        onOpenAuth={() => setIsAuthOpen(true)}
        walletBalance={walletBalance}
        user={user}
      />

      {/* Main Container */}
      <main className="app-main-content">
        {activeTab === 'catalog' && (
          <>
            {/* Dynamic Glass Hero Banner */}
            <HeroBanner onSelectBannerProduct={handleSelectBannerProduct} />

            {/* Category & Rarity Tier Pills */}
            <CategoryPillBar
              categories={categories}
              selectedCategory={selectedCategory}
              onSelectCategory={setSelectedCategory}
              selectedTier={selectedTier}
              onSelectTier={setSelectedTier}
            />

            {/* Product Catalog Grid */}
            <ProductGrid
              products={products}
              loading={loadingProducts}
              onOpenDetail={setSelectedProductDetail}
              onAddToCart={handleAddToCart}
              onToggleWishlist={handleToggleWishlist}
              wishlistIds={wishlistIds}
            />
          </>
        )}

        {activeTab === 'vault' && (
          <VaultView
            vaultItems={vaultItems}
            netWorthData={netWorthData}
          />
        )}

        {activeTab === 'leaderboard' && (
          <LeaderboardView leaderboard={leaderboard} />
        )}
      </main>

      {/* Product Detail Modal */}
      {selectedProductDetail && (
        <ProductDetailModal
          product={selectedProductDetail}
          onClose={() => setSelectedProductDetail(null)}
          onAddToCart={handleAddToCart}
          onBuyNow={(prod, qty) => {
            handleAddToCart(prod, qty);
            setIsCartOpen(true);
          }}
        />
      )}

      {/* Cart Drawer */}
      <CartDrawer
        isOpen={isCartOpen}
        onClose={() => setIsCartOpen(false)}
        cartItems={cartItems}
        onUpdateQuantity={handleUpdateQuantity}
        onRemoveItem={handleRemoveFromCart}
        onCheckout={handleCheckout}
        walletBalance={walletBalance}
      />

      {/* Auth Modal */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onAuthSuccess={handleAuthSuccess}
        user={user}
        onLogout={handleLogout}
      />

      {/* Wallet Modal */}
      <WalletModal
        isOpen={isWalletOpen}
        onClose={() => setIsWalletOpen(false)}
        walletBalance={walletBalance}
        onTopup={handleTopupWallet}
      />

      {/* Floating Glass Toast Notification */}
      <NotificationToast
        message={toast.message}
        type={toast.type}
        onClose={() => setToast({ message: '', type: 'info' })}
      />
    </div>
  );
}
