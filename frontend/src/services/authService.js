import axios from 'axios'

/**
 * Authentication service for handling API calls related to user authentication.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Create axios instance for auth requests
const authAPI = axios.create({
  baseURL: `${API_BASE_URL}/api/auth`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Create axios instance for authenticated requests
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth-storage')
    if (token) {
      try {
        const authData = JSON.parse(token)
        if (authData.state?.token) {
          config.headers.Authorization = `Bearer ${authData.state.token}`
        }
      } catch (error) {
        console.error('Error parsing auth token:', error)
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle token expiration
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      try {
        // Try to refresh token
        const refreshResponse = await authAPI.post('/refresh')
        const newToken = refreshResponse.data.data.token
        
        // Update stored token
        const authStorage = JSON.parse(localStorage.getItem('auth-storage') || '{}')
        if (authStorage.state) {
          authStorage.state.token = newToken
          localStorage.setItem('auth-storage', JSON.stringify(authStorage))
        }
        
        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return apiClient(originalRequest)
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.removeItem('auth-storage')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }
    
    return Promise.reject(error)
  }
)

export const authService = {
  /**
   * Login user with email and password
   * @param {Object} credentials - User credentials
   * @param {string} credentials.email - User email
   * @param {string} credentials.password - User password
   * @returns {Promise} API response
   */
  login: (credentials) => {
    return authAPI.post('/login', credentials)
  },

  /**
   * Register new user
   * @param {Object} userData - User registration data
   * @returns {Promise} API response
   */
  register: (userData) => {
    return authAPI.post('/register', userData)
  },

  /**
   * Refresh authentication token
   * @returns {Promise} API response
   */
  refreshToken: () => {
    return authAPI.post('/refresh')
  },

  /**
   * Validate current token
   * @returns {Promise} API response
   */
  validateToken: () => {
    return authAPI.post('/validate')
  },

  /**
   * Logout user
   * @returns {Promise} API response
   */
  logout: () => {
    return authAPI.post('/logout')
  },

  /**
   * Set authentication token in axios defaults
   * @param {string} token - JWT token
   */
  setAuthToken: (token) => {
    if (token) {
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
      authAPI.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }
  },

  /**
   * Remove authentication token from axios defaults
   */
  removeAuthToken: () => {
    delete apiClient.defaults.headers.common['Authorization']
    delete authAPI.defaults.headers.common['Authorization']
  }
}

// Export the configured axios instance for other services
export { apiClient }

export default authService
