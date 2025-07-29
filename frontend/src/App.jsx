import { BrowserRouter } from 'react-router-dom'
import { AppRouter } from './router/AppRouter'
import { AuthProvider } from './stores/authStore'

/**
 * Main App component that sets up the application structure
 * with routing, authentication, and global providers.
 */
function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen bg-gray-50">
          <AppRouter />
        </div>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
