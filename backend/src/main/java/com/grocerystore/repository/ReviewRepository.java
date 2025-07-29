package com.grocerystore.repository;

import com.grocerystore.entity.Product;
import com.grocerystore.entity.Review;
import com.grocerystore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Review entity operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find reviews by product.
     * 
     * @param product the product
     * @return list of reviews for the product
     */
    List<Review> findByProduct(Product product);

    /**
     * Find reviews by product with pagination.
     * 
     * @param product the product
     * @param pageable pagination information
     * @return page of reviews for the product
     */
    Page<Review> findByProduct(Product product, Pageable pageable);

    /**
     * Find reviews by product ID.
     * 
     * @param productId the product ID
     * @return list of reviews for the product
     */
    List<Review> findByProductId(UUID productId);

    /**
     * Find reviews by product ID with pagination.
     * 
     * @param productId the product ID
     * @param pageable pagination information
     * @return page of reviews for the product
     */
    Page<Review> findByProductId(UUID productId, Pageable pageable);

    /**
     * Find reviews by user.
     * 
     * @param user the user
     * @return list of reviews by the user
     */
    List<Review> findByUser(User user);

    /**
     * Find reviews by user with pagination.
     * 
     * @param user the user
     * @param pageable pagination information
     * @return page of reviews by the user
     */
    Page<Review> findByUser(User user, Pageable pageable);

    /**
     * Find reviews by user ID.
     * 
     * @param userId the user ID
     * @return list of reviews by the user
     */
    List<Review> findByUserId(UUID userId);

    /**
     * Find review by product and user (should be unique).
     * 
     * @param product the product
     * @param user the user
     * @return optional review by the user for the product
     */
    Optional<Review> findByProductAndUser(Product product, User user);

    /**
     * Find review by product ID and user ID.
     * 
     * @param productId the product ID
     * @param userId the user ID
     * @return optional review by the user for the product
     */
    Optional<Review> findByProductIdAndUserId(UUID productId, UUID userId);

    /**
     * Check if a user has reviewed a specific product.
     * 
     * @param product the product
     * @param user the user
     * @return true if the user has reviewed the product, false otherwise
     */
    boolean existsByProductAndUser(Product product, User user);

    /**
     * Check if a user has reviewed a specific product by IDs.
     * 
     * @param productId the product ID
     * @param userId the user ID
     * @return true if the user has reviewed the product, false otherwise
     */
    boolean existsByProductIdAndUserId(UUID productId, UUID userId);

    /**
     * Find reviews by rating.
     * 
     * @param rating the rating
     * @return list of reviews with the specified rating
     */
    List<Review> findByRating(Integer rating);

    /**
     * Find reviews by rating with pagination.
     * 
     * @param rating the rating
     * @param pageable pagination information
     * @return page of reviews with the specified rating
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);

    /**
     * Find reviews by product and rating.
     * 
     * @param product the product
     * @param rating the rating
     * @return list of reviews for the product with the specified rating
     */
    List<Review> findByProductAndRating(Product product, Integer rating);

    /**
     * Find reviews with rating greater than or equal to the specified value.
     * 
     * @param rating the minimum rating
     * @return list of reviews with rating >= specified value
     */
    List<Review> findByRatingGreaterThanEqual(Integer rating);

    /**
     * Find reviews with rating less than or equal to the specified value.
     * 
     * @param rating the maximum rating
     * @return list of reviews with rating <= specified value
     */
    List<Review> findByRatingLessThanEqual(Integer rating);

    /**
     * Count reviews by product.
     * 
     * @param product the product
     * @return count of reviews for the product
     */
    long countByProduct(Product product);

    /**
     * Count reviews by user.
     * 
     * @param user the user
     * @return count of reviews by the user
     */
    long countByUser(User user);

    /**
     * Calculate average rating for a product.
     * 
     * @param product the product
     * @return average rating for the product
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double calculateAverageRating(@Param("product") Product product);

    /**
     * Calculate average rating for a product by ID.
     * 
     * @param productId the product ID
     * @return average rating for the product
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double calculateAverageRatingById(@Param("productId") UUID productId);

    /**
     * Find recent reviews ordered by creation date.
     * 
     * @param pageable pagination information
     * @return page of recent reviews
     */
    Page<Review> findByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find reviews by product ordered by creation date.
     * 
     * @param product the product
     * @param pageable pagination information
     * @return page of reviews for the product ordered by date
     */
    Page<Review> findByProductOrderByCreatedAtDesc(Product product, Pageable pageable);

    /**
     * Find reviews by product ID ordered by creation date.
     * 
     * @param productId the product ID
     * @param pageable pagination information
     * @return page of reviews for the product ordered by date
     */
    Page<Review> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    /**
     * Find reviews with comments (non-null and non-empty).
     * 
     * @return list of reviews with comments
     */
    @Query("SELECT r FROM Review r WHERE r.comment IS NOT NULL AND TRIM(r.comment) != ''")
    List<Review> findReviewsWithComments();

    /**
     * Find reviews with comments with pagination.
     * 
     * @param pageable pagination information
     * @return page of reviews with comments
     */
    @Query("SELECT r FROM Review r WHERE r.comment IS NOT NULL AND TRIM(r.comment) != ''")
    Page<Review> findReviewsWithComments(Pageable pageable);

    /**
     * Find reviews by product with comments.
     * 
     * @param product the product
     * @return list of reviews for the product with comments
     */
    @Query("SELECT r FROM Review r WHERE r.product = :product AND r.comment IS NOT NULL AND TRIM(r.comment) != ''")
    List<Review> findByProductWithComments(@Param("product") Product product);

    /**
     * Get rating distribution for a product.
     * 
     * @param productId the product ID
     * @return list of objects containing rating and count
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution(@Param("productId") UUID productId);
}
