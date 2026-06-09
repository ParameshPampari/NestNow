import React, { useState, useEffect, useCallback } from 'react';
import { 
  Search, Wrench, Shield, Star, User, MapPin, CreditCard, Calendar, 
  LogOut, Lock, Mail, Phone, Plus, CheckCircle, AlertCircle, 
  Briefcase, Clock, DollarSign, Layers, X, Award, Trash, Filter, ShieldAlert
} from 'lucide-react';
import { api } from './api';

export default function App() {
  // --- UI Notifications ---
  const [toast, setToast] = useState(null);
  const showToast = useCallback((message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  }, []);

  // --- Auth State ---
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [user, setUser] = useState(null);
  const [userRole, setUserRole] = useState(localStorage.getItem('userRole') || '');
  const [view, setView] = useState('landing'); // 'landing', 'login', 'register', 'dashboard'

  // --- Dashboard Tab State ---
  // homeowner: 'marketplace', 'bookings', 'addresses'
  // professional: 'bookings', 'profile', 'subscription'
  // admin: 'overview', 'users', 'bookings', 'categories', 'services'
  const [dashTab, setDashTab] = useState('');

  // --- Catalog & Search State ---
  const [categories, setCategories] = useState([]);
  const [services, setServices] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  // --- Form Inputs ---
  const [loginForm, setLoginForm] = useState({ email: '', password: '' });
  const [regForm, setRegForm] = useState({ fullName: '', email: '', phone: '', password: '', role: 'HOMEOWNER' });
  const [addressForm, setAddressForm] = useState({ label: 'Home', line1: '', line2: '', city: '', state: '', pincode: '', latitude: 0.0, longitude: 0.0 });
  const [profileForm, setProfileForm] = useState({ bio: '', experienceYears: 1, serviceArea: '', profileImageUrl: '' });
  const [bookingForm, setBookingForm] = useState({ serviceId: null, addressId: '', scheduledAt: '', instantBooking: true, customerNotes: '' });
  const [paymentForm, setPaymentForm] = useState({ bookingId: null, amount: 0, paymentMethod: 'CREDIT_CARD' });
  const [reviewForm, setReviewForm] = useState({ bookingId: null, rating: 5, comment: '' });
  
  // --- Admin Form Inputs ---
  const [newCatForm, setNewCatForm] = useState({ name: '', description: '' });
  const [newServiceForm, setNewServiceForm] = useState({ title: '', description: '', price: '', duration: '', categoryId: '' });

  // --- Data Lists ---
  const [myAddresses, setMyAddresses] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [professionalBookings, setProfessionalBookings] = useState([]);
  const [professionalProfile, setProfessionalProfile] = useState(null);
  const [verifiedProfessionals, setVerifiedProfessionals] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  
  // --- Admin Lists/Stats ---
  const [adminStats, setAdminStats] = useState(null);
  const [adminUsers, setAdminUsers] = useState([]);
  const [adminBookings, setAdminBookings] = useState([]);

  // --- Modals Toggle ---
  const [modalType, setModalType] = useState(null); // 'address', 'booking', 'payment', 'review', 'profile'
  const [selectedBooking, setSelectedBooking] = useState(null);

  // --- Fetch Core Catalog Data ---
  const fetchCatalog = useCallback(async () => {
    try {
      const cats = await api.categories.getAll();
      setCategories(cats);
      const servs = await api.services.getAll();
      setServices(servs);
    } catch (err) {
      showToast(err.message, 'error');
    }
  }, [showToast]);

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      const results = await api.services.search(searchQuery);
      setServices(results);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleCategorySelect = async (cat) => {
    setSelectedCategory(cat);
    try {
      if (cat === null) {
        const servs = await api.services.getAll();
        setServices(servs);
      } else {
        const servs = await api.services.getByCategory(cat.id);
        setServices(servs);
      }
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // --- Fetch User Profile & Data based on login ---
  const loadUserData = useCallback(async () => {
    if (!token) return;
    try {
      const me = await api.auth.getCurrentUser();
      setUser(me);
      
      if (me.role === 'HOMEOWNER') {
        const addrs = await api.addresses.getMy();
        setMyAddresses(addrs);
        const bks = await api.bookings.getMy();
        setMyBookings(bks);
        setDashTab('marketplace');
      } else if (me.role === 'PROFESSIONAL') {
        try {
          const profile = await api.professionals.getMyProfile();
          setProfessionalProfile(profile);
          setProfileForm({
            bio: profile.bio || '',
            experienceYears: profile.experienceYears || 1,
            serviceArea: profile.serviceArea || '',
            profileImageUrl: profile.profileImageUrl || ''
          });
        } catch (e) {
          // If profile is not found, force profile modal
          setModalType('profile');
          showToast('Please set up your professional profile to begin receiving requests.', 'warning');
        }
        const pbks = await api.bookings.getProfessional();
        setProfessionalBookings(pbks);
        try {
          const subs = await api.subscriptions.getMy();
          setSubscriptions(subs);
        } catch (e) {}
        setDashTab('bookings');
      } else if (me.role === 'ADMIN') {
        loadAdminData();
        setDashTab('overview');
      }
    } catch (err) {
      showToast('Session expired, please login again.', 'error');
      handleLogout();
    }
  }, [token, showToast]);

  const loadAdminData = async () => {
    try {
      const stats = await api.admin.getDashboard();
      setAdminStats(stats);
      const users = await api.admin.getUsers();
      setAdminUsers(users);
      const bookings = await api.admin.getBookings();
      setAdminBookings(bookings);
    } catch (err) {
      showToast('Failed to load admin stats: ' + err.message, 'error');
    }
  };

  useEffect(() => {
    fetchCatalog();
    if (token) {
      loadUserData();
    }
  }, [token, fetchCatalog, loadUserData]);

  // --- Authentication Handlers ---
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await api.auth.login(loginForm);
      localStorage.setItem('token', res.token);
      localStorage.setItem('userRole', res.role);
      setToken(res.token);
      setUserRole(res.role);
      showToast('Logged in successfully!');
      setView('dashboard');
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const res = await api.auth.register(regForm);
      localStorage.setItem('token', res.token);
      localStorage.setItem('userRole', res.role);
      setToken(res.token);
      setUserRole(res.role);
      showToast('Account created successfully!');
      setView('dashboard');
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    setToken('');
    setUserRole('');
    setUser(null);
    setMyAddresses([]);
    setMyBookings([]);
    setProfessionalBookings([]);
    setProfessionalProfile(null);
    setView('landing');
    showToast('Logged out successfully.');
  };

  // --- Homeowner Operations ---
  const createAddress = async (e) => {
    e.preventDefault();
    try {
      await api.addresses.create(addressForm);
      showToast('Address added!');
      setModalType(null);
      // Reload
      const addrs = await api.addresses.getMy();
      setMyAddresses(addrs);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleBookService = (serviceId) => {
    if (!token) {
      setView('login');
      showToast('Please login to book services.', 'warning');
      return;
    }
    if (userRole !== 'HOMEOWNER') {
      showToast('Only homeowners can book services.', 'error');
      return;
    }
    if (myAddresses.length === 0) {
      setModalType('address');
      showToast('Please add a service address first.', 'warning');
      return;
    }
    setBookingForm({
      serviceId,
      addressId: myAddresses[0].id,
      scheduledAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 16),
      instantBooking: true,
      customerNotes: ''
    });
    setModalType('booking');
  };

  const executeBooking = async (e) => {
    e.preventDefault();
    try {
      // parse scheduledAt to ISO
      const formatted = {
        ...bookingForm,
        scheduledAt: bookingForm.instantBooking ? null : new Date(bookingForm.scheduledAt).toISOString()
      };
      const res = await api.bookings.create(formatted);
      showToast(`Booking requested! Provider assigned: ${res.professionalName || 'Finding provider...'}`);
      setModalType(null);
      // Reload bookings
      const bks = await api.bookings.getMy();
      setMyBookings(bks);
      setDashTab('bookings');
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleOpenPayment = (booking) => {
    setPaymentForm({
      bookingId: booking.id,
      amount: booking.estimatedAmount,
      paymentMethod: 'CREDIT_CARD'
    });
    setSelectedBooking(booking);
    setModalType('payment');
  };

  const executePayment = async (e) => {
    e.preventDefault();
    try {
      await api.payments.create(paymentForm);
      showToast('Payment processed successfully!');
      setModalType(null);
      // Reload
      const bks = await api.bookings.getMy();
      setMyBookings(bks);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleOpenReview = (booking) => {
    setReviewForm({
      bookingId: booking.id,
      rating: 5,
      comment: ''
    });
    setSelectedBooking(booking);
    setModalType('review');
  };

  const executeReview = async (e) => {
    e.preventDefault();
    try {
      await api.reviews.create(reviewForm);
      showToast('Review submitted. Thank you for your feedback!');
      setModalType(null);
      // Reload
      const bks = await api.bookings.getMy();
      setMyBookings(bks);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // --- Professional Operations ---
  const saveProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await api.professionals.createProfile(profileForm);
      setProfessionalProfile(res);
      showToast('Professional profile updated successfully!');
      setModalType(null);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const updateStatus = async (bookingId, newStatus) => {
    try {
      await api.bookings.updateStatus(bookingId, { status: newStatus });
      showToast(`Booking updated to ${newStatus}`);
      // Reload professional bookings
      const pbks = await api.bookings.getProfessional();
      setProfessionalBookings(pbks);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const subscribePlan = async (plan) => {
    try {
      await api.subscriptions.subscribe({ plan });
      showToast(`Subscription to ${plan} plan successful! You now have verified features.`);
      // Reload profile and subscription
      const profile = await api.professionals.getMyProfile();
      setProfessionalProfile(profile);
      const subs = await api.subscriptions.getMy();
      setSubscriptions(subs);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const cancelSubscription = async (subId) => {
    try {
      await api.subscriptions.cancel(subId);
      showToast('Subscription cancelled.');
      const subs = await api.subscriptions.getMy();
      setSubscriptions(subs);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // --- Admin Operations ---
  const handleVerifyProfessional = async (profId, approve) => {
    try {
      await api.admin.verifyProfessional(profId, { verified: approve });
      showToast(approve ? 'Professional profile verified!' : 'Verification rejected.');
      loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const createCategory = async (e) => {
    e.preventDefault();
    try {
      await api.categories.create(newCatForm);
      showToast('Category created!');
      setNewCatForm({ name: '', description: '' });
      fetchCatalog();
      loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const createService = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...newServiceForm,
        price: parseFloat(newServiceForm.price),
        duration: parseInt(newServiceForm.duration),
        categoryId: parseInt(newServiceForm.categoryId)
      };
      await api.services.create(data);
      showToast('Service added!');
      setNewServiceForm({ title: '', description: '', price: '', duration: '', categoryId: '' });
      fetchCatalog();
      loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  return (
    <div className="app-container">
      {/* Toast Notification */}
      {toast && (
        <div className={`notification notification-${toast.type}`}>
          {toast.type === 'success' ? <CheckCircle size={18} /> : <AlertCircle size={18} />}
          <span>{toast.message}</span>
        </div>
      )}

      {/* Navbar */}
      <nav className="navbar">
        <div className="nav-content">
          <div className="brand" onClick={() => setView('landing')}>
            <Wrench size={22} className="accent" />
            <span>NestNow</span>
          </div>
          <div className="nav-links">
            <span className="nav-link" onClick={() => setView('landing')}>Home</span>
            {token ? (
              <>
                <span className="nav-link" onClick={() => setView('dashboard')}>Dashboard</span>
                <span className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <User size={14} />
                  {user?.fullName || 'Account'}
                </span>
                <button className="btn btn-secondary" onClick={handleLogout} style={{ padding: '6px 12px', fontSize: '12px' }}>
                  <LogOut size={14} />
                  Logout
                </button>
              </>
            ) : (
              <>
                <span className="nav-link" onClick={() => setView('login')}>Login</span>
                <button className="btn btn-primary" onClick={() => setView('register')} style={{ padding: '8px 16px', fontSize: '13px' }}>
                  Sign Up
                </button>
              </>
            )}
          </div>
        </div>
      </nav>

      {/* Views */}
      {view === 'landing' && (
        <>
          {/* Hero */}
          <section className="hero">
            <h1>Your Nest, Cared For.</h1>
            <p>Find trusted local professionals for plumbing, electrical wiring, home repairs, deep cleaning, and appliance servicing.</p>
            <form className="search-container" onSubmit={handleSearch}>
              <Search className="search-icon" size={20} />
              <input 
                type="text" 
                className="search-input" 
                placeholder="What service do you need today? (e.g. Tap, AC service, Wiring)" 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </form>
          </section>

          {/* Categories */}
          <section className="categories-section">
            <h2 className="section-title">Browse Categories</h2>
            <div className="category-pill-container">
              <button 
                className={`category-pill ${selectedCategory === null ? 'active' : ''}`}
                onClick={() => handleCategorySelect(null)}
              >
                All Services
              </button>
              {categories.map((cat) => (
                <button 
                  key={cat.id} 
                  className={`category-pill ${selectedCategory?.id === cat.id ? 'active' : ''}`}
                  onClick={() => handleCategorySelect(cat)}
                >
                  {cat.name}
                </button>
              ))}
            </div>
          </section>

          {/* Services Grid */}
          <section className="services-section">
            <h2 className="section-title">
              {selectedCategory ? `${selectedCategory.name} Services` : 'Recommended Services'}
            </h2>
            <div className="grid-4" style={{ marginTop: '24px' }}>
              {services.map((service) => (
                <div key={service.id} className="glass-card service-card">
                  <div className="service-title">{service.title}</div>
                  <div className="service-desc">{service.description}</div>
                  <div className="service-meta">
                    <div>
                      <div className="service-price">₹{service.price}</div>
                      <div className="service-duration">{service.duration} mins</div>
                    </div>
                    <button className="btn btn-primary" onClick={() => handleBookService(service.id)}>
                      Book Now
                    </button>
                  </div>
                </div>
              ))}
              {services.length === 0 && (
                <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
                  No services found. Try searching for something else.
                </div>
              )}
            </div>
          </section>
        </>
      )}

      {view === 'login' && (
        <section className="auth-container">
          <div className="glass-panel auth-card">
            <div className="auth-header">
              <h2>Welcome Back</h2>
              <p>Sign in to your NestNow account to continue</p>
            </div>
            <form onSubmit={handleLogin}>
              <div className="form-group">
                <label className="form-label">Email Address</label>
                <div style={{ position: 'relative' }}>
                  <Mail size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="email" 
                    className="form-control" 
                    placeholder="name@example.com" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={loginForm.email}
                    onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Password</label>
                <div style={{ position: 'relative' }}>
                  <Lock size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="password" 
                    className="form-control" 
                    placeholder="••••••••" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={loginForm.password}
                    onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px', padding: '12px' }}>
                Sign In
              </button>
            </form>
            <div className="auth-footer">
              Don't have an account? <span className="auth-link" onClick={() => setView('register')}>Register here</span>
            </div>
          </div>
        </section>
      )}

      {view === 'register' && (
        <section className="auth-container">
          <div className="glass-panel auth-card">
            <div className="auth-header">
              <h2>Join NestNow</h2>
              <p>Create an account to book or provide home services</p>
            </div>
            <form onSubmit={handleRegister}>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <div style={{ position: 'relative' }}>
                  <User size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="text" 
                    className="form-control" 
                    placeholder="John Doe" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={regForm.fullName}
                    onChange={(e) => setRegForm({ ...regForm, fullName: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Email Address</label>
                <div style={{ position: 'relative' }}>
                  <Mail size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="email" 
                    className="form-control" 
                    placeholder="john@example.com" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={regForm.email}
                    onChange={(e) => setRegForm({ ...regForm, email: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <div style={{ position: 'relative' }}>
                  <Phone size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="tel" 
                    className="form-control" 
                    placeholder="9876543210" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={regForm.phone}
                    onChange={(e) => setRegForm({ ...regForm, phone: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Password</label>
                <div style={{ position: 'relative' }}>
                  <Lock size={16} style={{ position: 'absolute', left: '14px', top: '15px', color: 'var(--text-muted)' }} />
                  <input 
                    type="password" 
                    className="form-control" 
                    placeholder="••••••••" 
                    style={{ paddingLeft: '44px', width: '100%' }}
                    value={regForm.password}
                    onChange={(e) => setRegForm({ ...regForm, password: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">I want to join as a:</label>
                <select 
                  className="form-control form-select"
                  value={regForm.role}
                  onChange={(e) => setRegForm({ ...regForm, role: e.target.value })}
                >
                  <option value="HOMEOWNER">Homeowner (Looking for services)</option>
                  <option value="PROFESSIONAL">Professional Service Provider</option>
                  <option value="ADMIN">Administrator</option>
                </select>
              </div>
              <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px', padding: '12px' }}>
                Create Account
              </button>
            </form>
            <div className="auth-footer">
              Already have an account? <span className="auth-link" onClick={() => setView('login')}>Sign In here</span>
            </div>
          </div>
        </section>
      )}

      {view === 'dashboard' && (
        <section className="dashboard-wrapper">
          {/* Sidebar */}
          <div className="sidebar-menu">
            <div style={{ padding: '0 16px 20px', borderBottom: '1px solid var(--border-color)', marginBottom: '10px' }}>
              <div style={{ fontSize: '16px', fontWeight: 600, color: '#fff' }}>{user?.fullName}</div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Role: {user?.role}</div>
              {user?.role === 'PROFESSIONAL' && (
                <div style={{ fontSize: '11px', color: 'var(--secondary)', display: 'flex', alignItems: 'center', gap: '4px', marginTop: '6px' }}>
                  <Award size={12} />
                  Tier: {professionalProfile?.badgeTier || 'NONE'}
                </div>
              )}
            </div>

            {userRole === 'HOMEOWNER' && (
              <>
                <div className={`sidebar-item ${dashTab === 'marketplace' ? 'active' : ''}`} onClick={() => setDashTab('marketplace')}>
                  <Layers size={16} /> Services Catalog
                </div>
                <div className={`sidebar-item ${dashTab === 'bookings' ? 'active' : ''}`} onClick={() => setDashTab('bookings')}>
                  <Calendar size={16} /> My Bookings
                </div>
                <div className={`sidebar-item ${dashTab === 'addresses' ? 'active' : ''}`} onClick={() => setDashTab('addresses')}>
                  <MapPin size={16} /> My Addresses
                </div>
              </>
            )}

            {userRole === 'PROFESSIONAL' && (
              <>
                <div className={`sidebar-item ${dashTab === 'bookings' ? 'active' : ''}`} onClick={() => setDashTab('bookings')}>
                  <Calendar size={16} /> Client Bookings
                </div>
                <div className={`sidebar-item ${dashTab === 'profile' ? 'active' : ''}`} onClick={() => setDashTab('profile')}>
                  <User size={16} /> Manage Profile
                </div>
                <div className={`sidebar-item ${dashTab === 'subscription' ? 'active' : ''}`} onClick={() => setDashTab('subscription')}>
                  <CreditCard size={16} /> Subscriptions & Badges
                </div>
              </>
            )}

            {userRole === 'ADMIN' && (
              <>
                <div className={`sidebar-item ${dashTab === 'overview' ? 'active' : ''}`} onClick={() => setDashTab('overview')}>
                  <Layers size={16} /> Overview
                </div>
                <div className={`sidebar-item ${dashTab === 'users' ? 'active' : ''}`} onClick={() => setDashTab('users')}>
                  <User size={16} /> Manage Users
                </div>
                <div className={`sidebar-item ${dashTab === 'bookings' ? 'active' : ''}`} onClick={() => setDashTab('bookings')}>
                  <Calendar size={16} /> Manage Bookings
                </div>
                <div className={`sidebar-item ${dashTab === 'categories' ? 'active' : ''}`} onClick={() => setDashTab('categories')}>
                  <Layers size={16} /> Manage Categories
                </div>
                <div className={`sidebar-item ${dashTab === 'services' ? 'active' : ''}`} onClick={() => setDashTab('services')}>
                  <Wrench size={16} /> Manage Services
                </div>
              </>
            )}
          </div>

          {/* Core Content */}
          <div className="dashboard-content">
            {/* HOMEOWNER TAB: MARKETPLACE */}
            {userRole === 'HOMEOWNER' && dashTab === 'marketplace' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Book a Service</h2>
                  <p>Choose from our verified expert home maintenance services</p>
                </div>
                <div className="category-pill-container" style={{ marginBottom: '24px' }}>
                  <button 
                    className={`category-pill ${selectedCategory === null ? 'active' : ''}`}
                    onClick={() => handleCategorySelect(null)}
                  >
                    All Services
                  </button>
                  {categories.map((cat) => (
                    <button 
                      key={cat.id} 
                      className={`category-pill ${selectedCategory?.id === cat.id ? 'active' : ''}`}
                      onClick={() => handleCategorySelect(cat)}
                    >
                      {cat.name}
                    </button>
                  ))}
                </div>
                <div className="grid-3">
                  {services.map((service) => (
                    <div key={service.id} className="glass-card service-card">
                      <div className="service-title">{service.title}</div>
                      <div className="service-desc">{service.description}</div>
                      <div className="service-meta">
                        <div>
                          <div className="service-price">₹{service.price}</div>
                          <div className="service-duration">{service.duration} mins</div>
                        </div>
                        <button className="btn btn-primary" onClick={() => handleBookService(service.id)}>
                          Book
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* HOMEOWNER TAB: BOOKINGS */}
            {userRole === 'HOMEOWNER' && dashTab === 'bookings' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>My Bookings</h2>
                  <p>Track the progress and details of your requested services</p>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <div className="table-responsive">
                    <table className="custom-table">
                      <thead>
                        <tr>
                          <th>Service</th>
                          <th>Provider</th>
                          <th>Scheduled Time</th>
                          <th>Amount</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {myBookings.map((b) => (
                          <tr key={b.id}>
                            <td style={{ fontWeight: 500 }}>{b.serviceTitle}</td>
                            <td>{b.professionalName || <span style={{ color: 'var(--text-muted)' }}>Auto-assigning...</span>}</td>
                            <td>{new Date(b.scheduledAt).toLocaleString()}</td>
                            <td style={{ color: 'var(--secondary)', fontWeight: 600 }}>₹{b.estimatedAmount}</td>
                            <td>
                              <span className={`badge badge-${b.status.toLowerCase()}`}>{b.status}</span>
                            </td>
                            <td>
                              <div style={{ display: 'flex', gap: '8px' }}>
                                {b.status === 'ACCEPTED' && (
                                  <button className="btn btn-primary" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleOpenPayment(b)}>
                                    <CreditCard size={12} /> Pay Now
                                  </button>
                                )}
                                {b.status === 'COMPLETED' && (
                                  <button className="btn btn-secondary" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleOpenReview(b)}>
                                    <Star size={12} /> Review
                                  </button>
                                )}
                                {['REQUESTED', 'ACCEPTED'].includes(b.status) && (
                                  <button className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => updateStatus(b.id, 'CANCELLED')}>
                                    Cancel
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                        {myBookings.length === 0 && (
                          <tr>
                            <td colSpan="6" style={{ textAlign: 'center', padding: '30px', color: 'var(--text-secondary)' }}>
                              You have not booked any services yet.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* HOMEOWNER TAB: ADDRESSES */}
            {userRole === 'HOMEOWNER' && dashTab === 'addresses' && (
              <div>
                <div className="dashboard-title-bar" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <h2>My Addresses</h2>
                    <p>Manage the addresses where home service visits will take place</p>
                  </div>
                  <button className="btn btn-primary" onClick={() => setModalType('address')}>
                    <Plus size={16} /> Add Address
                  </button>
                </div>
                <div className="grid-3">
                  {myAddresses.map((addr) => (
                    <div key={addr.id} className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px', marginBottom: '4px' }}>
                        <MapPin size={16} className="accent" />
                        <span style={{ fontWeight: 600, fontSize: '15px' }}>{addr.label}</span>
                      </div>
                      <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{addr.line1}</div>
                      {addr.line2 && <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{addr.line2}</div>}
                      <div style={{ fontSize: '13px', color: 'var(--text-secondary)', fontWeight: 500 }}>
                        {addr.city}, {addr.state} - {addr.pincode}
                      </div>
                    </div>
                  ))}
                  {myAddresses.length === 0 && (
                    <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }} className="glass-panel">
                      No addresses saved. Click "Add Address" to add your service location.
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* PROFESSIONAL TAB: BOOKINGS */}
            {userRole === 'PROFESSIONAL' && dashTab === 'bookings' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Client Bookings</h2>
                  <p>Manage home service requests assigned to you</p>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <div className="table-responsive">
                    <table className="custom-table">
                      <thead>
                        <tr>
                          <th>Client</th>
                          <th>Service Type</th>
                          <th>Client Notes</th>
                          <th>Schedule At</th>
                          <th>Address</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {professionalBookings.map((b) => (
                          <tr key={b.id}>
                            <td style={{ fontWeight: 600 }}>{b.homeownerName}</td>
                            <td>{b.serviceTitle}</td>
                            <td style={{ fontStyle: 'italic', fontSize: '12px', color: 'var(--text-secondary)' }}>{b.customerNotes || 'No notes'}</td>
                            <td>{new Date(b.scheduledAt).toLocaleString()}</td>
                            <td style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                              {b.address.line1}, {b.address.city}
                            </td>
                            <td>
                              <span className={`badge badge-${b.status.toLowerCase()}`}>{b.status}</span>
                            </td>
                            <td>
                              <div style={{ display: 'flex', gap: '6px' }}>
                                {b.status === 'ACCEPTED' && (
                                  <button className="btn btn-primary" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => updateStatus(b.id, 'IN_PROGRESS')}>
                                    Start Job
                                  </button>
                                )}
                                {b.status === 'IN_PROGRESS' && (
                                  <button className="btn btn-secondary" style={{ padding: '6px 12px', fontSize: '12px', borderColor: 'var(--success)', color: 'var(--success)' }} onClick={() => updateStatus(b.id, 'COMPLETED')}>
                                    Complete Job
                                  </button>
                                )}
                                {['REQUESTED', 'ACCEPTED'].includes(b.status) && (
                                  <button className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => updateStatus(b.id, 'CANCELLED')}>
                                    Decline
                                  </button>
                                )}
                              </div>
                            </td>
                          </tr>
                        ))}
                        {professionalBookings.length === 0 && (
                          <tr>
                            <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: 'var(--text-secondary)' }}>
                              No bookings assigned to you yet. Ensure your profile is approved.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}

            {/* PROFESSIONAL TAB: PROFILE */}
            {userRole === 'PROFESSIONAL' && dashTab === 'profile' && (
              <div style={{ maxWidth: '600px' }}>
                <div className="dashboard-title-bar">
                  <h2>Manage Profile</h2>
                  <p>Keep your professional description up to date to get verified by administrators</p>
                </div>
                <div className="glass-panel" style={{ padding: '30px' }}>
                  <form onSubmit={saveProfile}>
                    <div className="form-group">
                      <label className="form-label">Professional Bio</label>
                      <textarea 
                        className="form-control" 
                        rows="4"
                        placeholder="Write details about your expertise, background, plumbing/electrical capabilities..."
                        value={profileForm.bio}
                        onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })}
                        required
                      />
                    </div>
                    <div className="grid-2">
                      <div className="form-group">
                        <label className="form-label">Years of Experience</label>
                        <input 
                          type="number" 
                          className="form-control" 
                          value={profileForm.experienceYears}
                          onChange={(e) => setProfileForm({ ...profileForm, experienceYears: parseInt(e.target.value) })}
                          min="1"
                          required 
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Service Area Coverage</label>
                        <input 
                          type="text" 
                          className="form-control" 
                          placeholder="e.g. Indiranagar, Bangalore" 
                          value={profileForm.serviceArea}
                          onChange={(e) => setProfileForm({ ...profileForm, serviceArea: e.target.value })}
                          required 
                        />
                      </div>
                    </div>
                    <div className="form-group">
                      <label className="form-label">Profile Image URL</label>
                      <input 
                        type="url" 
                        className="form-control" 
                        placeholder="https://images.unsplash.com/photo-..." 
                        value={profileForm.profileImageUrl}
                        onChange={(e) => setProfileForm({ ...profileForm, profileImageUrl: e.target.value })}
                      />
                    </div>
                    <button type="submit" className="btn btn-primary" style={{ padding: '12px 24px', marginTop: '10px' }}>
                      Update Profile Info
                    </button>
                  </form>
                </div>
              </div>
            )}

            {/* PROFESSIONAL TAB: SUBSCRIPTION */}
            {userRole === 'PROFESSIONAL' && dashTab === 'subscription' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Subscriptions & Badges</h2>
                  <p>Upgrade to a plan to receive verification badges and higher client placement rates</p>
                </div>
                <div className="grid-3" style={{ marginBottom: '40px' }}>
                  <div className="glass-card" style={{ borderTop: '4px solid var(--text-muted)' }}>
                    <h3>Standard Tier</h3>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '13px', margin: '10px 0' }}>Basic visibility in home service matchings.</p>
                    <div style={{ fontSize: '24px', fontWeight: 700, margin: '20px 0', color: '#fff' }}>₹299 <span style={{ fontSize: '13px', fontWeight: 400 }}>/ month</span></div>
                    <button className="btn btn-secondary" style={{ width: '100%' }} onClick={() => subscribePlan('STANDARD')}>
                      Upgrade to Standard
                    </button>
                  </div>
                  
                  <div className="glass-card" style={{ borderTop: '4px solid var(--primary)', transform: 'scale(1.03)', boxShadow: '0 8px 30px rgba(99,102,241,0.2)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <h3>Premium Tier</h3>
                      <span className="badge badge-accepted">Popular</span>
                    </div>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '13px', margin: '10px 0' }}>High visibility badge, verified ranking boosts, and priorities.</p>
                    <div style={{ fontSize: '24px', fontWeight: 700, margin: '20px 0', color: '#fff' }}>₹599 <span style={{ fontSize: '13px', fontWeight: 400 }}>/ month</span></div>
                    <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => subscribePlan('PREMIUM')}>
                      Upgrade to Premium
                    </button>
                  </div>

                  <div className="glass-card" style={{ borderTop: '4px solid var(--secondary)' }}>
                    <h3>Gold Executive</h3>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '13px', margin: '10px 0' }}>Ultimate priority placement. Featured listing on landing homepage.</p>
                    <div style={{ fontSize: '24px', fontWeight: 700, margin: '20px 0', color: '#fff' }}>₹999 <span style={{ fontSize: '13px', fontWeight: 400 }}>/ month</span></div>
                    <button className="btn btn-secondary" style={{ width: '100%' }} onClick={() => subscribePlan('GOLD')}>
                      Upgrade to Gold
                    </button>
                  </div>
                </div>

                <div className="dashboard-title-bar">
                  <h3>Active Plan Status</h3>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <table className="custom-table">
                    <thead>
                      <tr>
                        <th>Sub ID</th>
                        <th>Tier Plan</th>
                        <th>Billing Cycle Start</th>
                        <th>Expiry Date</th>
                        <th>Status</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {subscriptions.map((sub) => (
                        <tr key={sub.id}>
                          <td>#{sub.id}</td>
                          <td style={{ fontWeight: 600 }}>{sub.plan}</td>
                          <td>{new Date(sub.startDate).toLocaleDateString()}</td>
                          <td>{new Date(sub.endDate).toLocaleDateString()}</td>
                          <td>
                            <span className={`badge ${sub.status === 'ACTIVE' ? 'badge-active' : 'badge-cancelled'}`}>{sub.status}</span>
                          </td>
                          <td>
                            {sub.status === 'ACTIVE' && (
                              <button className="btn btn-danger" style={{ padding: '4px 10px', fontSize: '12px' }} onClick={() => cancelSubscription(sub.id)}>
                                Cancel
                              </button>
                            )}
                          </td>
                        </tr>
                      ))}
                      {subscriptions.length === 0 && (
                        <tr>
                          <td colSpan="6" style={{ textAlign: 'center', padding: '24px', color: 'var(--text-secondary)' }}>
                            No active subscriptions found.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ADMIN TAB: OVERVIEW */}
            {userRole === 'ADMIN' && dashTab === 'overview' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Admin Dashboard Overview</h2>
                  <p>Track homeowners, verified service provider directories, and platform statistics</p>
                </div>
                <div className="stats-grid">
                  <div className="glass-card stat-card">
                    <div className="stat-label">Total Platform Users</div>
                    <div className="stat-value">{adminStats?.totalUsers || 0}</div>
                    <div className="stat-footer">Homeowners & Pros</div>
                  </div>
                  <div className="glass-card stat-card">
                    <div className="stat-label">Total Service Bookings</div>
                    <div className="stat-value">{adminStats?.totalBookings || 0}</div>
                    <div className="stat-footer">Completed & Requested</div>
                  </div>
                  <div className="glass-card stat-card">
                    <div className="stat-label">Platform Active Revenue</div>
                    <div className="stat-value">₹{adminStats?.totalEarnings || 0}</div>
                    <div className="stat-footer">From active subscriptions</div>
                  </div>
                  <div className="glass-card stat-card" style={{ borderLeft: '4px solid var(--warning)' }}>
                    <div className="stat-label">Verification Backlog</div>
                    <div className="stat-value">{adminStats?.pendingVerifications || 0}</div>
                    <div className="stat-footer">Profiles requiring verification</div>
                  </div>
                </div>

                <div className="dashboard-title-bar" style={{ marginTop: '40px' }}>
                  <h3>Pending Provider Verification Requests</h3>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <table className="custom-table">
                    <thead>
                      <tr>
                        <th>Provider Name</th>
                        <th>Bio Description</th>
                        <th>Experience</th>
                        <th>Location</th>
                        <th>Badge Tier</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {adminUsers
                        .filter(u => u.role === 'PROFESSIONAL' && !u.verified)
                        .map((prof) => (
                          <tr key={prof.id}>
                            <td style={{ fontWeight: 600 }}>{prof.fullName}</td>
                            <td style={{ fontSize: '12px', color: 'var(--text-secondary)', maxWidth: '300px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                              Registered pro profile pending verification.
                            </td>
                            <td>-</td>
                            <td>-</td>
                            <td>NONE</td>
                            <td>
                              <div style={{ display: 'flex', gap: '8px' }}>
                                <button className="btn btn-primary" style={{ padding: '6px 12px', fontSize: '12px' }} onClick={() => handleVerifyProfessional(prof.id, true)}>
                                  Verify / Approve
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      {adminUsers.filter(u => u.role === 'PROFESSIONAL' && !u.verified).length === 0 && (
                        <tr>
                          <td colSpan="6" style={{ textAlign: 'center', padding: '24px', color: 'var(--text-secondary)' }}>
                            No pending provider verification requests in the queue.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ADMIN TAB: USERS */}
            {userRole === 'ADMIN' && dashTab === 'users' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Platform User Directory</h2>
                  <p>View registered account details and verification access credentials</p>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <table className="custom-table">
                    <thead>
                      <tr>
                        <th>User ID</th>
                        <th>Name</th>
                        <th>Email Address</th>
                        <th>Phone Number</th>
                        <th>Role Type</th>
                        <th>Verification Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {adminUsers.map((u) => (
                        <tr key={u.id}>
                          <td>#{u.id}</td>
                          <td style={{ fontWeight: 600 }}>{u.fullName}</td>
                          <td>{u.email}</td>
                          <td>{u.phone}</td>
                          <td style={{ color: u.role === 'ADMIN' ? 'var(--accent)' : 'var(--text-primary)' }}>{u.role}</td>
                          <td>
                            <span className={`badge ${u.verified ? 'badge-active' : 'badge-pending'}`}>
                              {u.verified ? 'VERIFIED' : 'PENDING'}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ADMIN TAB: BOOKINGS */}
            {userRole === 'ADMIN' && dashTab === 'bookings' && (
              <div>
                <div className="dashboard-title-bar">
                  <h2>Manage Platform Bookings</h2>
                  <p>Track all service bookings on NestNow</p>
                </div>
                <div className="glass-panel" style={{ padding: '10px' }}>
                  <table className="custom-table">
                    <thead>
                      <tr>
                        <th>Booking ID</th>
                        <th>Homeowner</th>
                        <th>Assigned Pro</th>
                        <th>Service Title</th>
                        <th>Est Amount</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {adminBookings.map((b) => (
                        <tr key={b.id}>
                          <td>#{b.id}</td>
                          <td style={{ fontWeight: 600 }}>{b.homeownerName}</td>
                          <td>{b.professionalName || 'Not Assigned'}</td>
                          <td>{b.serviceTitle}</td>
                          <td style={{ color: 'var(--secondary)', fontWeight: 600 }}>₹{b.estimatedAmount}</td>
                          <td>
                            <span className={`badge badge-${b.status.toLowerCase()}`}>{b.status}</span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ADMIN TAB: CATEGORIES */}
            {userRole === 'ADMIN' && dashTab === 'categories' && (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: '30px' }}>
                <div>
                  <div className="dashboard-title-bar">
                    <h2>Manage Categories</h2>
                    <p>Current active categories in the catalog directory</p>
                  </div>
                  <div className="glass-panel" style={{ padding: '10px' }}>
                    <table className="custom-table">
                      <thead>
                        <tr>
                          <th>Cat ID</th>
                          <th>Name</th>
                          <th>Description</th>
                        </tr>
                      </thead>
                      <tbody>
                        {categories.map((c) => (
                          <tr key={c.id}>
                            <td>#{c.id}</td>
                            <td style={{ fontWeight: 600 }}>{c.name}</td>
                            <td style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{c.description}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
                <div>
                  <div className="dashboard-title-bar">
                    <h2>Add Category</h2>
                    <p>Insert a new category</p>
                  </div>
                  <div className="glass-panel" style={{ padding: '24px' }}>
                    <form onSubmit={createCategory}>
                      <div className="form-group">
                        <label className="form-label">Category Name</label>
                        <input 
                          type="text" 
                          className="form-control" 
                          placeholder="e.g. Painting" 
                          value={newCatForm.name}
                          onChange={(e) => setNewCatForm({ ...newCatForm, name: e.target.value })}
                          required 
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Description</label>
                        <textarea 
                          className="form-control" 
                          rows="3" 
                          placeholder="Brief description..."
                          value={newCatForm.description}
                          onChange={(e) => setNewCatForm({ ...newCatForm, description: e.target.value })}
                          required 
                        />
                      </div>
                      <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                        Create Category
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            )}

            {/* ADMIN TAB: SERVICES */}
            {userRole === 'ADMIN' && dashTab === 'services' && (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: '30px' }}>
                <div>
                  <div className="dashboard-title-bar">
                    <h2>Manage Services</h2>
                    <p>Platform offerings matched to specific categories</p>
                  </div>
                  <div className="glass-panel" style={{ padding: '10px' }}>
                    <table className="custom-table">
                      <thead>
                        <tr>
                          <th>Service</th>
                          <th>Category</th>
                          <th>Price</th>
                          <th>Duration</th>
                        </tr>
                      </thead>
                      <tbody>
                        {services.map((s) => (
                          <tr key={s.id}>
                            <td style={{ fontWeight: 600 }}>{s.title}</td>
                            <td>{s.categoryName}</td>
                            <td style={{ color: 'var(--secondary)' }}>₹{s.price}</td>
                            <td>{s.duration} mins</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
                <div>
                  <div className="dashboard-title-bar">
                    <h2>Add Service</h2>
                    <p>Insert a new marketplace service card</p>
                  </div>
                  <div className="glass-panel" style={{ padding: '24px' }}>
                    <form onSubmit={createService}>
                      <div className="form-group">
                        <label className="form-label">Category</label>
                        <select 
                          className="form-control form-select"
                          value={newServiceForm.categoryId}
                          onChange={(e) => setNewServiceForm({ ...newServiceForm, categoryId: e.target.value })}
                          required
                        >
                          <option value="">Select Category</option>
                          {categories.map((c) => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                          ))}
                        </select>
                      </div>
                      <div className="form-group">
                        <label className="form-label">Service Title</label>
                        <input 
                          type="text" 
                          className="form-control" 
                          placeholder="e.g. Deep Sofa Cleaning" 
                          value={newServiceForm.title}
                          onChange={(e) => setNewServiceForm({ ...newServiceForm, title: e.target.value })}
                          required 
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Description</label>
                        <textarea 
                          className="form-control" 
                          rows="3" 
                          placeholder="Description..."
                          value={newServiceForm.description}
                          onChange={(e) => setNewServiceForm({ ...newServiceForm, description: e.target.value })}
                          required 
                        />
                      </div>
                      <div className="grid-2">
                        <div className="form-group">
                          <label className="form-label">Price (₹)</label>
                          <input 
                            type="number" 
                            className="form-control" 
                            placeholder="499" 
                            value={newServiceForm.price}
                            onChange={(e) => setNewServiceForm({ ...newServiceForm, price: e.target.value })}
                            required 
                          />
                        </div>
                        <div className="form-group">
                          <label className="form-label">Duration (mins)</label>
                          <input 
                            type="number" 
                            className="form-control" 
                            placeholder="90" 
                            value={newServiceForm.duration}
                            onChange={(e) => setNewServiceForm({ ...newServiceForm, duration: e.target.value })}
                            required 
                          />
                        </div>
                      </div>
                      <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }}>
                        Create Service
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            )}
          </div>
        </section>
      )}

      {/* MODALS */}

      {/* Address creation modal */}
      {modalType === 'address' && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content">
            <div className="modal-header flex-between">
              <h3>Add New Service Address</h3>
              <X size={18} style={{ cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => setModalType(null)} />
            </div>
            <form onSubmit={createAddress}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Label Designation</label>
                  <select 
                    className="form-control form-select"
                    value={addressForm.label}
                    onChange={(e) => setAddressForm({ ...addressForm, label: e.target.value })}
                  >
                    <option value="Home">Home</option>
                    <option value="Office">Office</option>
                    <option value="Other">Other Location</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Address Line 1</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    placeholder="Flat number, building, street..." 
                    value={addressForm.line1}
                    onChange={(e) => setAddressForm({ ...addressForm, line1: e.target.value })}
                    required 
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Address Line 2 (Optional)</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    placeholder="Landmark, locality..." 
                    value={addressForm.line2}
                    onChange={(e) => setAddressForm({ ...addressForm, line2: e.target.value })}
                  />
                </div>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">City</label>
                    <input 
                      type="text" 
                      className="form-control" 
                      placeholder="e.g. Bangalore" 
                      value={addressForm.city}
                      onChange={(e) => setAddressForm({ ...addressForm, city: e.target.value })}
                      required 
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">State</label>
                    <input 
                      type="text" 
                      className="form-control" 
                      placeholder="e.g. Karnataka" 
                      value={addressForm.state}
                      onChange={(e) => setAddressForm({ ...addressForm, state: e.target.value })}
                      required 
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Pincode</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    placeholder="560001" 
                    value={addressForm.pincode}
                    onChange={(e) => setAddressForm({ ...addressForm, pincode: e.target.value })}
                    required 
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalType(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save Address</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Booking confirmation wizard modal */}
      {modalType === 'booking' && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content">
            <div className="modal-header flex-between">
              <h3>Confirm Service Booking</h3>
              <X size={18} style={{ cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => setModalType(null)} />
            </div>
            <form onSubmit={executeBooking}>
              <div className="modal-body">
                <div style={{ background: 'rgba(255,255,255,0.02)', padding: '14px', borderRadius: '8px', marginBottom: '20px', border: '1px solid var(--border-color)' }}>
                  <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Selected Service:</div>
                  <div style={{ fontSize: '16px', fontWeight: 600, color: 'var(--secondary)' }}>
                    {services.find(s => s.id === bookingForm.serviceId)?.title}
                  </div>
                  <div style={{ fontSize: '14px', color: '#fff', marginTop: '4px' }}>
                    Price: ₹{services.find(s => s.id === bookingForm.serviceId)?.price}
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Select Address</label>
                  <select 
                    className="form-control form-select"
                    value={bookingForm.addressId}
                    onChange={(e) => setBookingForm({ ...bookingForm, addressId: parseInt(e.target.value) })}
                    required
                  >
                    {myAddresses.map(a => (
                      <option key={a.id} value={a.id}>{a.label}: {a.line1}, {a.city}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group" style={{ flexDirection: 'row', gap: '10px', alignItems: 'center', margin: '14px 0' }}>
                  <input 
                    type="checkbox" 
                    id="instantBooking" 
                    checked={bookingForm.instantBooking}
                    onChange={(e) => setBookingForm({ ...bookingForm, instantBooking: e.target.checked })}
                    style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                  />
                  <label htmlFor="instantBooking" className="form-label" style={{ cursor: 'pointer', fontSize: '14px', color: '#fff' }}>
                    Instant Dispatch (assigned within 30 minutes)
                  </label>
                </div>

                {!bookingForm.instantBooking && (
                  <div className="form-group">
                    <label className="form-label">Scheduled Time</label>
                    <input 
                      type="datetime-local" 
                      className="form-control" 
                      value={bookingForm.scheduledAt}
                      onChange={(e) => setBookingForm({ ...bookingForm, scheduledAt: e.target.value })}
                      required={!bookingForm.instantBooking}
                    />
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Special Instructions for Provider</label>
                  <textarea 
                    className="form-control" 
                    rows="3" 
                    placeholder="e.g. Ring bell of Flat 302, carry ladder, leakage is in kitchen..."
                    value={bookingForm.customerNotes}
                    onChange={(e) => setBookingForm({ ...bookingForm, customerNotes: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalType(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Confirm & Dispatch</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Payment simulation modal */}
      {modalType === 'payment' && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content">
            <div className="modal-header flex-between">
              <h3>Complete Payment</h3>
              <X size={18} style={{ cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => setModalType(null)} />
            </div>
            <form onSubmit={executePayment}>
              <div className="modal-body">
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '20px' }}>
                  <div className="flex-between">
                    <span style={{ color: 'var(--text-secondary)' }}>Service:</span>
                    <span style={{ fontWeight: 600 }}>{selectedBooking?.serviceTitle}</span>
                  </div>
                  <div className="flex-between">
                    <span style={{ color: 'var(--text-secondary)' }}>Assigned Professional:</span>
                    <span>{selectedBooking?.professionalName}</span>
                  </div>
                  <hr style={{ border: 0, borderTop: '1px solid var(--border-color)', margin: '8px 0' }} />
                  <div className="flex-between" style={{ fontSize: '18px' }}>
                    <span style={{ color: 'var(--text-secondary)', fontWeight: 600 }}>Amount Due:</span>
                    <span style={{ color: 'var(--secondary)', fontWeight: 700 }}>₹{selectedBooking?.estimatedAmount}</span>
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Payment Method</label>
                  <select 
                    className="form-control form-select"
                    value={paymentForm.paymentMethod}
                    onChange={(e) => setPaymentForm({ ...paymentForm, paymentMethod: e.target.value })}
                  >
                    <option value="CREDIT_CARD">Credit Card</option>
                    <option value="DEBIT_CARD">Debit Card</option>
                    <option value="UPI">UPI (Unified Payments Interface)</option>
                    <option value="NET_BANKING">Net Banking</option>
                  </select>
                </div>

                <div style={{ background: 'rgba(6, 182, 212, 0.05)', padding: '14px', borderRadius: '8px', border: '1px solid rgba(6, 182, 212, 0.1)', fontSize: '13px', color: 'var(--text-secondary)' }}>
                  This is a secure simulated transaction interface. No real money will be charged.
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalType(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary" style={{ gap: '8px' }}>
                  <CreditCard size={14} /> Pay ₹{selectedBooking?.estimatedAmount}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Review modal */}
      {modalType === 'review' && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content">
            <div className="modal-header flex-between">
              <h3>Submit Service Review</h3>
              <X size={18} style={{ cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => setModalType(null)} />
            </div>
            <form onSubmit={executeReview}>
              <div className="modal-body">
                <div style={{ textAlign: 'center', marginBottom: '24px' }}>
                  <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '4px' }}>How was your experience with:</div>
                  <div style={{ fontSize: '16px', fontWeight: 600, color: '#fff' }}>{selectedBooking?.professionalName}</div>
                  <div style={{ fontSize: '14px', color: 'var(--text-muted)', marginTop: '2px' }}>{selectedBooking?.serviceTitle}</div>
                </div>

                <div className="form-group" style={{ alignItems: 'center' }}>
                  <label className="form-label">Overall Rating</label>
                  <div style={{ display: 'flex', gap: '8px', margin: '10px 0' }}>
                    {[1, 2, 3, 4, 5].map((star) => (
                      <Star 
                        key={star} 
                        size={28} 
                        style={{ cursor: 'pointer', fill: star <= reviewForm.rating ? 'var(--warning)' : 'none', color: 'var(--warning)' }} 
                        onClick={() => setReviewForm({ ...reviewForm, rating: star })}
                      />
                    ))}
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Review Details</label>
                  <textarea 
                    className="form-control" 
                    rows="3" 
                    placeholder="Describe the quality of work, speed, politeness of the worker..."
                    value={reviewForm.comment}
                    onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setModalType(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Submit Review</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Professional initial profile setup / edit modal */}
      {modalType === 'profile' && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content" style={{ maxWidth: '460px' }}>
            <div className="modal-header">
              <h3>Setup Professional Profile</h3>
              <p style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>You must configure this profile to activate your professional listing.</p>
            </div>
            <form onSubmit={saveProfile}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Short Professional Bio</label>
                  <textarea 
                    className="form-control" 
                    rows="3" 
                    placeholder="Describe your capabilities (e.g. Certified plumber with 5 years experience specializing in bathroom leaks)..."
                    value={profileForm.bio}
                    onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })}
                    required
                  />
                </div>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Experience (Years)</label>
                    <input 
                      type="number" 
                      className="form-control" 
                      min="1"
                      value={profileForm.experienceYears}
                      onChange={(e) => setProfileForm({ ...profileForm, experienceYears: parseInt(e.target.value) })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Service Area</label>
                    <input 
                      type="text" 
                      className="form-control" 
                      placeholder="e.g. Bangalore South"
                      value={profileForm.serviceArea}
                      onChange={(e) => setProfileForm({ ...profileForm, serviceArea: e.target.value })}
                      required
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Avatar URL</label>
                  <input 
                    type="url" 
                    className="form-control" 
                    placeholder="https://images.unsplash.com..."
                    value={profileForm.profileImageUrl}
                    onChange={(e) => setProfileForm({ ...profileForm, profileImageUrl: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Activate Professional Profile</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
