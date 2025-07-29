package com.grocerystore.repository;

import com.grocerystore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Product entity operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Search products by name containing the search term (case-insensitive).
     * 
     * @param name the search term
     * @return list of products whose name contains the search term
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search products by name containing the search term with pagination (case-insensitive).
     * 
     * @param name the search term
     * @param pageable pagination information
     * @return page of products whose name contains the search term
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find products that are in stock (quantity > 0).
     * 
     * @return list of products in stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    List<Product> findInStockProducts();

    /**
     * Find products that are in stock with pagination.
     * 
     * @param pageable pagination information
     * @return page of products in stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    Page<Product> findInStockProducts(Pageable pageable);

    /**
     * Find products that are out of stock (quantity = 0).
     * 
     * @return list of products out of stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity = 0")
    List<Product> findOutOfStockProducts();

    /**
     * Find products within a price range.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products within the price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products within a price range with pagination.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return page of products within the price range
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find products created after a specific date.
     * 
     * @param date the date threshold
     * @return list of products created after the specified date
     */
    List<Product> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Search products by name or description containing the search term (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find products with low stock (quantity below threshold).
     * 
     * @param threshold the stock threshold
     * @return list of products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold AND p.quantity > 0")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    /**
     * Count products that are in stock.
     * 
     * @return count of products in stock
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0")
    long countInStockProducts();

    /**
     * Count products that are out of stock.
     * 
     * @return count of products out of stock
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = 0")
    long countOutOfStockProducts();

    /**
     * Find top-rated products based on average review rating.
     * 
     * @param pageable pagination information
     * @return page of top-rated products
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r " +
           "GROUP BY p.id " +
           "ORDER BY AVG(COALESCE(r.rating, 0)) DESC")
    Page<Product> findTopRatedProducts(Pageable pageable);

    /**
     * Find most reviewed products.
     * 
     * @param pageable pagination information
     * @return page of most reviewed products
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r " +
           "GROUP BY p.id " +
           "ORDER BY COUNT(r.id) DESC")
    Page<Product> findMostReviewedProducts(Pageable pageable);

    /**
     * Find recently added products.
     * 
     * @param pageable pagination information
     * @return page of recently added products
     */
    Page<Product> findByOrderByCreatedAtDesc(Pageable pageable);
}
