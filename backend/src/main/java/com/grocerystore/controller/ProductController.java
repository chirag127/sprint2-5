package com.grocerystore.controller;

import com.grocerystore.dto.common.ApiResponse;
import com.grocerystore.dto.common.PageResponse;
import com.grocerystore.dto.product.CreateProductRequest;
import com.grocerystore.dto.product.ProductResponse;
import com.grocerystore.dto.product.UpdateProductRequest;
import com.grocerystore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for product operations.
 *
 * @author Chirag Singhal
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Product management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Get all products with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return paginated list of products
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Get all products with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.info("Getting all products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> products = productService.getAllProducts(pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", pageResponse));
    }

    /**
     * Get product by ID.
     *
     * @param productId product ID
     * @return product details
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID productId) {
        logger.info("Getting product by ID: {}", productId);

        ProductResponse productResponse = productService.getProductById(productId);

        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", productResponse));
    }

    /**
     * Get products in stock.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of in-stock products
     */
    @GetMapping("/in-stock")
    @Operation(summary = "Get in-stock products", description = "Get products that are currently in stock")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getInStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Getting in-stock products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponse> products = productService.getInStockProducts(pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("In-stock products retrieved successfully", pageResponse));
    }

    /**
     * Search products by name or description.
     *
     * @param searchTerm search term
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of products
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name or description")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Searching products with term: {}", searchTerm);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponse> products = productService.searchProducts(searchTerm, pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Product search completed", pageResponse));
    }

    /**
     * Get products within price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of products
     */
    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Get products within specified price range")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Getting products by price range: {} - {}", minPrice, maxPrice);

        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Page<ProductResponse> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Products retrieved by price range", pageResponse));
    }

    /**
     * Get top-rated products.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of top-rated products
     */
    @GetMapping("/top-rated")
    @Operation(summary = "Get top-rated products", description = "Get products with highest ratings")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getTopRatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Getting top-rated products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getTopRatedProducts(pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Top-rated products retrieved successfully", pageResponse));
    }

    /**
     * Get most reviewed products.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of most reviewed products
     */
    @GetMapping("/most-reviewed")
    @Operation(summary = "Get most reviewed products", description = "Get products with most reviews")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getMostReviewedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Getting most reviewed products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getMostReviewedProducts(pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Most reviewed products retrieved successfully", pageResponse));
    }

    /**
     * Get recently added products.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return paginated list of recent products
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent products", description = "Get recently added products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getRecentProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        logger.info("Getting recent products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getRecentProducts(pageable);
        PageResponse<ProductResponse> pageResponse = PageResponse.fromPage(products);

        return ResponseEntity.ok(ApiResponse.success("Recent products retrieved successfully", pageResponse));
    }

    // Admin endpoints

    /**
     * Create a new product (admin only).
     *
     * @param request create product request
     * @return created product details
     */
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        logger.info("Creating new product: {}", request.getName());

        ProductResponse productResponse = productService.createProduct(request);

        return ResponseEntity.ok(ApiResponse.success("Product created successfully", productResponse));
    }

    /**
     * Update product (admin only).
     *
     * @param productId product ID
     * @param request update product request
     * @return updated product details
     */
    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Update an existing product (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {

        logger.info("Updating product: {}", productId);

        ProductResponse productResponse = productService.updateProduct(productId, request);

        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productResponse));
    }

    /**
     * Update product stock (admin only).
     *
     * @param productId product ID
     * @param quantity new quantity
     * @return updated product details
     */
    @PutMapping("/{productId}/stock")
    @Operation(summary = "Update product stock", description = "Update product stock quantity (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity) {

        logger.info("Updating stock for product: {} to quantity: {}", productId, quantity);

        ProductResponse productResponse = productService.updateProductStock(productId, quantity);

        return ResponseEntity.ok(ApiResponse.success("Product stock updated successfully", productResponse));
    }

    /**
     * Delete product (admin only).
     *
     * @param productId product ID
     * @return success message
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Delete a product (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID productId) {
        logger.info("Deleting product: {}", productId);

        productService.deleteProduct(productId);

        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    /**
     * Get low stock products (admin only).
     *
     * @param threshold stock threshold
     * @return list of low stock products
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Get products with low stock (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {

        logger.info("Getting low stock products with threshold: {}", threshold);

        List<ProductResponse> products = productService.getLowStockProducts(threshold);

        return ResponseEntity.ok(ApiResponse.success("Low stock products retrieved successfully", products));
    }

    /**
     * Get product statistics (admin only).
     *
     * @return product statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get product statistics", description = "Get product statistics (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductService.ProductStatistics>> getProductStatistics() {
        logger.info("Getting product statistics");

        ProductService.ProductStatistics statistics = productService.getProductStatistics();

        return ResponseEntity.ok(ApiResponse.success("Product statistics retrieved successfully", statistics));
    }
