import { Outlet } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import Header from './Header'
import Footer from './Footer'
import CartSidebar from '../cart/CartSidebar'

/**
 * Main layout component that wraps the application pages.
 * Includes header, footer, and toast notifications.
 *
 * @author Chirag Singhal
 * @version 1.0.0
 */
const MainLayout = () => {
  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      {/* Header */}
      <Header />

      {/* Main content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <Footer />

      {/* Cart Sidebar */}
      <CartSidebar />

      {/* Toast notifications */}
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#10b981',
              secondary: '#fff',
            },
          },
          error: {
            duration: 5000,
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </div>
  )
}

export default MainLayout
