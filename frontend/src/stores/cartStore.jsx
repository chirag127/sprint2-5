import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import toast from 'react-hot-toast';

/**
 * Shopping cart store using Zustand with persistence.
 * Manages cart items, quantities, and cart operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const useCartStore = create(
  persist(
    (set, get) => ({
      // State
      items: [],
      isOpen: false,

      // Actions
      addItem: (product, quantity = 1) => {
        const { items } = get();
        const existingItem = items.find(item => item.id === product.id);

        if (existingItem) {
          // Update quantity if item already exists
          set({
            items: items.map(item =>
              item.id === product.id
                ? { ...item, quantity: item.quantity + quantity }
                : item
            )
          });
          toast.success(`Updated ${product.name} quantity in cart`);
        } else {
          // Add new item to cart
          set({
            items: [...items, { ...product, quantity }]
          });
          toast.success(`Added ${product.name} to cart`);
        }
      },

      removeItem: (productId) => {
        const { items } = get();
        const item = items.find(item => item.id === productId);
        
        set({
          items: items.filter(item => item.id !== productId)
        });
        
        if (item) {
          toast.success(`Removed ${item.name} from cart`);
        }
      },

      updateQuantity: (productId, quantity) => {
        if (quantity <= 0) {
          get().removeItem(productId);
          return;
        }

        const { items } = get();
        set({
          items: items.map(item =>
            item.id === productId
              ? { ...item, quantity }
              : item
          )
        });
      },

      clearCart: () => {
        set({ items: [] });
        toast.success('Cart cleared');
      },

      toggleCart: () => {
        set(state => ({ isOpen: !state.isOpen }));
      },

      openCart: () => {
        set({ isOpen: true });
      },

      closeCart: () => {
        set({ isOpen: false });
      },

      // Computed values
      getTotalItems: () => {
        const { items } = get();
        return items.reduce((total, item) => total + item.quantity, 0);
      },

      getTotalPrice: () => {
        const { items } = get();
        return items.reduce((total, item) => total + (item.price * item.quantity), 0);
      },

      getItemQuantity: (productId) => {
        const { items } = get();
        const item = items.find(item => item.id === productId);
        return item ? item.quantity : 0;
      },

      isInCart: (productId) => {
        const { items } = get();
        return items.some(item => item.id === productId);
      },

      // Get cart items formatted for order creation
      getOrderItems: () => {
        const { items } = get();
        return items.map(item => ({
          productId: item.id,
          quantity: item.quantity
        }));
      },

      // Validate cart before checkout
      validateCart: () => {
        const { items } = get();
        
        if (items.length === 0) {
          toast.error('Your cart is empty');
          return false;
        }

        // Check for items with zero or negative quantities
        const invalidItems = items.filter(item => item.quantity <= 0);
        if (invalidItems.length > 0) {
          toast.error('Some items have invalid quantities');
          return false;
        }

        // Check stock availability (this would typically be done on the server)
        const outOfStockItems = items.filter(item => 
          item.stockQuantity !== undefined && item.quantity > item.stockQuantity
        );
        
        if (outOfStockItems.length > 0) {
          const itemNames = outOfStockItems.map(item => item.name).join(', ');
          toast.error(`Insufficient stock for: ${itemNames}`);
          return false;
        }

        return true;
      },

      // Sync cart with latest product data
      syncWithProducts: (products) => {
        const { items } = get();
        const updatedItems = items.map(cartItem => {
          const product = products.find(p => p.id === cartItem.id);
          if (product) {
            return {
              ...cartItem,
              name: product.name,
              price: product.price,
              imageUrl: product.imageUrl,
              stockQuantity: product.stockQuantity,
              category: product.category
            };
          }
          return cartItem;
        }).filter(item => {
          // Remove items that are no longer available
          const product = products.find(p => p.id === item.id);
          return product && product.stockQuantity > 0;
        });

        if (updatedItems.length !== items.length) {
          set({ items: updatedItems });
          toast.info('Cart updated with latest product information');
        }
      }
    }),
    {
      name: 'grocery-cart-storage',
      partialize: (state) => ({
        items: state.items
      })
    }
  )
);

export default useCartStore;
