import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { productService } from '../services/productService'
import ProductCard from '../components/product/ProductCard'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import { Search, Filter, SortAsc, SortDesc } from 'lucide-react'

/**
 * Products page with search, filter, and pagination functionality.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const ProductsPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [searchTerm, setSearchTerm] = useState(searchParams.get('search') || '')
  const [currentPage, setCurrentPage] = useState(0)
  const [sortBy, setSortBy] = useState('createdAt')
  const [sortDir, setSortDir] = useState('desc')
  const [priceRange, setPriceRange] = useState({ min: '', max: '' })

  const pageSize = 12

  // Get products based on search or all products
  const { data: productsData, isLoading, error } = useQuery({
    queryKey: ['products', searchTerm, currentPage, sortBy, sortDir, priceRange],
    queryFn: () => {
      if (searchTerm) {
        return productService.searchProducts(searchTerm, {
          page: currentPage,
          size: pageSize,
          sortBy,
          sortDir
        })
      } else if (priceRange.min || priceRange.max) {
        return productService.getProductsByPriceRange(
          priceRange.min || 0,
          priceRange.max || 999999,
          {
            page: currentPage,
            size: pageSize,
            sortBy,
            sortDir
          }
        )
      } else {
        return productService.getAllProducts({
          page: currentPage,
          size: pageSize,
          sortBy,
          sortDir
        })
      }
    }
  })

  const products = productsData?.data?.data?.content || []
  const totalPages = productsData?.data?.data?.totalPages || 0
  const totalElements = productsData?.data?.data?.totalElements || 0

  // Handle search
  const handleSearch = (e) => {
    e.preventDefault()
    setCurrentPage(0)
    if (searchTerm) {
      setSearchParams({ search: searchTerm })
    } else {
      setSearchParams({})
    }
  }

  // Handle sort change
  const handleSortChange = (newSortBy) => {
    if (sortBy === newSortBy) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc')
    } else {
      setSortBy(newSortBy)
      setSortDir('asc')
    }
    setCurrentPage(0)
  }

  // Handle price filter
  const handlePriceFilter = () => {
    setCurrentPage(0)
  }

  // Clear filters
  const clearFilters = () => {
    setSearchTerm('')
    setPriceRange({ min: '', max: '' })
    setSortBy('createdAt')
    setSortDir('desc')
    setCurrentPage(0)
    setSearchParams({})
  }

  // Update search term from URL params
  useEffect(() => {
    const search = searchParams.get('search')
    if (search) {
      setSearchTerm(search)
    }
  }, [searchParams])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Products</h1>
        
        {/* Search and Filters */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          {/* Search */}
          <form onSubmit={handleSearch} className="mb-4">
            <div className="relative">
              <input
                type="text"
                placeholder="Search products..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              />
              <Search className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
              <button
                type="submit"
                className="absolute right-2 top-2 px-4 py-1.5 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
              >
                Search
              </button>
            </div>
          </form>

          {/* Filters Row */}
          <div className="flex flex-col lg:flex-row gap-4 items-start lg:items-center justify-between">
            {/* Price Range */}
            <div className="flex items-center space-x-2">
              <span className="text-sm font-medium text-gray-700">Price:</span>
              <input
                type="number"
                placeholder="Min"
                value={priceRange.min}
                onChange={(e) => setPriceRange(prev => ({ ...prev, min: e.target.value }))}
                className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
              />
              <span className="text-gray-500">-</span>
              <input
                type="number"
                placeholder="Max"
                value={priceRange.max}
                onChange={(e) => setPriceRange(prev => ({ ...prev, max: e.target.value }))}
                className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
              />
              <button
                onClick={handlePriceFilter}
                className="px-3 py-1 bg-gray-100 text-gray-700 rounded text-sm hover:bg-gray-200 transition-colors"
              >
                Apply
              </button>
            </div>

            {/* Sort Options */}
            <div className="flex items-center space-x-4">
              <span className="text-sm font-medium text-gray-700">Sort by:</span>
              <div className="flex space-x-2">
                <button
                  onClick={() => handleSortChange('name')}
                  className={`flex items-center space-x-1 px-3 py-1 rounded text-sm transition-colors ${
                    sortBy === 'name' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  <span>Name</span>
                  {sortBy === 'name' && (
                    sortDir === 'asc' ? <SortAsc className="w-4 h-4" /> : <SortDesc className="w-4 h-4" />
                  )}
                </button>
                <button
                  onClick={() => handleSortChange('price')}
                  className={`flex items-center space-x-1 px-3 py-1 rounded text-sm transition-colors ${
                    sortBy === 'price' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  <span>Price</span>
                  {sortBy === 'price' && (
                    sortDir === 'asc' ? <SortAsc className="w-4 h-4" /> : <SortDesc className="w-4 h-4" />
                  )}
                </button>
                <button
                  onClick={() => handleSortChange('createdAt')}
                  className={`flex items-center space-x-1 px-3 py-1 rounded text-sm transition-colors ${
                    sortBy === 'createdAt' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  <span>Newest</span>
                  {sortBy === 'createdAt' && (
                    sortDir === 'asc' ? <SortAsc className="w-4 h-4" /> : <SortDesc className="w-4 h-4" />
                  )}
                </button>
              </div>
              
              <button
                onClick={clearFilters}
                className="px-3 py-1 text-sm text-gray-600 hover:text-gray-800 transition-colors"
              >
                Clear Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Results Info */}
      <div className="mb-6">
        <p className="text-gray-600">
          {isLoading ? 'Loading...' : `Showing ${products.length} of ${totalElements} products`}
          {searchTerm && ` for "${searchTerm}"`}
        </p>
      </div>

      {/* Products Grid */}
      {isLoading ? (
        <div className="flex justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      ) : error ? (
        <div className="text-center py-12">
          <p className="text-red-600">Error loading products. Please try again.</p>
        </div>
      ) : products.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600">No products found.</p>
          {(searchTerm || priceRange.min || priceRange.max) && (
            <button
              onClick={clearFilters}
              className="mt-4 text-green-600 hover:text-green-700 font-medium"
            >
              Clear filters to see all products
            </button>
          )}
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-8">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center">
              <nav className="flex items-center space-x-2">
                <button
                  onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                  disabled={currentPage === 0}
                  className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  const pageNum = currentPage < 3 ? i : currentPage - 2 + i
                  if (pageNum >= totalPages) return null
                  
                  return (
                    <button
                      key={pageNum}
                      onClick={() => setCurrentPage(pageNum)}
                      className={`px-3 py-2 text-sm font-medium rounded-md ${
                        currentPage === pageNum
                          ? 'text-white bg-green-600'
                          : 'text-gray-700 bg-white border border-gray-300 hover:bg-gray-50'
                      }`}
                    >
                      {pageNum + 1}
                    </button>
                  )
                })}
                
                <button
                  onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                  disabled={currentPage === totalPages - 1}
                  className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </nav>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default ProductsPage
