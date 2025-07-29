import { Link } from 'react-router-dom'
import { ShoppingCart, Mail, Phone, MapPin } from 'lucide-react'

/**
 * Footer component with links and company information.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const Footer = () => {
  return (
    <footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Company Info */}
          <div className="col-span-1 md:col-span-2">
            <div className="flex items-center space-x-2 mb-4">
              <div className="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center">
                <ShoppingCart className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-bold">GroceryStore</span>
            </div>
            <p className="text-gray-300 mb-4 max-w-md">
              Your trusted online grocery store delivering fresh, quality products 
              right to your doorstep. Shop from thousands of products with fast 
              and reliable delivery.
            </p>
            <div className="space-y-2">
              <div className="flex items-center space-x-2 text-gray-300">
                <Mail className="w-4 h-4" />
                <span>support@grocerystore.com</span>
              </div>
              <div className="flex items-center space-x-2 text-gray-300">
                <Phone className="w-4 h-4" />
                <span>+1 (555) 123-4567</span>
              </div>
              <div className="flex items-center space-x-2 text-gray-300">
                <MapPin className="w-4 h-4" />
                <span>123 Grocery Street, Food City, FC 12345</span>
              </div>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <Link 
                  to="/products" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  All Products
                </Link>
              </li>
              <li>
                <Link 
                  to="/products?category=fresh" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Fresh Produce
                </Link>
              </li>
              <li>
                <Link 
                  to="/products?category=dairy" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Dairy Products
                </Link>
              </li>
              <li>
                <Link 
                  to="/products?category=meat" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Meat & Seafood
                </Link>
              </li>
              <li>
                <Link 
                  to="/products?category=pantry" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Pantry Staples
                </Link>
              </li>
            </ul>
          </div>

          {/* Customer Service */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Customer Service</h3>
            <ul className="space-y-2">
              <li>
                <a 
                  href="#" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Help Center
                </a>
              </li>
              <li>
                <a 
                  href="#" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Delivery Info
                </a>
              </li>
              <li>
                <a 
                  href="#" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Return Policy
                </a>
              </li>
              <li>
                <a 
                  href="#" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Contact Us
                </a>
              </li>
              <li>
                <a 
                  href="#" 
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Track Order
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom section */}
        <div className="border-t border-gray-800 mt-8 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="text-gray-300 text-sm">
              © 2024 GroceryStore. All rights reserved. Built by Chirag Singhal.
            </div>
            <div className="flex space-x-6 mt-4 md:mt-0">
              <a 
                href="#" 
                className="text-gray-300 hover:text-white text-sm transition-colors"
              >
                Privacy Policy
              </a>
              <a 
                href="#" 
                className="text-gray-300 hover:text-white text-sm transition-colors"
              >
                Terms of Service
              </a>
              <a 
                href="#" 
                className="text-gray-300 hover:text-white text-sm transition-colors"
              >
                Cookie Policy
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  )
}

export default Footer
