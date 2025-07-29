import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../stores/authStore.jsx'
import { useEffect } from 'react'

// Layout components
import MainLayout from '../components/layout/MainLayout'
import AdminLayout from '../components/layout/AdminLayout'

// Public pages
import HomePage from '../pages/HomePage'
import ProductsPage from '../pages/ProductsPage'
import ProductDetailPage from '../pages/ProductDetailPage'
import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'

// Protected pages
import ProfilePage from '../pages/user/ProfilePage'
import ChangePasswordPage from '../pages/user/ChangePasswordPage'
import CartPage from '../pages/cart/CartPage'
import CheckoutPage from '../pages/cart/CheckoutPage'
import OrdersPage from '../pages/orders/OrdersPage'
import OrderDetailsPage from '../pages/orders/OrderDetailsPage'

// Admin pages
import AdminDashboard from '../pages/admin/AdminDashboard'
import AdminProducts from '../pages/admin/AdminProducts'
import AdminUsers from '../pages/admin/AdminUsers'
import AdminProductForm from '../pages/admin/AdminProductForm'
import AdminOrdersPage from '../pages/admin/orders/AdminOrdersPage'

// Error pages
import NotFoundPage from '../pages/NotFoundPage'
import UnauthorizedPage from '../pages/UnauthorizedPage'

/**
 * Protected route component that requires authentication
 */
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}

/**
 * Admin route component that requires admin role
 */
const AdminRoute = ({ children }) => {
  const { isAuthenticated, isAdmin } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (!isAdmin()) {
    return <Navigate to="/unauthorized" replace />
  }

  return children
}

/**
 * Guest route component that redirects authenticated users
 */
const GuestRoute = ({ children }) => {
  const { isAuthenticated, isAdmin } = useAuth()

  if (isAuthenticated) {
    return <Navigate to={isAdmin() ? "/admin" : "/"} replace />
  }

  return children
}

/**
 * Main application router component
 */
export const AppRouter = () => {
  const { initializeAuth } = useAuth()

  // Initialize auth state on app start
  useEffect(() => {
    initializeAuth()
  }, [initializeAuth])

  return (
    <Routes>
      {/* Public routes with main layout */}
      <Route path="/" element={<MainLayout />}>
        <Route index element={<HomePage />} />
        <Route path="products" element={<ProductsPage />} />
        <Route path="products/:id" element={<ProductDetailPage />} />

        {/* Cart routes (accessible to all) */}
        <Route path="cart" element={<CartPage />} />

        {/* Protected user routes */}
        <Route path="checkout" element={
          <ProtectedRoute>
            <CheckoutPage />
          </ProtectedRoute>
        } />
        <Route path="orders" element={
          <ProtectedRoute>
            <OrdersPage />
          </ProtectedRoute>
        } />
        <Route path="orders/:orderId" element={
          <ProtectedRoute>
            <OrderDetailsPage />
          </ProtectedRoute>
        } />
        <Route path="profile" element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        } />
        <Route path="change-password" element={
          <ProtectedRoute>
            <ChangePasswordPage />
          </ProtectedRoute>
        } />
      </Route>

      {/* Auth routes (guest only) */}
      <Route path="/login" element={
        <GuestRoute>
          <LoginPage />
        </GuestRoute>
      } />
      <Route path="/register" element={
        <GuestRoute>
          <RegisterPage />
        </GuestRoute>
      } />

      {/* Admin routes */}
      <Route path="/admin" element={
        <AdminRoute>
          <AdminLayout />
        </AdminRoute>
      }>
        <Route index element={<AdminDashboard />} />
        <Route path="products" element={<AdminProducts />} />
        <Route path="products/new" element={<AdminProductForm />} />
        <Route path="products/edit/:id" element={<AdminProductForm />} />
        <Route path="orders" element={<AdminOrdersPage />} />
        <Route path="users" element={<AdminUsers />} />
      </Route>

      {/* Error routes */}
      <Route path="/unauthorized" element={<UnauthorizedPage />} />
      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  )
}

export default AppRouter
