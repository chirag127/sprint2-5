import { Link } from 'react-router-dom'
import { Shield, Home, ArrowLeft } from 'lucide-react'

/**
 * 403 Unauthorized page component.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const UnauthorizedPage = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="text-center">
          <div className="mx-auto w-24 h-24 bg-red-100 rounded-full flex items-center justify-center mb-6">
            <Shield className="w-12 h-12 text-red-600" />
          </div>
          
          <h1 className="text-6xl font-bold text-red-600">403</h1>
          <h2 className="mt-4 text-3xl font-bold text-gray-900">Access Denied</h2>
          <p className="mt-4 text-lg text-gray-600">
            You don't have permission to access this resource.
          </p>
          <p className="mt-2 text-sm text-gray-500">
            Please contact an administrator if you believe this is an error.
          </p>
          
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/"
              className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-green-600 hover:bg-green-700 transition-colors"
            >
              <Home className="w-5 h-5 mr-2" />
              Go home
            </Link>
            <button
              onClick={() => window.history.back()}
              className="inline-flex items-center px-6 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 transition-colors"
            >
              <ArrowLeft className="w-5 h-5 mr-2" />
              Go back
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default UnauthorizedPage
