import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { CreditCard, MapPin, Phone, FileText, ShoppingBag } from 'lucide-react';
import toast from 'react-hot-toast';
import useCartStore from '../../stores/cartStore';
import useAuthStore from '../../stores/authStore';
import orderService from '../../services/orderService';
import MainLayout from '../../components/layout/MainLayout';
import LoadingSpinner from '../../components/ui/LoadingSpinner';

/**
 * Checkout form validation schema.
 */
const checkoutSchema = z.object({
  deliveryAddress: z
    .string()
    .min(10, 'Delivery address must be at least 10 characters')
    .max(500, 'Delivery address cannot exceed 500 characters'),
  contactNumber: z
    .string()
    .regex(/^[+]?[0-9]{10,15}$/, 'Please enter a valid phone number'),
  orderNotes: z
    .string()
    .max(500, 'Order notes cannot exceed 500 characters')
    .optional()
});

/**
 * Checkout page component.
 * Handles order placement with delivery details.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const CheckoutPage = () => {
  const navigate = useNavigate();
  const { items, clearCart, getTotalItems, getTotalPrice, getOrderItems, validateCart } = useCartStore();
  const { user } = useAuthStore();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm({
    resolver: zodResolver(checkoutSchema),
    defaultValues: {
      deliveryAddress: user?.address || '',
      contactNumber: user?.phoneNumber || '',
      orderNotes: ''
    }
  });

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(price);
  };

  const onSubmit = async (data) => {
    if (!validateCart()) {
      return;
    }

    setIsSubmitting(true);
    
    try {
      const orderData = {
        orderItems: getOrderItems(),
        deliveryAddress: data.deliveryAddress,
        contactNumber: data.contactNumber,
        orderNotes: data.orderNotes || null
      };

      const response = await orderService.createOrder(orderData);
      
      if (response.success) {
        clearCart();
        toast.success('Order placed successfully!');
        navigate(`/orders/${response.data.id}`, { 
          state: { orderCreated: true } 
        });
      } else {
        throw new Error(response.message || 'Failed to place order');
      }
    } catch (error) {
      console.error('Checkout error:', error);
      toast.error(error.message || 'Failed to place order. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Redirect if cart is empty
  if (items.length === 0) {
    navigate('/cart');
    return null;
  }

  return (
    <MainLayout>
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-800 mb-8">Checkout</h1>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Checkout Form */}
            <div className="space-y-6">
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                {/* Delivery Address */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <MapPin className="h-5 w-5" />
                    Delivery Address
                  </h2>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Full Address *
                    </label>
                    <textarea
                      {...register('deliveryAddress')}
                      rows={4}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter your complete delivery address including landmark, city, and pincode"
                    />
                    {errors.deliveryAddress && (
                      <p className="text-red-500 text-sm mt-1">{errors.deliveryAddress.message}</p>
                    )}
                  </div>
                </div>

                {/* Contact Information */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <Phone className="h-5 w-5" />
                    Contact Information
                  </h2>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Phone Number *
                    </label>
                    <input
                      type="tel"
                      {...register('contactNumber')}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter your phone number"
                    />
                    {errors.contactNumber && (
                      <p className="text-red-500 text-sm mt-1">{errors.contactNumber.message}</p>
                    )}
                  </div>
                </div>

                {/* Order Notes */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <FileText className="h-5 w-5" />
                    Order Notes (Optional)
                  </h2>
                  
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Special Instructions
                    </label>
                    <textarea
                      {...register('orderNotes')}
                      rows={3}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Any special instructions for delivery (optional)"
                    />
                    {errors.orderNotes && (
                      <p className="text-red-500 text-sm mt-1">{errors.orderNotes.message}</p>
                    )}
                  </div>
                </div>

                {/* Payment Method */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
                    <CreditCard className="h-5 w-5" />
                    Payment Method
                  </h2>
                  
                  <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                    <div className="flex items-center gap-3">
                      <div className="w-4 h-4 bg-green-600 rounded-full"></div>
                      <div>
                        <p className="font-medium text-green-800">Cash on Delivery</p>
                        <p className="text-sm text-green-600">Pay when your order arrives</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Submit Button */}
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full bg-green-600 text-white py-3 px-4 rounded-lg hover:bg-green-700 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  {isSubmitting ? (
                    <>
                      <LoadingSpinner size="sm" />
                      Placing Order...
                    </>
                  ) : (
                    'Place Order'
                  )}
                </button>
              </form>
            </div>

            {/* Order Summary */}
            <div className="lg:sticky lg:top-4 lg:self-start">
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
                
                {/* Items */}
                <div className="space-y-3 mb-6 max-h-64 overflow-y-auto">
                  {items.map((item) => (
                    <div key={item.id} className="flex items-center gap-3">
                      <div className="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
                        {item.imageUrl ? (
                          <img
                            src={item.imageUrl}
                            alt={item.name}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-gray-400">
                            <ShoppingBag className="h-4 w-4" />
                          </div>
                        )}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="font-medium text-sm truncate">{item.name}</p>
                        <p className="text-gray-600 text-sm">Qty: {item.quantity}</p>
                      </div>
                      <p className="font-semibold text-sm">
                        {formatPrice(item.price * item.quantity)}
                      </p>
                    </div>
                  ))}
                </div>

                {/* Totals */}
                <div className="space-y-3 border-t pt-4">
                  <div className="flex justify-between">
                    <span>Subtotal ({getTotalItems()} items)</span>
                    <span>{formatPrice(getTotalPrice())}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Delivery Fee</span>
                    <span className="text-green-600">Free</span>
                  </div>
                  <div className="flex justify-between text-lg font-semibold border-t pt-3">
                    <span>Total</span>
                    <span className="text-green-600">{formatPrice(getTotalPrice())}</span>
                  </div>
                </div>

                <div className="mt-6 p-4 bg-blue-50 rounded-lg">
                  <p className="text-sm text-blue-800">
                    <strong>Estimated Delivery:</strong> 2-3 business days
                  </p>
                  <p className="text-sm text-blue-600 mt-1">
                    Free delivery on all orders
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default CheckoutPage;
