package com.grocerystore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for the Online Grocery Ordering System.
 * 
 * This application provides a comprehensive platform for customers to browse,
 * search, and order groceries online, with administrative capabilities for
 * managing products, users, and orders.
 * 
 * Key Features:
 * - User authentication and authorization with JWT
 * - Product catalog management
 * - Shopping cart and order processing
 * - Product reviews and ratings
 * - Admin dashboard and management tools
 * - Email notifications
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 * @since 2025-07-29
 */
@SpringBootApplication
@EnableJpaAuditing
public class GroceryStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroceryStoreApplication.class, args);
    }

}
