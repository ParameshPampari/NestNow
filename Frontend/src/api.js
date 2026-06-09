const BASE_URL = 'http://localhost:8080';

async function request(path, options = {}) {
  const token = localStorage.getItem('token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers
  };

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    let errorMsg = 'An error occurred';
    try {
      const data = await response.json();
      errorMsg = data.message || errorMsg;
    } catch (e) {
      try {
        errorMsg = await response.text();
      } catch (e2) {}
    }
    throw new Error(errorMsg);
  }

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  // Authentication
  auth: {
    login: (credentials) => request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    }),
    register: (details) => request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(details)
    }),
    getCurrentUser: () => request('/api/users/me')
  },

  // Categories
  categories: {
    getAll: () => request('/api/categories'),
    create: (data) => request('/api/categories/create', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },

  // Services
  services: {
    getAll: () => request('/api/services'),
    search: (query) => request(`/api/services/search?q=${encodeURIComponent(query)}`),
    getByCategory: (categoryId) => request(`/api/services/category/${categoryId}`),
    create: (data) => request('/api/services/create', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },

  // Professional Profiles
  professionals: {
    createProfile: (profileData) => request('/api/professionals/profile', {
      method: 'POST',
      body: JSON.stringify(profileData)
    }),
    getMyProfile: () => request('/api/professionals/profile'),
    getVerified: () => request('/api/professionals'),
    getById: (id) => request(`/api/professionals/${id}`)
  },

  // Bookings
  bookings: {
    create: (bookingData) => request('/api/bookings', {
      method: 'POST',
      body: JSON.stringify(bookingData)
    }),
    getMy: () => request('/api/bookings/my'),
    getProfessional: () => request('/api/bookings/professional'),
    updateStatus: (bookingId, statusData) => request(`/api/bookings/${bookingId}/status`, {
      method: 'PUT',
      body: JSON.stringify(statusData)
    })
  },

  // Payments
  payments: {
    create: (paymentData) => request('/api/payments', {
      method: 'POST',
      body: JSON.stringify(paymentData)
    }),
    getForBooking: (bookingId) => request(`/api/payments/booking/${bookingId}`)
  },

  // Reviews
  reviews: {
    create: (reviewData) => request('/api/reviews', {
      method: 'POST',
      body: JSON.stringify(reviewData)
    }),
    getForProfessional: (professionalId) => request(`/api/reviews/professional/${professionalId}`),
    flag: (reviewId) => request(`/api/reviews/${reviewId}/flag`, {
      method: 'PUT'
    })
  },

  // Subscriptions
  subscriptions: {
    subscribe: (subData) => request('/api/subscriptions', {
      method: 'POST',
      body: JSON.stringify(subData)
    }),
    getMy: () => request('/api/subscriptions/my'),
    cancel: (id) => request(`/api/subscriptions/${id}/cancel`, {
      method: 'PUT'
    })
  },

  // Addresses
  addresses: {
    create: (addressData) => request('/api/addresses', {
      method: 'POST',
      body: JSON.stringify(addressData)
    }),
    getMy: () => request('/api/addresses')
  },

  // Admin Dashboard
  admin: {
    getDashboard: () => request('/api/admin/dashboard'),
    getUsers: () => request('/api/admin/users'),
    getBookings: () => request('/api/admin/bookings'),
    verifyProfessional: (id, data) => request(`/api/admin/professionals/${id}/verify`, {
      method: 'PUT',
      body: JSON.stringify(data)
    })
  }
};
