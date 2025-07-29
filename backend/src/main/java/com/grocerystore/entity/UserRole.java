package com.grocerystore.entity;

/**
 * Enumeration representing user roles in the system.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
public enum UserRole {
    /**
     * Customer role - can browse products, place orders, write reviews
     */
    CUSTOMER,
    
    /**
     * Administrator role - can manage products, users, and orders
     */
    ADMIN
}
