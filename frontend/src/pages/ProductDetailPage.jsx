import { useParams } from 'react-router-dom'

/**
 * Product detail page component.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
const ProductDetailPage = () => {
  const { id } = useParams()

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="text-center">
        <h1 className="text-2xl font-bold text-gray-900 mb-4">Product Detail</h1>
        <p className="text-gray-600">Product ID: {id}</p>
        <p className="text-sm text-gray-500 mt-4">This page will be implemented in Phase 4</p>
      </div>
    </div>
  )
}

export default ProductDetailPage
