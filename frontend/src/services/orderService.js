import api from './api';

/**
 * Order service for API communication.
 * Handles all order-related API calls.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
class OrderService {
  /**
   * Create a new order.
   * @param {Object} orderData - Order creation data
   * @returns {Promise<Object>} Created order
   */
  async createOrder(orderData) {
    try {
      const response = await api.post('/orders', orderData);
      return response.data;
    } catch (error) {
      console.error('Error creating order:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get customer's order history.
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} Paginated orders
   */
  async getMyOrders(params = {}) {
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortBy || 'orderDate',
        sortDir: params.sortDir || 'desc'
      });

      const response = await api.get(`/orders/my-orders?${queryParams}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching my orders:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get order by ID.
   * @param {string} orderId - Order ID
   * @returns {Promise<Object>} Order details
   */
  async getOrderById(orderId) {
    try {
      const response = await api.get(`/orders/${orderId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching order:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get all orders (admin only).
   * @param {Object} params - Query parameters
   * @returns {Promise<Object>} Paginated orders
   */
  async getAllOrders(params = {}) {
    try {
      const queryParams = new URLSearchParams({
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortBy || 'orderDate',
        sortDir: params.sortDir || 'desc'
      });

      const response = await api.get(`/orders/admin/all?${queryParams}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching all orders:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Update order status (admin only).
   * @param {string} orderId - Order ID
   * @param {string} status - New status
   * @returns {Promise<Object>} Updated order
   */
  async updateOrderStatus(orderId, status) {
    try {
      const response = await api.put(`/orders/${orderId}/status?status=${status}`);
      return response.data;
    } catch (error) {
      console.error('Error updating order status:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get order statistics (admin only).
   * @returns {Promise<Object>} Order statistics
   */
  async getOrderStatistics() {
    try {
      const response = await api.get('/orders/admin/statistics');
      return response.data;
    } catch (error) {
      console.error('Error fetching order statistics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get order status options.
   * @returns {Array} Available order statuses
   */
  getOrderStatuses() {
    return [
      { value: 'PENDING', label: 'Pending', color: 'yellow' },
      { value: 'PROCESSING', label: 'Processing', color: 'blue' },
      { value: 'SHIPPED', label: 'Shipped', color: 'purple' },
      { value: 'DELIVERED', label: 'Delivered', color: 'green' },
      { value: 'CANCELLED', label: 'Cancelled', color: 'red' }
    ];
  }

  /**
   * Get payment method options.
   * @returns {Array} Available payment methods
   */
  getPaymentMethods() {
    return [
      { value: 'CASH_ON_DELIVERY', label: 'Cash on Delivery' },
      { value: 'ONLINE_PAYMENT', label: 'Online Payment' },
      { value: 'CARD_PAYMENT', label: 'Card Payment' }
    ];
  }

  /**
   * Format order status for display.
   * @param {string} status - Order status
   * @returns {Object} Formatted status with label and color
   */
  formatOrderStatus(status) {
    const statuses = this.getOrderStatuses();
    const statusObj = statuses.find(s => s.value === status);
    return statusObj || { value: status, label: status, color: 'gray' };
  }

  /**
   * Format payment method for display.
   * @param {string} method - Payment method
   * @returns {string} Formatted payment method
   */
  formatPaymentMethod(method) {
    const methods = this.getPaymentMethods();
    const methodObj = methods.find(m => m.value === method);
    return methodObj ? methodObj.label : method;
  }

  /**
   * Calculate order total from items.
   * @param {Array} items - Order items
   * @returns {number} Total amount
   */
  calculateOrderTotal(items) {
    return items.reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  /**
   * Validate order data before submission.
   * @param {Object} orderData - Order data to validate
   * @returns {Object} Validation result
   */
  validateOrderData(orderData) {
    const errors = {};

    if (!orderData.orderItems || orderData.orderItems.length === 0) {
      errors.orderItems = 'Order must contain at least one item';
    }

    if (!orderData.deliveryAddress || orderData.deliveryAddress.trim().length < 10) {
      errors.deliveryAddress = 'Delivery address must be at least 10 characters long';
    }

    if (!orderData.contactNumber || !/^[+]?[0-9]{10,15}$/.test(orderData.contactNumber)) {
      errors.contactNumber = 'Please provide a valid contact number';
    }

    if (orderData.orderNotes && orderData.orderNotes.length > 500) {
      errors.orderNotes = 'Order notes cannot exceed 500 characters';
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    };
  }

  /**
   * Handle API errors.
   * @param {Error} error - API error
   * @returns {Error} Formatted error
   */
  handleError(error) {
    if (error.response) {
      // Server responded with error status
      const message = error.response.data?.message || 'An error occurred';
      const status = error.response.status;
      
      if (status === 401) {
        // Unauthorized - redirect to login
        window.location.href = '/login';
        return new Error('Please log in to continue');
      } else if (status === 403) {
        return new Error('You do not have permission to perform this action');
      } else if (status === 404) {
        return new Error('The requested resource was not found');
      } else if (status >= 500) {
        return new Error('Server error. Please try again later.');
      }
      
      return new Error(message);
    } else if (error.request) {
      // Network error
      return new Error('Network error. Please check your connection.');
    } else {
      // Other error
      return new Error(error.message || 'An unexpected error occurred');
    }
  }
}

export default new OrderService();
