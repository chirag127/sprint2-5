package com.grocerystore.repository;

import com.grocerystore.entity.Order;
import com.grocerystore.entity.OrderItem;
import com.grocerystore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for OrderItem entity operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Find order items by order.
     * 
     * @param order the order
     * @return list of order items for the order
     */
    List<OrderItem> findByOrder(Order order);

    /**
     * Find order items by order ID.
     * 
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItem> findByOrderId(UUID orderId);

    /**
     * Find order items by product.
     * 
     * @param product the product
     * @return list of order items for the product
     */
    List<OrderItem> findByProduct(Product product);

    /**
     * Find order items by product ID.
     * 
     * @param productId the product ID
     * @return list of order items for the product
     */
    List<OrderItem> findByProductId(UUID productId);

    /**
     * Find order items by product with pagination.
     * 
     * @param product the product
     * @param pageable pagination information
     * @return page of order items for the product
     */
    Page<OrderItem> findByProduct(Product product, Pageable pageable);

    /**
     * Count order items by order.
     * 
     * @param order the order
     * @return count of order items for the order
     */
    long countByOrder(Order order);

    /**
     * Count order items by product.
     * 
     * @param product the product
     * @return count of order items for the product
     */
    long countByProduct(Product product);

    /**
     * Calculate total quantity sold for a product.
     * 
     * @param product the product
     * @return total quantity sold
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.product = :product")
    long calculateTotalQuantitySold(@Param("product") Product product);

    /**
     * Calculate total quantity sold for a product by ID.
     * 
     * @param productId the product ID
     * @return total quantity sold
     */
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.product.id = :productId")
    long calculateTotalQuantitySoldById(@Param("productId") UUID productId);

    /**
     * Find most popular products by quantity sold.
     * 
     * @param pageable pagination information
     * @return page of products ordered by total quantity sold
     */
    @Query("SELECT oi.product FROM OrderItem oi " +
           "GROUP BY oi.product " +
           "ORDER BY SUM(oi.quantity) DESC")
    Page<Product> findMostPopularProducts(Pageable pageable);

    /**
     * Find best-selling products by revenue.
     * 
     * @param pageable pagination information
     * @return page of products ordered by total revenue
     */
    @Query("SELECT oi.product FROM OrderItem oi " +
           "GROUP BY oi.product " +
           "ORDER BY SUM(oi.price * oi.quantity) DESC")
    Page<Product> findBestSellingProducts(Pageable pageable);

    /**
     * Calculate total revenue for a product.
     * 
     * @param product the product
     * @return total revenue for the product
     */
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0) FROM OrderItem oi WHERE oi.product = :product")
    java.math.BigDecimal calculateProductRevenue(@Param("product") Product product);

    /**
     * Find order items for completed orders only.
     * 
     * @return list of order items from completed orders
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.status = 'COMPLETED'")
    List<OrderItem> findCompletedOrderItems();

    /**
     * Find order items for completed orders with pagination.
     * 
     * @param pageable pagination information
     * @return page of order items from completed orders
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.status = 'COMPLETED'")
    Page<OrderItem> findCompletedOrderItems(Pageable pageable);

    /**
     * Find order items by user ID (through order relationship).
     * 
     * @param userId the user ID
     * @return list of order items for the user
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    List<OrderItem> findByUserId(@Param("userId") UUID userId);

    /**
     * Find order items by user ID with pagination.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of order items for the user
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    Page<OrderItem> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Check if a user has purchased a specific product.
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @return true if the user has purchased the product, false otherwise
     */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
           "WHERE oi.order.user.id = :userId AND oi.product.id = :productId " +
           "AND oi.order.status = 'COMPLETED'")
    boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") UUID productId);

    /**
     * Find products purchased by a user.
     * 
     * @param userId the user ID
     * @return list of products purchased by the user
     */
    @Query("SELECT DISTINCT oi.product FROM OrderItem oi " +
           "WHERE oi.order.user.id = :userId AND oi.order.status = 'COMPLETED'")
    List<Product> findProductsPurchasedByUser(@Param("userId") UUID userId);
}
