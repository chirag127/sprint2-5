package com.grocerystore.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating a new order.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
public class OrderCreateDTO {

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> orderItems;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 10, max = 500, message = "Delivery address must be between 10 and 500 characters")
    private String deliveryAddress;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Contact number must be a valid phone number")
    private String contactNumber;

    @Size(max = 500, message = "Order notes cannot exceed 500 characters")
    private String orderNotes;

    // Constructors
    public OrderCreateDTO() {}

    public OrderCreateDTO(List<OrderItemDTO> orderItems, String deliveryAddress, String contactNumber) {
        this.orderItems = orderItems;
        this.deliveryAddress = deliveryAddress;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters
    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "orderItems=" + orderItems +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", orderNotes='" + orderNotes + '\'' +
                '}';
    }
}
