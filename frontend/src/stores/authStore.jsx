import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authService } from '../services/authService'
import toast from 'react-hot-toast'

/**
 * Authentication store using Zustand for state management.
 * Handles user authentication, token management, and auth state persistence.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */

const useAuthStore = create(
  persist(
    (set, get) => ({
      // State
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Actions
      login: async (credentials) => {
        set({ isLoading: true, error: null })
        
        try {
          const response = await authService.login(credentials)
          const { data } = response.data
          
          set({
            user: {
              id: data.id,
              fullName: data.fullName,
              email: data.email,
              role: data.role
            },
            token: data.token,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })
          
          // Set token in axios defaults
          authService.setAuthToken(data.token)
          
          toast.success('Login successful!')
          return { success: true, data }
        } catch (error) {
          const errorMessage = error.response?.data?.message || 'Login failed'
          set({
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: errorMessage
          })
          
          toast.error(errorMessage)
          return { success: false, error: errorMessage }
        }
      },

      register: async (userData) => {
        set({ isLoading: true, error: null })
        
        try {
          const response = await authService.register(userData)
          const { data } = response.data
          
          set({
            user: {
              id: data.id,
              fullName: data.fullName,
              email: data.email,
              role: data.role
            },
            token: data.token,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })
          
          // Set token in axios defaults
          authService.setAuthToken(data.token)
          
          toast.success('Registration successful!')
          return { success: true, data }
        } catch (error) {
          const errorMessage = error.response?.data?.message || 'Registration failed'
          set({
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: errorMessage
          })
          
          toast.error(errorMessage)
          return { success: false, error: errorMessage }
        }
      },

      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
          error: null
        })
        
        // Remove token from axios defaults
        authService.removeAuthToken()
        
        toast.success('Logged out successfully!')
      },

      refreshToken: async () => {
        const { token } = get()
        if (!token) return false
        
        try {
          const response = await authService.refreshToken()
          const { data } = response.data
          
          set({
            token: data.token,
            user: {
              id: data.id,
              fullName: data.fullName,
              email: data.email,
              role: data.role
            }
          })
          
          // Update token in axios defaults
          authService.setAuthToken(data.token)
          
          return true
        } catch (error) {
          // Token refresh failed, logout user
          get().logout()
          return false
        }
      },

      validateToken: async () => {
        const { token } = get()
        if (!token) return false
        
        try {
          const response = await authService.validateToken()
          return response.data.data === true
        } catch (error) {
          return false
        }
      },

      clearError: () => {
        set({ error: null })
      },

      // Initialize auth state from stored token
      initializeAuth: () => {
        const { token } = get()
        if (token) {
          authService.setAuthToken(token)
          // Optionally validate token on app start
          get().validateToken().then((isValid) => {
            if (!isValid) {
              get().logout()
            }
          })
        }
      },

      // Check if user has specific role
      hasRole: (role) => {
        const { user } = get()
        return user?.role === role
      },

      // Check if user is admin
      isAdmin: () => {
        const { user } = get()
        return user?.role === 'ADMIN'
      },

      // Check if user is customer
      isCustomer: () => {
        const { user } = get()
        return user?.role === 'CUSTOMER'
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
)

// React context provider for auth store (optional, for React DevTools)
import { createContext, useContext } from 'react'

const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  return (
    <AuthContext.Provider value={useAuthStore}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    return useAuthStore()
  }
  return context()
}

export default useAuthStore
