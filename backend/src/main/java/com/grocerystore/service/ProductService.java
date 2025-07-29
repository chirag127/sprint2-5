package com.grocerystore.service;

import com.grocerystore.dto.product.CreateProductRequest;
import com.grocerystore.dto.product.ProductResponse;
import com.grocerystore.dto.product.UpdateProductRequest;
import com.grocerystore.entity.Product;
import com.grocerystore.exception.BadRequestException;
import com.grocerystore.exception.ResourceNotFoundException;
import com.grocerystore.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for product operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create a new product.
     * 
     * @param request create product request
     * @return created product response DTO
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        logger.info("Creating new product: {}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());

        product = productRepository.save(product);
        
        logger.info("Product created successfully: {} with ID: {}", product.getName(), product.getId());
        
        return ProductResponse.fromProduct(product);
    }

    /**
     * Get product by ID.
     * 
     * @param productId product ID
     * @return product response DTO
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return ProductResponse.fromProduct(product);
    }

    /**
     * Get all products with pagination.
     * 
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Get products in stock with pagination.
     * 
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getInStockProducts(Pageable pageable) {
        Page<Product> products = productRepository.findInStockProducts(pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Search products by name or description.
     * 
     * @param searchTerm search term
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String searchTerm, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(searchTerm, pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Get products within price range.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("Minimum price cannot be greater than maximum price");
        }
        
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Get top-rated products.
     * 
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getTopRatedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findTopRatedProducts(pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Get most reviewed products.
     * 
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getMostReviewedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findMostReviewedProducts(pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Get recently added products.
     * 
     * @param pageable pagination information
     * @return page of product response DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getRecentProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByOrderByCreatedAtDesc(pageable);
        return products.map(ProductResponse::fromProduct);
    }

    /**
     * Update product.
     * 
     * @param productId product ID
     * @param request update product request
     * @return updated product response DTO
     */
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        logger.info("Updating product: {} with ID: {}", product.getName(), productId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());

        product = productRepository.save(product);
        
        logger.info("Product updated successfully: {}", product.getName());
        
        return ProductResponse.fromProduct(product);
    }

    /**
     * Update product stock.
     * 
     * @param productId product ID
     * @param quantity new quantity
     * @return updated product response DTO
     */
    public ProductResponse updateProductStock(UUID productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (quantity < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }
        
        logger.info("Updating stock for product: {} from {} to {}", product.getName(), product.getQuantity(), quantity);

        product.setQuantity(quantity);
        product = productRepository.save(product);
        
        logger.info("Stock updated successfully for product: {}", product.getName());
        
        return ProductResponse.fromProduct(product);
    }

    /**
     * Delete product.
     * 
     * @param productId product ID
     */
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        logger.info("Deleting product: {} with ID: {}", product.getName(), productId);
        
        productRepository.delete(product);
        
        logger.info("Product deleted successfully: {}", product.getName());
    }

    /**
     * Get low stock products.
     * 
     * @param threshold stock threshold
     * @return list of product response DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(int threshold) {
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Get product statistics.
     * 
     * @return product statistics
     */
    @Transactional(readOnly = true)
    public ProductStatistics getProductStatistics() {
        long totalProducts = productRepository.count();
        long inStockProducts = productRepository.countInStockProducts();
        long outOfStockProducts = productRepository.countOutOfStockProducts();
        
        return new ProductStatistics(totalProducts, inStockProducts, outOfStockProducts);
    }

    /**
     * Inner class for product statistics.
     */
    public static class ProductStatistics {
        private final long totalProducts;
        private final long inStockProducts;
        private final long outOfStockProducts;

        public ProductStatistics(long totalProducts, long inStockProducts, long outOfStockProducts) {
            this.totalProducts = totalProducts;
            this.inStockProducts = inStockProducts;
            this.outOfStockProducts = outOfStockProducts;
        }

        public long getTotalProducts() { return totalProducts; }
        public long getInStockProducts() { return inStockProducts; }
        public long getOutOfStockProducts() { return outOfStockProducts; }
    }
}
