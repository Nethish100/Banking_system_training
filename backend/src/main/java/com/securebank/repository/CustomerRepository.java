package com.securebank.repository;

import com.securebank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by email
     * @param email the email to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if email exists for a different customer
     * @param email the email to check
     * @param customerId the customer ID to exclude
     * @return true if email exists for different customer
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.email = :email AND c.customerId != :customerId")
    boolean existsByEmailAndCustomerIdNot(@Param("email") String email, @Param("customerId") Long customerId);
    
    /**
     * Find customers by name containing (case-insensitive)
     * @param name the name to search for
     * @return List of customers with matching names
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find customers created after a certain date
     * @param date the date to filter by
     * @return List of customers created after the date
     */
    List<Customer> findByCreatedDateAfter(LocalDateTime date);
    
    /**
     * Count total number of customers
     * @return total count of customers
     */
    @Query("SELECT COUNT(c) FROM Customer c")
    long countCustomers();
    
    /**
     * Find customers ordered by creation date descending
     * @return List of customers ordered by newest first
     */
    @Query("SELECT c FROM Customer c ORDER BY c.createdDate DESC")
    List<Customer> findAllOrderByCreatedDateDesc();
    
    /**
     * Find customers with accounts
     * @return List of customers who have at least one account
     */
    @Query("SELECT DISTINCT c FROM Customer c WHERE SIZE(c.accounts) > 0")
    List<Customer> findCustomersWithAccounts();
    
    /**
     * Find customers without accounts
     * @return List of customers who have no accounts
     */
    @Query("SELECT c FROM Customer c WHERE SIZE(c.accounts) = 0")
    List<Customer> findCustomersWithoutAccounts();
}