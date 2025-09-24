package com.securebank.controller;

import com.securebank.entity.Customer;
import com.securebank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Customer Controller - Handles Customer Operations
 * As per Banking System Requirements:
 * 1. ADD a new Customer [ID must be auto-generated]
 * 2. Retrieve a Customer against its ID
 * 3. Update the details of Customer except ID
 * 
 * Note: DELETE operation removed as not mentioned in requirements
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieve a Customer against its ID
     * Requirement: "Retrieve a Customer against its ID"
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving customer: " + e.getMessage());
        }
    }

    /**
     * ADD a new Customer [ID must be auto-generated]
     * Requirement: "ADD a new Customer [ID must be auto-generated]"
     */
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            // Ensure ID is not set (will be auto-generated)
            customer.setCustomerId(null);
            
            Customer savedCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating customer: " + e.getMessage());
        }
    }

    /**
     * Update the details of Customer except ID
     * Requirement: "Update the details of Customer except ID"
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        try {
            // Ensure the customer ID cannot be changed
            customer.setCustomerId(id);
            
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found with ID: " + id);
        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating customer: " + e.getMessage());
        }
    }

    /**
     * NOTE: DELETE operation removed as it's not mentioned in the requirements
     * Requirements only specify:
     * 1. ADD a new Customer
     * 2. Retrieve a Customer against its ID  
     * 3. Update the details of Customer except ID
     */

    /**
     * Get customer count for dashboard statistics
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCustomerCount() {
        try {
            long count = customerService.getCustomerCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }

    /**
     * Search customers by name (additional utility function)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.searchCustomersByName(name);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Check if customer exists (utility function)
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> customerExists(@PathVariable Long id) {
        try {
            customerService.getCustomerById(id);
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }
}
