package com.securebank.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account entity representing bank accounts
 * As per Banking System Requirements:
 * - Account No (auto-generated)
 * - Account Holder Name  
 * - Account Balance
 * - Type of Account (Saving, Current etc.)
 */
@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @Column(name = "account_no", length = 20)
    private String accountNo;
    
    @Column(name = "account_holder_name", nullable = false, length = 100)
    @NotBlank(message = "Account holder name is required")
    @Size(min = 2, max = 100, message = "Account holder name must be between 2 and 100 characters")
    private String accountHolderName;
    
    @Column(name = "account_balance", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Account balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Account balance must be non-negative")
    private BigDecimal accountBalance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;
    
// inside Account.java

// Account Type Enum - Updated as per requirements
public enum AccountType {
    SAVINGS("Savings Account"),
    CURRENT("Current Account"),
    CHECKING("Checking Account"),
    BUSINESS("Business Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // ✅ Custom fromString to handle legacy DB values
    @com.fasterxml.jackson.annotation.JsonCreator
    public static AccountType fromString(String value) {
        if (value == null) return null;
        switch (value.toUpperCase()) {
            case "SAVINGS":
            case "SAVING":    // legacy
                return SAVINGS;
            case "CURRENT":
                return CURRENT;
            case "CHECKING":
                return CHECKING;
            case "BUSINESS":
                return BUSINESS;
            default:
                throw new IllegalArgumentException("Unknown account type: " + value);
        }
    }

    // ✅ Always serialize to "SAVING" or "CURRENT"
    @com.fasterxml.jackson.annotation.JsonValue
    public String toValue() {
        return this.name();
    }
}

    
    // Constructors
    public Account() {}
    
    public Account(String accountNo, String accountHolderName, BigDecimal accountBalance, 
                   AccountType accountType, Customer customer) {
        this.accountNo = accountNo;
        this.accountHolderName = accountHolderName;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        this.customer = customer;
    }
    
    // Getters and Setters
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public String getAccountHolderName() {
        return accountHolderName;
    }
    
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    // Helper method to get customer ID for JSON serialization
    public Long getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }
    
    // Helper methods for account operations
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.accountBalance = this.accountBalance.add(amount);
            this.updatedDate = LocalDateTime.now();
        }
    }
    
    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && 
            this.accountBalance.compareTo(amount) >= 0) {
            this.accountBalance = this.accountBalance.subtract(amount);
            this.updatedDate = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountNo='" + accountNo + '\'' +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountType=" + accountType +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", customerId=" + (customer != null ? customer.getCustomerId() : null) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return accountNo != null && accountNo.equals(account.accountNo);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
