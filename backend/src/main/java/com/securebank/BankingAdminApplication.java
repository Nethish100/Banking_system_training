package com.securebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for Banking Administration System
 * 
 * @author SecureBank Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class BankingAdminApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BankingAdminApplication.class, args);
        System.out.println("Banking Administration System Started Successfully!");
        System.out.println("Access the application at: http://localhost:8080");
    }
}