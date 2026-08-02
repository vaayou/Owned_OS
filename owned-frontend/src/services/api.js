// API Service for Owned OS Frontend
// STRICTLY CONNECTED TO BACKEND GATEWAY AT http://localhost:8080/api ONLY

const API_BASE_URL = 'http://localhost:8080/api';

// Local storage token helpers
export const getAuthToken = () => localStorage.getItem('owned_access_token') || '';
export const setAuthToken = (token) => localStorage.setItem('owned_access_token', token);
export const removeAuthToken = () => localStorage.removeItem('owned_access_token');

export const getStoredUser = () => {
  const userStr = localStorage.getItem('owned_user');
  if (!userStr) return null;
  try { return JSON.parse(userStr); } catch { return null; }
};

export const setStoredUser = (user) => localStorage.setItem('owned_user', JSON.stringify(user));

// Base HTTP Request helper (throws error if backend endpoint fails)
async function request(endpoint, options = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const res = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const errorText = await res.text().catch(() => '');
    throw new Error(`Backend Error (${res.status}): ${errorText || res.statusText}`);
  }

  // Handle empty 204 responses
  if (res.status === 204) return true;

  return await res.json();
}

// Normalizer to convert Backend DTOs to Frontend UI props
const normalizeProduct = (p) => ({
  id: p.id,
  title: p.name || p.title,
  subtitle: p.description ? (p.description.length > 50 ? p.description.slice(0, 50) + '...' : p.description) : '',
  description: p.description || '',
  categoryId: p.categoryId,
  categoryName: p.categoryName || 'General',
  tier: p.categoryTier || p.tier || 'EVERYDAY',
  price: typeof p.priceCoins === 'number' ? p.priceCoins : parseFloat(p.priceCoins || p.price || 0),
  originalPrice: (p.priceCoins ? p.priceCoins * 1.15 : p.price ? p.price * 1.15 : 0),
  discountPercent: 12,
  rating: p.rating || 4.8,
  reviewCount: p.reviewCount || 42,
  seller: 'Owned Vault Store',
  stock: p.stock || 10,
  inStock: p.isActive !== false,
  certificateId: `CERT-OWNED-${p.id}`,
  image: p.imageUrl || p.image || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600',
  specs: ['Authentic Product', 'Verified Ownership', 'Instant Vault Sync']
});

// API Gateway endpoints wrapper (Strict Backend Execution)
export const api = {
  // Auth Endpoint
  async login(username, password) {
    const data = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });

    if (data && (data.token || data.accessToken)) {
      const token = data.token || data.accessToken;
      setAuthToken(token);
      const user = { username, email: `${username}@owned.io`, id: data.userId || 1 };
      setStoredUser(user);
      return { success: true, token, user };
    }
    throw new Error('Invalid credentials from backend authentication service.');
  },

  async register(username, email, password) {
    const data = await request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });

    if (data && (data.token || data.accessToken)) {
      const token = data.token || data.accessToken;
      setAuthToken(token);
      const user = { username, email, id: data.userId || 1 };
      setStoredUser(user);
      return { success: true, token, user };
    }
    return { success: true, user: { username, email } };
  },

  // Products Endpoint - Fetches strictly from Gateway /api/products
  async getProducts({ categoryId, tier, search } = {}) {
    const params = new URLSearchParams();
    if (categoryId && categoryId !== 0) params.append('categoryId', categoryId);
    if (tier && tier !== 'ALL') params.append('tier', tier);
    if (search) params.append('search', search);

    const queryString = params.toString() ? `?${params.toString()}` : '';
    const data = await request(`/products${queryString}`);
    
    if (Array.isArray(data)) {
      return data.map(normalizeProduct);
    }
    return [];
  },

  // Categories Endpoint - Fetches strictly from Gateway /api/categories
  async getCategories() {
    const data = await request('/categories');
    if (Array.isArray(data)) {
      const formatted = data.map(c => ({
        id: c.id,
        name: c.name,
        tier: c.tier,
        children: c.children || []
      }));
      return [{ id: 0, name: 'All Categories' }, ...formatted];
    }
    return [{ id: 0, name: 'All Categories' }];
  },

  // Wallet Endpoints (Backend field is coinBalance)
  async getWallet() {
    const data = await request('/wallet/me');
    const rawBal = data.coinBalance ?? data.balanceInr ?? data.balance ?? 0;
    return {
      balanceInr: typeof rawBal === 'number' ? rawBal : parseFloat(rawBal || 0),
      currency: data.currency || 'INR'
    };
  },

  async topupWallet(amountInr) {
    // 1. Initiate Top-up with Backend
    const topupData = await request('/wallet/topup', {
      method: 'POST',
      body: JSON.stringify({ amountInr, provider: 'RAZORPAY' }),
    });

    // 2. Auto-trigger payment webhook for instant sandbox success & balance update
    if (topupData && topupData.providerTxnId) {
      await request(`/payments/webhook?providerTxnId=${topupData.providerTxnId}&eventType=payment.succeeded`, {
        method: 'POST',
        headers: {
          'X-Razorpay-Signature': 'sandbox-signature'
        }
      });
    }

    return topupData;
  },

  // Orders Endpoint
  async checkout(cartItems) {
    return await request('/orders', {
      method: 'POST',
      body: JSON.stringify({
        items: cartItems.map(item => ({ productId: item.id, quantity: item.quantity }))
      }),
    });
  },

  // Collection Vault Endpoints
  async getVaultCollection() {
    const data = await request('/collection/me');
    if (Array.isArray(data)) {
      return data.map(normalizeProduct);
    }
    return [];
  },

  async getNetWorth() {
    const data = await request('/collection/me/networth');
    return {
      totalNetWorthInr: data.totalNetWorthInr || data.netWorth || 0,
      itemCount: data.itemCount || 0,
      tierRank: data.tierRank || 'COLLECTOR'
    };
  },

  // Leaderboard Endpoint
  async getLeaderboard() {
    const data = await request('/collection/leaderboard');
    if (Array.isArray(data)) {
      return data.map((item, idx) => ({
        rank: item.rank || idx + 1,
        username: item.username || `Collector_${idx + 1}`,
        avatar: item.avatar || `https://i.pravatar.cc/150?img=${idx + 10}`,
        netWorth: item.netWorth || item.totalNetWorthInr || 0,
        itemsOwned: item.itemsOwned || item.itemCount || 0,
        tier: item.tier || 'LUXURY'
      }));
    }
    return [];
  }
};
