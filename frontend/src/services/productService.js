import { apiClient } from './authService'

/**
 * Product service for handling API calls related to product management.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */

export const productService = {
  /**
   * Get all products with pagination
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getAllProducts: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 12,
      sortBy: params.sortBy || 'createdAt',
      sortDir: params.sortDir || 'desc'
    })
    
    return apiClient.get(`/api/products?${queryParams}`)
  },

  /**
   * Get product by ID
   * @param {string} productId - Product ID
   * @returns {Promise} API response
   */
  getProductById: (productId) => {
    return apiClient.get(`/api/products/${productId}`)
  },

  /**
   * Get products in stock
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getInStockProducts: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/in-stock?${queryParams}`)
  },

  /**
   * Search products by name or description
   * @param {string} searchTerm - Search term
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  searchProducts: (searchTerm, params = {}) => {
    const queryParams = new URLSearchParams({
      searchTerm,
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/search?${queryParams}`)
  },

  /**
   * Get products by price range
   * @param {number} minPrice - Minimum price
   * @param {number} maxPrice - Maximum price
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getProductsByPriceRange: (minPrice, maxPrice, params = {}) => {
    const queryParams = new URLSearchParams({
      minPrice,
      maxPrice,
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/price-range?${queryParams}`)
  },

  /**
   * Get top-rated products
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getTopRatedProducts: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/top-rated?${queryParams}`)
  },

  /**
   * Get most reviewed products
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getMostReviewedProducts: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/most-reviewed?${queryParams}`)
  },

  /**
   * Get recent products
   * @param {Object} params - Query parameters
   * @returns {Promise} API response
   */
  getRecentProducts: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 12
    })
    
    return apiClient.get(`/api/products/recent?${queryParams}`)
  },

  // Admin endpoints

  /**
   * Create new product (admin only)
   * @param {Object} productData - Product data
   * @returns {Promise} API response
   */
  createProduct: (productData) => {
    return apiClient.post('/api/products', productData)
  },

  /**
   * Update product (admin only)
   * @param {string} productId - Product ID
   * @param {Object} productData - Product data
   * @returns {Promise} API response
   */
  updateProduct: (productId, productData) => {
    return apiClient.put(`/api/products/${productId}`, productData)
  },

  /**
   * Update product stock (admin only)
   * @param {string} productId - Product ID
   * @param {number} quantity - New quantity
   * @returns {Promise} API response
   */
  updateProductStock: (productId, quantity) => {
    return apiClient.put(`/api/products/${productId}/stock?quantity=${quantity}`)
  },

  /**
   * Delete product (admin only)
   * @param {string} productId - Product ID
   * @returns {Promise} API response
   */
  deleteProduct: (productId) => {
    return apiClient.delete(`/api/products/${productId}`)
  },

  /**
   * Get low stock products (admin only)
   * @param {number} threshold - Stock threshold
   * @returns {Promise} API response
   */
  getLowStockProducts: (threshold = 10) => {
    return apiClient.get(`/api/products/low-stock?threshold=${threshold}`)
  },

  /**
   * Get product statistics (admin only)
   * @returns {Promise} API response
   */
  getProductStatistics: () => {
    return apiClient.get('/api/products/statistics')
  }
}

export default productService
