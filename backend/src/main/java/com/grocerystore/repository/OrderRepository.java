package com.grocerystore.repository;

import com.grocerystore.entity.Order;
import com.grocerystore.entity.OrderStatus;
import com.grocerystore.entity.User;
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
 * Repository interface for Order entity operations.
 *
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find orders by user.
     *
     * @param user the user
     * @return list of orders for the user
     */
    List<Order> findByUser(User user);

    /**
     * Find orders by user with pagination.
     *
     * @param user the user
     * @param pageable pagination information
     * @return page of orders for the user
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * Find orders by user ID.
     *
     * @param userId the user ID
     * @return list of orders for the user
     */
    List<Order> findByUserId(UUID userId);

    /**
     * Find orders by user ID with pagination.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of orders for the user
     */
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find orders by status.
     *
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by status with pagination.
     *
     * @param status the order status
     * @param pageable pagination information
     * @return page of orders with the specified status
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Find orders by user and status.
     *
     * @param user the user
     * @param status the order status
     * @return list of orders for the user with the specified status
     */
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    /**
     * Find orders placed between two dates.
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of orders placed between the dates
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders placed between two dates with pagination.
     *
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return page of orders placed between the dates
     */
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find orders placed after a specific date.
     *
     * @param date the date threshold
     * @return list of orders placed after the specified date
     */
    List<Order> findByOrderDateAfter(LocalDateTime date);

    /**
     * Find orders with total amount greater than or equal to the specified amount.
     *
     * @param amount the minimum amount
     * @return list of orders with total amount >= specified amount
     */
    List<Order> findByTotalAmountGreaterThanEqual(BigDecimal amount);

    /**
     * Count orders by status.
     *
     * @param status the order status
     * @return count of orders with the specified status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders by user.
     *
     * @param user the user
     * @return count of orders for the user
     */
    long countByUser(User user);

    /**
     * Calculate total revenue from completed orders.
     *
     * @return total revenue from completed orders
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    /**
     * Calculate total revenue from completed orders.
     *
     * @return total revenue from completed orders
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal calculateTotalRevenue();

    /**
     * Calculate total revenue for a specific period.
     *
     * @param startDate start date
     * @param endDate end date
     * @return total revenue for the period
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find all orders ordered by order date descending.
     *
     * @param pageable pagination information
     * @return page of recent orders
     */
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);

    /**
     * Find recent orders (ordered by order date descending).
     *
     * @param pageable pagination information
     * @return page of recent orders
     */
    Page<Order> findByOrderByOrderDateDesc(Pageable pageable);

    /**
     * Find orders by user ordered by order date descending.
     *
     * @param user the user
     * @param pageable pagination information
     * @return page of user's orders ordered by date
     */
    Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);

    /**
     * Find orders by user ID ordered by order date descending.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of user's orders ordered by date
     */
    Page<Order> findByUserIdOrderByOrderDateDesc(UUID userId, Pageable pageable);

    /**
     * Get monthly order statistics.
     *
     * @param year the year
     * @param month the month (1-12)
     * @return count of orders for the specified month
     */
    @Query("SELECT COUNT(o) FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    long getMonthlyOrderCount(@Param("year") int year, @Param("month") int month);

    /**
     * Get daily order statistics.
     *
     * @param date the date
     * @return count of orders for the specified date
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = DATE(:date)")
    long getDailyOrderCount(@Param("date") LocalDateTime date);

    /**
     * Find top customers by order count.
     *
     * @param pageable pagination information
     * @return page of users ordered by their order count
     */
    @Query("SELECT o.user FROM Order o " +
           "GROUP BY o.user " +
           "ORDER BY COUNT(o.id) DESC")
    Page<User> findTopCustomersByOrderCount(Pageable pageable);

    /**
     * Find top customers by total spending.
     *
     * @param pageable pagination information
     * @return page of users ordered by their total spending
     */
    @Query("SELECT o.user FROM Order o " +
           "WHERE o.status = 'COMPLETED' " +
           "GROUP BY o.user " +
           "ORDER BY SUM(o.totalAmount) DESC")
    Page<User> findTopCustomersBySpending(Pageable pageable);
}
