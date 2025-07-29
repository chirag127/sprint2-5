import { apiClient } from './authService'

/**
 * User service for handling API calls related to user management.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */

export const userService = {
  /**
   * Get current user profile
   * @returns {Promise} API response
   */
  getCurrentProfile: () => {
    return apiClient.get('/api/users/profile')
  },

  /**
   * Update current user profile
   * @param {Object} profileData - Profile update data
   * @returns {Promise} API response
   */
  updateProfile: (profileData) => {
    return apiClient.put('/api/users/profile', profileData)
  },

  /**
   * Change user password
   * @param {Object} passwordData - Password change data
   * @returns {Promise} API response
   */
  changePassword: (passwordData) => {
    return apiClient.put('/api/users/change-password', passwordData)
  },

  /**
   * Get user by ID (admin only)
   * @param {string} userId - User ID
   * @returns {Promise} API response
   */
  getUserById: (userId) => {
    return apiClient.get(`/api/users/${userId}`)
  },

  /**
   * Get all users with pagination (admin only)
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getAllUsers: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 10,
      sortBy: params.sortBy || 'createdAt',
      sortDir: params.sortDir || 'desc'
    })
    
    return apiClient.get(`/api/users?${queryParams}`)
  },

  /**
   * Search users by name or email (admin only)
   * @param {string} searchTerm - Search term
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  searchUsers: (searchTerm, params = {}) => {
    const queryParams = new URLSearchParams({
      searchTerm,
      page: params.page || 0,
      size: params.size || 10
    })
    
    return apiClient.get(`/api/users/search?${queryParams}`)
  },

  /**
   * Get users by role (admin only)
   * @param {string} role - User role
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getUsersByRole: (role, params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 10
    })
    
    return apiClient.get(`/api/users/role/${role}?${queryParams}`)
  },

  /**
   * Update user role (admin only)
   * @param {string} userId - User ID
   * @param {string} role - New role
   * @returns {Promise} API response
   */
  updateUserRole: (userId, role) => {
    return apiClient.put(`/api/users/${userId}/role?role=${role}`)
  },

  /**
   * Delete user (admin only)
   * @param {string} userId - User ID
   * @returns {Promise} API response
   */
  deleteUser: (userId) => {
    return apiClient.delete(`/api/users/${userId}`)
  },

  /**
   * Get user statistics (admin only)
   * @returns {Promise} API response
   */
  getUserStatistics: () => {
    return apiClient.get('/api/users/statistics')
  }
}

export default userService
