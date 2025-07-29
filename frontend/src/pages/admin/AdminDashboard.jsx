import { useQuery } from '@tanstack/react-query'
import { productService } from '../../services/productService'
import { userService } from '../../services/userService'
import LoadingSpinner from '../../components/ui/LoadingSpinner'
import { 
  Package, 
  Users, 
  ShoppingCart, 
  TrendingUp,
  AlertTriangle,
  DollarSign
} from 'lucide-react'

/**
 * Admin dashboard with statistics and overview.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const AdminDashboard = () => {
  // Fetch product statistics
  const { data: productStats, isLoading: isLoadingProducts } = useQuery({
    queryKey: ['admin', 'product-statistics'],
    queryFn: productService.getProductStatistics
  })

  // Fetch user statistics
  const { data: userStats, isLoading: isLoadingUsers } = useQuery({
    queryKey: ['admin', 'user-statistics'],
    queryFn: userService.getUserStatistics
  })

  // Fetch low stock products
  const { data: lowStockProducts, isLoading: isLoadingLowStock } = useQuery({
    queryKey: ['admin', 'low-stock-products'],
    queryFn: () => productService.getLowStockProducts(10)
  })

  const isLoading = isLoadingProducts || isLoadingUsers || isLoadingLowStock

  const stats = [
    {
      name: 'Total Products',
      value: productStats?.data?.data?.totalProducts || 0,
      icon: Package,
      color: 'bg-blue-500',
      change: '+12%',
      changeType: 'positive'
    },
    {
      name: 'Total Users',
      value: userStats?.data?.data?.totalUsers || 0,
      icon: Users,
      color: 'bg-green-500',
      change: '+8%',
      changeType: 'positive'
    },
    {
      name: 'Low Stock Items',
      value: lowStockProducts?.data?.data?.length || 0,
      icon: AlertTriangle,
      color: 'bg-yellow-500',
      change: '-2%',
      changeType: 'negative'
    },
    {
      name: 'Average Price',
      value: `$${productStats?.data?.data?.averagePrice?.toFixed(2) || '0.00'}`,
      icon: DollarSign,
      color: 'bg-purple-500',
      change: '+5%',
      changeType: 'positive'
    }
  ]

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Welcome to the admin dashboard</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon
          return (
            <div key={stat.name} className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className={`${stat.color} rounded-md p-3`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-2xl font-semibold text-gray-900">{stat.value}</p>
                </div>
              </div>
              <div className="mt-4">
                <span className={`text-sm font-medium ${
                  stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {stat.change}
                </span>
                <span className="text-sm text-gray-500 ml-1">from last month</span>
              </div>
            </div>
          )
        })}
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Low Stock Products */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900 flex items-center">
              <AlertTriangle className="w-5 h-5 text-yellow-500 mr-2" />
              Low Stock Products
            </h2>
          </div>
          <div className="p-6">
            {lowStockProducts?.data?.data?.length > 0 ? (
              <div className="space-y-4">
                {lowStockProducts.data.data.slice(0, 5).map((product) => (
                  <div key={product.id} className="flex items-center justify-between">
                    <div>
                      <p className="font-medium text-gray-900">{product.name}</p>
                      <p className="text-sm text-gray-600">${product.price}</p>
                    </div>
                    <div className="text-right">
                      <p className={`text-sm font-medium ${
                        product.stockQuantity === 0 ? 'text-red-600' : 'text-yellow-600'
                      }`}>
                        {product.stockQuantity === 0 ? 'Out of Stock' : `${product.stockQuantity} left`}
                      </p>
                    </div>
                  </div>
                ))}
                {lowStockProducts.data.data.length > 5 && (
                  <p className="text-sm text-gray-500 text-center pt-2">
                    +{lowStockProducts.data.data.length - 5} more items
                  </p>
                )}
              </div>
            ) : (
              <p className="text-gray-500 text-center py-4">No low stock products</p>
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">Quick Actions</h2>
          </div>
          <div className="p-6">
            <div className="grid grid-cols-1 gap-4">
              <button
                onClick={() => window.location.href = '/admin/products/new'}
                className="flex items-center justify-center px-4 py-3 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 transition-colors"
              >
                <Package className="w-5 h-5 mr-2" />
                Add New Product
              </button>
              <button
                onClick={() => window.location.href = '/admin/products'}
                className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
              >
                <ShoppingCart className="w-5 h-5 mr-2" />
                Manage Products
              </button>
              <button
                onClick={() => window.location.href = '/admin/users'}
                className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
              >
                <Users className="w-5 h-5 mr-2" />
                Manage Users
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center">
            <TrendingUp className="w-5 h-5 text-blue-500 mr-2" />
            System Overview
          </h2>
        </div>
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <p className="text-2xl font-bold text-blue-600">
                {productStats?.data?.data?.totalProducts || 0}
              </p>
              <p className="text-sm text-gray-600">Total Products</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-green-600">
                {productStats?.data?.data?.inStockProducts || 0}
              </p>
              <p className="text-sm text-gray-600">In Stock</p>
            </div>
            <div className="text-center">
              <p className="text-2xl font-bold text-red-600">
                {productStats?.data?.data?.outOfStockProducts || 0}
              </p>
              <p className="text-sm text-gray-600">Out of Stock</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
