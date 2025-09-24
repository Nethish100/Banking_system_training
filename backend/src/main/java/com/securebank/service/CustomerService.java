package com.securebank.service;

import com.securebank.entity.Customer;
import com.securebank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAllOrderByCreatedDateDesc();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public Customer createCustomer(Customer customer) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }

        customer.setCreatedDate(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer existingCustomer = getCustomerById(id);

        // Check if email already exists for a different customer
        if (customerRepository.existsByEmailAndCustomerIdNot(customerDetails.getEmail(), id)) {
            throw new IllegalArgumentException("Email already exists: " + customerDetails.getEmail());
        }

        existingCustomer.setName(customerDetails.getName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setMobileNumber(customerDetails.getMobileNumber());
        existingCustomer.setAddress(customerDetails.getAddress());
        existingCustomer.setUpdatedDate(LocalDateTime.now());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        
        // Check if customer has accounts
        if (!customer.getAccounts().isEmpty()) {
            throw new IllegalStateException("Cannot delete customer with existing accounts");
        }
        
        customerRepository.delete(customer);
    }

    public long getCustomerCount() {
        return customerRepository.countCustomers();
    }

    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public List<Customer> getCustomersWithAccounts() {
        return customerRepository.findCustomersWithAccounts();
    }

    public List<Customer> getCustomersWithoutAccounts() {
        return customerRepository.findCustomersWithoutAccounts();
    }

    public List<Customer> getRecentCustomers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return customerRepository.findByCreatedDateAfter(cutoffDate);
    }
}