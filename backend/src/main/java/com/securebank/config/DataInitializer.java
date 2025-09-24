package com.securebank.config;

import com.securebank.entity.Account;
import com.securebank.entity.Customer;
import com.securebank.entity.User;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.CustomerRepository;
import com.securebank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Initializer for Banking System
 * Creates sample data ONCE ONLY, and does NOT touch data after that
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPasswordHash(passwordEncoder.encode("password"));
            adminUser.setRole("ADMIN");
            adminUser.setCreatedDate(LocalDateTime.now());
            adminUser.setIsActive(true);
            userRepository.save(adminUser);
            System.out.println("✅ Default admin user created: admin/password");
        }

        // Add additional users as needed
        // Example: Create custom user
        if (!userRepository.existsByUsername("manager")) {
            User managerUser = new User();
            managerUser.setUsername("manager");
            managerUser.setPasswordHash(passwordEncoder.encode("manager123"));
            managerUser.setRole("ADMIN");
            managerUser.setCreatedDate(LocalDateTime.now());
            managerUser.setIsActive(true);
            userRepository.save(managerUser);
            System.out.println("✅ Manager user created: manager/manager123");
        }

        // ONLY initialize demo data if there are NO customers/accounts
        boolean needSamples = (customerRepository.count() == 0 && accountRepository.count() == 0);

        if (needSamples) {
            createSampleCustomers();
            createSampleAccounts();
            System.out.println("✅ Sample customers and accounts created");
        } else {
            System.out.println("ℹ️ Persistent data mode: NO RESET. User data will remain across restarts.");
        }

        System.out.println("🎉 Banking System data initialization completed!");
        System.out.println("📊 System Status:");
        System.out.println("   Users: " + userRepository.count());
        System.out.println("   Customers: " + customerRepository.count());
        System.out.println("   Accounts: " + accountRepository.count());
    }

    private void createSampleCustomers() {
        Customer customer1 = new Customer(
            "John Smith",
            "john.smith@email.com",
            "+1-555-0123",
            "123 Main Street, Springfield, IL 62701, USA"
        );

        Customer customer2 = new Customer(
            "Sarah Johnson",
            "sarah.johnson@email.com", 
            "+1-555-0124",
            "456 Oak Avenue, Madison, WI 53703, USA"
        );

        Customer customer3 = new Customer(
            "Michael Brown",
            "michael.brown@email.com",
            "+1-555-0125", 
            "789 Pine Road, Austin, TX 78701, USA"
        );

        Customer customer4 = new Customer(
            "Emily Davis",
            "emily.davis@email.com",
            "+1-555-0126",
            "321 Elm Street, Denver, CO 80202, USA"
        );

        Customer customer5 = new Customer(
            "David Wilson",
            "david.wilson@email.com",
            "+1-555-0127",
            "654 Maple Drive, Seattle, WA 98101, USA"
        );

        Customer customer6 = new Customer(
            "Lisa Anderson",
            "lisa.anderson@email.com",
            "+1-555-0128",
            "987 Cedar Lane, Miami, FL 33101, USA"
        );

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);
        customerRepository.save(customer5);
        customerRepository.save(customer6);
    }

    private void createSampleAccounts() {
        // Get all customers
        var customers = customerRepository.findAll();
        if (customers.isEmpty()) return;

        Account account1 = new Account(
            "ACC000001",
            "John Smith",
            new BigDecimal("15000.00"),
            Account.AccountType.SAVINGS,
            customers.get(0)
        );
        Account account2 = new Account(
            "ACC000002",
            "Sarah Johnson",
            new BigDecimal("8500.00"),
            Account.AccountType.CURRENT,  
            customers.get(1)
        );
        Account account3 = new Account(
            "ACC000003",
            "Michael Brown",
            new BigDecimal("25000.00"),
            Account.AccountType.CURRENT,
            customers.get(2)
        );
        Account account4 = new Account(
            "ACC000004",
            "Emily Davis",
            new BigDecimal("12750.50"),
            Account.AccountType.SAVINGS,
            customers.get(3)
        );
        Account account5 = new Account(
            "ACC000005",
            "David Wilson",
            new BigDecimal("5500.75"),
            Account.AccountType.CURRENT,
            customers.get(4)
        );
        Account account6 = new Account(
            "ACC000006",
            "John Smith",
            new BigDecimal("3200.00"),
            Account.AccountType.SAVINGS,
            customers.get(0)
        );
        Account account7 = new Account(
            "ACC000007",
            "Lisa Anderson",
            new BigDecimal("18000.25"),
            Account.AccountType.SAVINGS,
            customers.get(5)
        );
        Account account8 = new Account(
            "ACC000008",
            "Lisa Anderson",
            new BigDecimal("7500.00"),
            Account.AccountType.CURRENT,
            customers.get(5)
        );
        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);
        accountRepository.save(account4);
        accountRepository.save(account5);
        accountRepository.save(account6);
        accountRepository.save(account7);
        accountRepository.save(account8);
    }
}
