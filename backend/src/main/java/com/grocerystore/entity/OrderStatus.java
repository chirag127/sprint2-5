package com.grocerystore.entity;

/**
 * Enumeration representing order status in the system.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
public enum OrderStatus {
    /**
     * Order has been placed but not yet processed
     */
    PENDING,
    
    /**
     * Order is being processed/prepared
     */
    PROCESSING,
    
    /**
     * Order has been completed and delivered
     */
    COMPLETED,
    
    /**
     * Order has been cancelled
     */
    CANCELLED
}
