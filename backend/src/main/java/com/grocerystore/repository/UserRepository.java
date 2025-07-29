package com.grocerystore.repository;

import com.grocerystore.entity.User;
import com.grocerystore.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email address.
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email address.
     * 
     * @param email the email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role.
     * 
     * @param role the user role
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find users by role with pagination.
     * 
     * @param role the user role
     * @param pageable pagination information
     * @return page of users with the specified role
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Search users by full name containing the search term (case-insensitive).
     * 
     * @param name the search term
     * @return list of users whose full name contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search users by full name containing the search term with pagination (case-insensitive).
     * 
     * @param name the search term
     * @param pageable pagination information
     * @return page of users whose full name contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find users created after a specific date.
     * 
     * @param date the date threshold
     * @return list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Count users by role.
     * 
     * @param role the user role
     * @return count of users with the specified role
     */
    long countByRole(UserRole role);

    /**
     * Count total number of customers.
     * 
     * @return count of customers
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'")
    long countCustomers();

    /**
     * Count total number of admins.
     * 
     * @return count of admins
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    long countAdmins();

    /**
     * Find users who have placed orders.
     * 
     * @return list of users who have at least one order
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();

    /**
     * Find users who have written reviews.
     * 
     * @return list of users who have at least one review
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.reviews r")
    List<User> findUsersWithReviews();

    /**
     * Search users by email or full name containing the search term (case-insensitive).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
}
