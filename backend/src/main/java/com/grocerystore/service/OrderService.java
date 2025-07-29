package com.grocerystore.service;

import com.grocerystore.dto.OrderCreateDTO;
import com.grocerystore.dto.OrderDTO;
import com.grocerystore.dto.OrderItemDTO;
import com.grocerystore.entity.*;
import com.grocerystore.exception.ResourceNotFoundException;
import com.grocerystore.exception.ValidationException;
import com.grocerystore.repository.OrderRepository;
import com.grocerystore.repository.ProductRepository;
import com.grocerystore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing orders.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, 
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new order for a customer.
     */
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO, UUID customerId) {
        // Validate customer exists
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // Validate order items
        if (orderCreateDTO.getOrderItems() == null || orderCreateDTO.getOrderItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }

        // Create order
        Order order = new Order();
        order.setUser(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setDeliveryAddress(orderCreateDTO.getDeliveryAddress());
        order.setContactNumber(orderCreateDTO.getContactNumber());
        order.setOrderNotes(orderCreateDTO.getOrderNotes());
        
        // Set estimated delivery date (3 days from now)
        order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(3));

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process order items
        for (OrderItemDTO itemDTO : orderCreateDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            // Check stock availability
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new ValidationException("Insufficient stock for product: " + product.getName() + 
                                            ". Available: " + product.getStockQuantity() + 
                                            ", Requested: " + itemDTO.getQuantity());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.getOrderItems().add(orderItem);

            // Calculate total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    /**
     * Get order history for a customer.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getCustomerOrders(UUID customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(customerId, pageable);
        return orders.map(this::convertToDTO);
    }

    /**
     * Get all orders (admin only).
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByOrderByOrderDateDesc(pageable);
        return orders.map(this::convertToDTO);
    }

    /**
     * Get order by ID.
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Update order status (admin only).
     */
    public OrderDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
        
        // Set actual delivery date if order is delivered
        if (newStatus == OrderStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    /**
     * Get order statistics (admin only).
     */
    @Transactional(readOnly = true)
    public OrderStatsDTO getOrderStatistics() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long deliveredOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return new OrderStatsDTO(totalOrders, pendingOrders, processingOrders, 
                                deliveredOrders, cancelledOrders, totalRevenue);
    }

    /**
     * Convert Order entity to DTO.
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getUser().getId());
        dto.setCustomerName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        dto.setCustomerEmail(order.getUser().getEmail());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setContactNumber(order.getContactNumber());
        dto.setOrderNotes(order.getOrderNotes());
        dto.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        dto.setActualDeliveryDate(order.getActualDeliveryDate());

        // Convert order items
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDTOs);

        return dto;
    }

    /**
     * Convert OrderItem entity to DTO.
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setProductImage(orderItem.getProduct().getImageUrl());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setSubtotal(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return dto;
    }

    /**
     * Inner class for order statistics.
     */
    public static class OrderStatsDTO {
        private long totalOrders;
        private long pendingOrders;
        private long processingOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private BigDecimal totalRevenue;

        public OrderStatsDTO(long totalOrders, long pendingOrders, long processingOrders,
                           long deliveredOrders, long cancelledOrders, BigDecimal totalRevenue) {
            this.totalOrders = totalOrders;
            this.pendingOrders = pendingOrders;
            this.processingOrders = processingOrders;
            this.deliveredOrders = deliveredOrders;
            this.cancelledOrders = cancelledOrders;
            this.totalRevenue = totalRevenue;
        }

        // Getters and setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
        public long getProcessingOrders() { return processingOrders; }
        public void setProcessingOrders(long processingOrders) { this.processingOrders = processingOrders; }
        public long getDeliveredOrders() { return deliveredOrders; }
        public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}
