import React from 'react'
import { Link } from 'react-router-dom'
import { Star, ShoppingCart, Plus, Minus } from 'lucide-react'
import useCartStore from '../../stores/cartStore'

/**
 * Product card component for displaying product information in a grid.
 *
 * @author Chirag Singhal
 * @version 1.0.0
 */
const ProductCard = ({ product }) => {
  const {
    id,
    name,
    description,
    price,
    imageUrl,
    stockQuantity,
    averageRating,
    reviewCount
  } = product

  const { addItem, updateQuantity, getItemQuantity, isInCart } = useCartStore()

  const isOutOfStock = stockQuantity === 0
  const isLowStock = stockQuantity > 0 && stockQuantity <= 10
  const cartQuantity = getItemQuantity(product.id)
  const inCart = isInCart(product.id)

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(price)
  }

  const handleAddToCart = (e) => {
    e.preventDefault()
    e.stopPropagation()
    addItem(product, 1)
  }

  const handleUpdateQuantity = (e, newQuantity) => {
    e.preventDefault()
    e.stopPropagation()
    updateQuantity(product.id, newQuantity)
  }

  const renderStars = (rating) => {
    const stars = []
    const fullStars = Math.floor(rating)
    const hasHalfStar = rating % 1 !== 0

    for (let i = 0; i < fullStars; i++) {
      stars.push(
        <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
      )
    }

    if (hasHalfStar) {
      stars.push(
        <Star key="half" className="w-4 h-4 fill-yellow-400 text-yellow-400 opacity-50" />
      )
    }

    const emptyStars = 5 - Math.ceil(rating)
    for (let i = 0; i < emptyStars; i++) {
      stars.push(
        <Star key={`empty-${i}`} className="w-4 h-4 text-gray-300" />
      )
    }

    return stars
  }

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
      {/* Product Image */}
      <Link to={`/products/${id}`} className="block">
        <div className="relative">
          <img
            src={imageUrl || '/api/placeholder/300/200'}
            alt={name}
            className="w-full h-48 object-cover"
            onError={(e) => {
              e.target.src = '/api/placeholder/300/200'
            }}
          />
          {isOutOfStock && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
              <span className="text-white font-semibold text-lg">Out of Stock</span>
            </div>
          )}
          {isLowStock && !isOutOfStock && (
            <div className="absolute top-2 right-2 bg-orange-500 text-white px-2 py-1 rounded text-xs font-semibold">
              Low Stock
            </div>
          )}
        </div>
      </Link>

      {/* Product Info */}
      <div className="p-4">
        <Link to={`/products/${id}`} className="block">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 hover:text-green-600 transition-colors line-clamp-2">
            {name}
          </h3>
        </Link>

        <p className="text-gray-600 text-sm mb-3 line-clamp-2">
          {description}
        </p>

        {/* Rating */}
        {averageRating > 0 && (
          <div className="flex items-center mb-3">
            <div className="flex items-center">
              {renderStars(averageRating)}
            </div>
            <span className="ml-2 text-sm text-gray-600">
              {averageRating.toFixed(1)} ({reviewCount})
            </span>
          </div>
        )}

        {/* Price and Actions */}
        <div className="flex items-center justify-between">
          <div className="flex flex-col">
            <span className="text-2xl font-bold text-green-600">
              {formatPrice(price)}
            </span>
            {stockQuantity > 0 && (
              <span className="text-xs text-gray-500">
                {stockQuantity} in stock
              </span>
            )}
          </div>

          {!inCart ? (
            <button
              onClick={handleAddToCart}
              disabled={isOutOfStock}
              className={`flex items-center space-x-1 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                isOutOfStock
                  ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  : 'bg-green-600 text-white hover:bg-green-700'
              }`}
            >
              <ShoppingCart className="w-4 h-4" />
              <span>{isOutOfStock ? 'Unavailable' : 'Add to Cart'}</span>
            </button>
          ) : (
            <div className="flex items-center space-x-2">
              <button
                onClick={(e) => handleUpdateQuantity(e, cartQuantity - 1)}
                className="p-1 rounded-md bg-gray-200 hover:bg-gray-300 transition-colors"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="px-2 py-1 bg-green-100 text-green-800 rounded-md text-sm font-medium min-w-[2rem] text-center">
                {cartQuantity}
              </span>
              <button
                onClick={(e) => handleUpdateQuantity(e, cartQuantity + 1)}
                disabled={cartQuantity >= stockQuantity}
                className="p-1 rounded-md bg-gray-200 hover:bg-gray-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default ProductCard
