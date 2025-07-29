package com.grocerystore.controller;

import com.grocerystore.dto.common.ApiResponse;
import com.grocerystore.dto.common.PageResponse;
import com.grocerystore.dto.order.OrderCreateDTO;
import com.grocerystore.dto.order.OrderDTO;
import com.grocerystore.entity.OrderStatus;
import com.grocerystore.security.UserPrincipal;
import com.grocerystore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing orders.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order.
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody OrderCreateDTO orderCreateDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        OrderDTO createdOrder = orderService.createOrder(orderCreateDTO, userPrincipal.getId());
        
        ApiResponse<OrderDTO> response = new ApiResponse<>(
            true,
            "Order created successfully",
            createdOrder
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get customer's order history.
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderDTO> orders = orderService.getCustomerOrders(userPrincipal.getId(), pageable);
        
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(
            orders.getContent(),
            orders.getNumber(),
            orders.getSize(),
            orders.getTotalElements(),
            orders.getTotalPages(),
            orders.isLast()
        );
        
        ApiResponse<PageResponse<OrderDTO>> response = new ApiResponse<>(
            true,
            "Orders retrieved successfully",
            pageResponse
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get order by ID (customer can only access their own orders).
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        OrderDTO order = orderService.getOrderById(orderId);
        
        // Check if customer is trying to access their own order
        if (userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"))) {
            if (!order.getCustomerId().equals(userPrincipal.getId())) {
                ApiResponse<OrderDTO> response = new ApiResponse<>(
                    false,
                    "Access denied: You can only view your own orders",
                    null
                );
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }
        
        ApiResponse<OrderDTO> response = new ApiResponse<>(
            true,
            "Order retrieved successfully",
            order
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders (admin only).
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(
            orders.getContent(),
            orders.getNumber(),
            orders.getSize(),
            orders.getTotalElements(),
            orders.getTotalPages(),
            orders.isLast()
        );
        
        ApiResponse<PageResponse<OrderDTO>> response = new ApiResponse<>(
            true,
            "All orders retrieved successfully",
            pageResponse
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status (admin only).
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {
        
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        
        ApiResponse<OrderDTO> response = new ApiResponse<>(
            true,
            "Order status updated successfully",
            updatedOrder
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get order statistics (admin only).
     */
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderService.OrderStatsDTO>> getOrderStatistics() {
        
        OrderService.OrderStatsDTO stats = orderService.getOrderStatistics();
        
        ApiResponse<OrderService.OrderStatsDTO> response = new ApiResponse<>(
            true,
            "Order statistics retrieved successfully",
            stats
        );
        
        return ResponseEntity.ok(response);
    }
}
